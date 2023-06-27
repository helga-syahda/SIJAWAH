package propensi.SIJAWAH.controller;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import propensi.SIJAWAH.model.*;
import propensi.SIJAWAH.service.JumlahService;
import propensi.SIJAWAH.service.PelangganService;
import propensi.SIJAWAH.service.ProdukService;
import propensi.SIJAWAH.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/pelanggan")
public class PelangganController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProdukService produkService;

    @Autowired
    private PelangganService pelangganService;

    @Autowired
    private JumlahService jumlahService;

    @GetMapping("/add")
    public String addPelangganManual(HttpServletRequest request, Model model) {
        String role = request.getUserPrincipal().getName();
        List<ProdukModel> listProduk = produkService.getListProduk();
        model.addAttribute("pelanggan", new PelangganModel());
        model.addAttribute("listProduk", listProduk);
        return "form-pelanggan";
    }

    @PostMapping(value = "/add", params = { "addRowProduk" })
    private String addRowProduk(@ModelAttribute PelangganModel pelanggan, BindingResult bindingResult, Model model) {
        if (pelanggan.getListJumlah() == null || pelanggan.getListJumlah().size() == 0) {
            pelanggan.setListJumlah(new ArrayList<>());
        }
        pelanggan.getListJumlah().add(new JumlahModel());
        List<ProdukModel> listProduk = produkService.getListProduk();
        model.addAttribute("pelanggan", pelanggan);
        model.addAttribute("listProduk", listProduk);
        return "form-pelanggan";
    }

    @PostMapping(value = "/add", params = { "deleteRowProduk" })
    private String deleteRowProduk(@ModelAttribute PelangganModel pelanggan, @RequestParam("deleteRowProduk") Integer row, Model model) {
        if (pelanggan.getListJumlah() == null || pelanggan.getListJumlah().size() == 0) {
            pelanggan.setListJumlah(new ArrayList<>());
        }

        pelanggan.getListJumlah().remove(row.intValue());
        List<ProdukModel> listProduk = produkService.getListProduk();
        model.addAttribute("pelanggan", pelanggan);
        model.addAttribute("listProduk", listProduk);
        return "form-pelanggan";
    }

    @PostMapping(value = "/add", params = { "save" })
    public String addPelangganSubmit(@ModelAttribute PelangganModel pelanggan, Model model, RedirectAttributes redirAttrs) {
        if(pelanggan.getListJumlah() == null) {
            pelanggan.setListJumlah(new ArrayList<>());
        }
        Set<String> setProduk = new HashSet<>();
        for (JumlahModel jumlah : pelanggan.getListJumlah()) {
            setProduk.add(jumlah.getProduk().getUuid());
        }
        if (setProduk.size() < pelanggan.getListJumlah().size()) {
            model.addAttribute("error", "Terdapat produk yang duplikat di list");
            List<ProdukModel> listProduk = produkService.getListProduk();
            model.addAttribute("pelanggan", pelanggan);
            model.addAttribute("listProduk", listProduk);
            return "form-pelanggan";
        }

        if(pelangganService.getPelanggan(pelanggan.getNomorTelepon())!=null) {
            model.addAttribute("error", "Nomor telepon sudah terdaftar");
            List<ProdukModel> listProduk = produkService.getListProduk();
            model.addAttribute("pelanggan", pelanggan);
            model.addAttribute("listProduk", listProduk);
            return "form-pelanggan";
        }

        for (JumlahModel temp: pelanggan.getListJumlah()) {
            temp.setPelanggan(pelanggan);
        }
        pelanggan.setLastUpdate(LocalDateTime.now());
        pelangganService.addPelanggan(pelanggan);
        redirAttrs.addFlashAttribute("success", "Pelanggan berhasil ditambahkan");
        return "redirect:/pelanggan/viewall";
    }

    @GetMapping("/file-add")
    public String addPelangganFile() {
        return "upload-pelanggan";
    }


    @PostMapping("/file-add")
    public String uploadCSVFile(@RequestParam("file") MultipartFile file, Model model) {

        // validate file
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a CSV file to upload.");
            model.addAttribute("status", false);
        } else {

            // parse CSV file to create a list of `User` objects
            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {


                CSVReader csvReader = new CSVReader(reader);
                String[] record;
                boolean header = true;
                while ((record = csvReader.readNext()) != null) {
                    if (header) {
                        header = false;
                        continue;
                    }
                    String nama = record[1];
                    String nomorTelepon = record[3];
                    String namaProduk = record[7];
                    String jumlahProduk = record[9];

                    PelangganModel pelanggan = pelangganService.getPelanggan(nomorTelepon);
                    if (pelanggan==null) {
                        pelanggan = new PelangganModel();
                        pelanggan.setNama(nama);
                        pelanggan.setNomorTelepon(nomorTelepon);
                        pelanggan.setJumlahPembelian(1);
                    } else {
                        pelanggan.setJumlahPembelian(pelanggan.getJumlahPembelian()+1);
                    }
                    pelanggan.setLastUpdate(LocalDateTime.now());

                    ProdukModel produk = produkService.getProduk(namaProduk);

                    JumlahModel jumlah = new JumlahModel();
                    jumlah.setPelanggan(pelanggan);
                    jumlah.setProduk(produk);
                    jumlah.setKuantitas(Integer.parseInt(jumlahProduk));
                    JumlahModel jumlahExist = jumlahService.isExist(jumlah);
                    if (jumlahExist != null) {
                        jumlahExist.setKuantitas(jumlahExist.getKuantitas()+jumlah.getKuantitas());
                    } else {
                        if(pelanggan.getListJumlah() == null) {
                            pelanggan.setListJumlah(new ArrayList<>());
                        }
                        pelanggan.getListJumlah().add(jumlah);
                    }


                    pelangganService.addPelanggan(pelanggan);
                }

            } catch (Exception ex) {
                model.addAttribute("error", "An error occurred while processing the CSV file.");
                model.addAttribute("status", false);
            }
        }

        return "redirect:/pelanggan/viewall";
    }


    @GetMapping("/viewall")
    public String listPelanggan(@ModelAttribute PelangganModel pelanggan, Model model) {
        List<PelangganModel> listPelanggan = pelangganService.getListPelanggan();
        model.addAttribute("listPelanggan", listPelanggan);
        return "viewall-pelanggan";
    }

    @GetMapping("/update/{uuid}")
    public String updatePelanggan(@PathVariable String uuid, Model model) {
        PelangganModel pelanggan = pelangganService.getPelangganByUUID(uuid);
        List<ProdukModel> listProduk = produkService.getListProduk();

        model.addAttribute("pelanggan", pelanggan);
        model.addAttribute("listProduk", listProduk);
        return "form-update-pelanggan";
    }

    @PostMapping(value="/update", params = {"addRow"})
    private String addRowProdukUpdate(@ModelAttribute PelangganModel pelanggan, Model model) {
        if (pelanggan.getListJumlah() == null || pelanggan.getListJumlah().size()==0){
            pelanggan.setListJumlah(new ArrayList<>());
        }
        pelanggan.getListJumlah().add(new JumlahModel());
        List<ProdukModel> listProduk = produkService.getListProduk();

        model.addAttribute("pelanggan", pelanggan);
        model.addAttribute("listProduk", listProduk);
        return "form-update-pelanggan";
    }

    @PostMapping(value="/update", params = {"deleteRow"})
    private String deleteRowProdukUpdate(@ModelAttribute PelangganModel pelanggan, @RequestParam("deleteRow") Integer row, Model model) {
        final Integer rowId = Integer.valueOf(row);
        pelanggan.getListJumlah().remove(rowId.intValue());

        List<ProdukModel> listProduk = produkService.getListProduk();

        model.addAttribute("pelanggan", pelanggan);
        model.addAttribute("listProduk", listProduk);
        return "form-update-pelanggan";
    }


    @PostMapping(value="/update", params = { "save" })
    public String updatePelangganSubmit(@ModelAttribute PelangganModel pelanggan, Model model, RedirectAttributes redirAttrs) {
        if(pelanggan.getListJumlah() == null) {
            pelanggan.setListJumlah(new ArrayList<>());
        }
        Set<String> setProduk = new HashSet<>();
        for (JumlahModel jumlah : pelanggan.getListJumlah()) {
            setProduk.add(jumlah.getProduk().getUuid());
        }

        if (setProduk.size() < pelanggan.getListJumlah().size()) {
            model.addAttribute("error", "Terdapat produk yang duplikat di list");
            List<ProdukModel> listProduk = produkService.getListProduk();
            model.addAttribute("pelanggan", pelanggan);
            model.addAttribute("listProduk", listProduk);
            return "form-update-pelanggan";
        }

        String nomorTeleponPrev = pelangganService.getPelangganByUUID(pelanggan.getUuid()).getNomorTelepon();
        if(pelangganService.getPelanggan(pelanggan.getNomorTelepon())!=null && !pelanggan.getNomorTelepon().equals(nomorTeleponPrev)) {
            model.addAttribute("error", "Nomor telepon sudah terdaftar");
            List<ProdukModel> listProduk = produkService.getListProduk();
            model.addAttribute("pelanggan", pelanggan);
            model.addAttribute("listProduk", listProduk);
            return "form-update-pelanggan";
        }

        List<JumlahModel> listJumlah = jumlahService.getListJumlah();
        for (JumlahModel jumlah : listJumlah) {
            if(jumlah.getPelanggan().getUuid().equals(pelanggan.getUuid())) {
                jumlahService.removeJumlah(jumlah);
            }
        }

        for (JumlahModel temp: pelanggan.getListJumlah()) {
            if (temp.getPelanggan()==null) {
                temp.setPelanggan(pelanggan);
            }
        }
        pelanggan.setLastUpdate(LocalDateTime.now());

        pelangganService.addPelanggan(pelanggan);
        redirAttrs.addFlashAttribute("success", "Pelanggan berhasil di-update");
        return "redirect:/pelanggan/viewall";
    }

    @GetMapping(value = "/delete/{uuid}")
    public String deletePelanggan(@PathVariable String uuid, Model model){
        pelangganService.deletePelanggan(uuid);
        return "redirect:/pelanggan/viewall";
    }

}
