package propensi.SIJAWAH.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import propensi.SIJAWAH.model.RapatModel;
import propensi.SIJAWAH.model.UserModel;
import propensi.SIJAWAH.service.RapatService;
import propensi.SIJAWAH.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/rapat")
public class RapatController {
    @Autowired
    @Qualifier("rapatServiceImpl")
    private RapatService rapatService;

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;

    @GetMapping("/ajuan/add")
    public String addPengajuanRapatPage(Model model, Principal principal) {
        RapatModel rapat = new RapatModel();
        rapat.setStatus("Menunggu Persetujuan");
        List<UserModel> listPesertaExisting = userService.getUserExceptAdmin();
        List<UserModel> listPesertaNew = new ArrayList<>();

        UserModel picRapat = userService.getUserByUsername(principal.getName());
        rapat.setPic(picRapat);
        if (picRapat.getRole().equals("ceo")) {
            rapat.setStatus("Disetujui");
        } else {
            rapat.setStatus("Menunggu Persetujuan");
        }
        rapat.setListPeserta(listPesertaNew);
        rapat.getListPeserta().add(picRapat);
        rapat.getListPeserta().add(new UserModel());

        model.addAttribute("rapat", rapat);
        model.addAttribute("currentPic", picRapat);
        model.addAttribute("listPesertaExisting", listPesertaExisting);
        return "pengajuan-rapat/form-add-pengajuan-rapat";
    }

    @PostMapping(value = "/ajuan/add", params = {"addRowPeserta"})
    public String addRowPesertaMultiple(@ModelAttribute RapatModel rapat, Model model, Principal principal) {
        List<UserModel> listPesertaRapat = rapat.getListPeserta();
        Set<String> setPeserta = new HashSet<>();
        for (UserModel peserta : listPesertaRapat) {
            setPeserta.add(peserta.getUuid());
        }
        if (setPeserta.size() < listPesertaRapat.size()) {
            model.addAttribute("error", "Terdapat peserta yang duplikat di list");
        } else {
            rapat.getListPeserta().add(new UserModel());
            model.addAttribute("success", "Peserta berhasil ditambahkan ke list");
        }

        List<UserModel> listPesertaExisting = userService.getUserExceptAdmin();
        UserModel picRapat = userService.getUserByUsername(principal.getName());

        model.addAttribute("rapat", rapat);
        model.addAttribute("currentPic", picRapat);
        model.addAttribute("listPesertaExisting", listPesertaExisting);
        return "pengajuan-rapat/form-add-pengajuan-rapat";
    }

    @PostMapping(value = "/ajuan/add", params = {"deleteRowPeserta"})
    public String deleteRowPesertaMultiple(@ModelAttribute RapatModel rapat, @RequestParam("deleteRowPeserta") Integer row, Model model, Principal principal) {
        final Integer rowId = Integer.valueOf(row);
        rapat.getListPeserta().remove(rowId.intValue());

        List<UserModel> listPesertaExisting = userService.getUserExceptAdmin();
        UserModel picRapat = userService.getUserByUsername(principal.getName());

        model.addAttribute("success", "Peserta berhasil dihapus dari list");
        model.addAttribute("rapat", rapat);
        model.addAttribute("currentPic", picRapat);
        model.addAttribute("listPesertaExisting", listPesertaExisting);
        return "pengajuan-rapat/form-add-pengajuan-rapat";
    }


    @PostMapping(value = "/ajuan/add", params = {"save"})
    public String addPengajuanRapatSubmit(@ModelAttribute RapatModel rapat, Model model, Principal principal, RedirectAttributes redirAttrs) {
        UserModel picRapat = userService.getUserByUsername(principal.getName());
        List<UserModel> listPesertaExisting = userService.getUserExceptAdmin();

        List<UserModel> listPesertaRapat = rapat.getListPeserta();
        Set<String> setPeserta = new HashSet<>();
        for (UserModel peserta : listPesertaRapat) {
            setPeserta.add(peserta.getUuid());
        }
        if (setPeserta.size() < listPesertaRapat.size()) {
            model.addAttribute("error", "Terdapat peserta yang duplikat di list");
            model.addAttribute("rapat", rapat);
            model.addAttribute("currentPic", picRapat);
            model.addAttribute("listPesertaExisting", listPesertaExisting);
            return "pengajuan-rapat/form-add-pengajuan-rapat";
        }

        if (picRapat.getRole().equals("ceo") && rapat.getWaktu().minusHours(7).isBefore(LocalDateTime.now().plusDays(3))) {
            // Validasi H+3
            model.addAttribute("error", "Waktu rapat yang diajukan oleh CEO minimal H+3 dari waktu pengajuan");
            model.addAttribute("rapat", rapat);
            model.addAttribute("currentPic", picRapat);
            model.addAttribute("listPesertaExisting", listPesertaExisting);
            return "pengajuan-rapat/form-add-pengajuan-rapat";
        } else if (!picRapat.getRole().equals("ceo") && rapat.getWaktu().minusHours(7).isBefore(LocalDateTime.now().plusDays(7))){
            // Validasi H+3
            model.addAttribute("error", "Waktu rapat yang diajukan oleh karyawan minimal H+7 dari waktu pengajuan");
            model.addAttribute("rapat", rapat);
            model.addAttribute("currentPic", picRapat);
            model.addAttribute("listPesertaExisting", listPesertaExisting);
            return "pengajuan-rapat/form-add-pengajuan-rapat";
        }

        for (UserModel peserta : rapat.getListPeserta()) {
            if (peserta.getListRapat() == null || peserta.getListRapat().size() == 0) {
                peserta.setListRapat(new ArrayList<>());
            }
            peserta.getListRapat().add(rapat);
        }

        if (picRapat.getListPengajuanRapat() == null || picRapat.getListPengajuanRapat().size() == 0) {
            picRapat.setListPengajuanRapat(new ArrayList<>());
        }
        picRapat.getListPengajuanRapat().add(rapat);

        rapatService.addRapat(rapat);
        redirAttrs.addFlashAttribute("success", "Rapat baru berhasil diajukan");
        return "redirect:/rapat/viewall-pengajuan";
    }

    @GetMapping(value = "/ajuan/delete/{uuid}")
    public String deletePengajuanRapat(@PathVariable String uuid, Model model, RedirectAttributes redirAttrs) {
        RapatModel rapat = rapatService.getRapatByUuid(uuid);
        String namaRapat = rapat.getNama();
        if (rapat.getStatus().equals("Menunggu Persetujuan")){
            rapatService.deletePengajuanRapat(rapat);
            model.addAttribute("nama", namaRapat);
            redirAttrs.addFlashAttribute("batal", "Pembatalan pengajuan rapat berhasil dilakukan");
            return "redirect:/rapat/viewall-pengajuan";
        }
        redirAttrs.addFlashAttribute("batal", "Pembatalan pengajuan rapat gagal dilakukan");
        return "redirect:/rapat/viewall-pengajuan";
    }

    @GetMapping(value = "/viewall-pengajuan")
    public String viewAllPengajuanRapat(HttpServletRequest request, Model model) {
        String username = request.getUserPrincipal().getName();
        UserModel user = userService.getUserByUsername(username);
        List<RapatModel> rapatList = userService.getRapatByUser(user);
        model.addAttribute("listRapat", rapatList);
        model.addAttribute("role", user.getRole());
        return "pengajuan-rapat/viewall-pengajuan-rapat";
    }

    @GetMapping(value = "/viewall-rapat")
    private String viewAllRapat(Model model, Principal principal) {
        UserModel userModel = userService.getUserByUsername(principal.getName());

        List<RapatModel> listRapatComing = rapatService.getAllRapatPribadiCoomingSoon(userModel);
        List<RapatModel> listRapatDone = rapatService.getAllRapatPribadiDone(userModel);
        model.addAttribute("listRapatComing", listRapatComing);
        model.addAttribute("listRapatDone", listRapatDone);
        model.addAttribute("name", userModel.getUsername());
        model.addAttribute("role", userModel.getRole());
        return "rapat/viewall-rapat";
    }
    @GetMapping("/ajuan/{id}")
    public String detailPengajuanRapat(@PathVariable String id, Model model) {
        RapatModel rapat = rapatService.getRapatByUuid(id);
        model.addAttribute("rapat",rapat);
        model.addAttribute("listPeserta", rapat.getListPeserta());
        return "pengajuan-rapat/view-rapat-ajuan";
    }

    @GetMapping("/{id}")
    public String detailRapat(@PathVariable String id, Model model) {
        RapatModel rapat = rapatService.getRapatByUuid(id);
        model.addAttribute("rapat",rapat);
        model.addAttribute("listPeserta", rapat.getListPeserta());
        return "rapat/view-rapat";
    }

    @GetMapping("/persetujuan/{id}")
    public String persetujuanRapat(@PathVariable String id,  RedirectAttributes redirAttrs){
        RapatModel rapat = rapatService.getRapatByUuid(id);
        if (rapat == null){
            return "error/404.html";
        }
        rapat.setStatus("Disetujui");
        rapatService.updateRapat(rapat);
        redirAttrs.addFlashAttribute("success", "Persetujuan pengajuan rapat berhasil dilakukan");
        return "redirect:/rapat/viewall-pengajuan";
    }

    @GetMapping("/penolakan/{id}")
    public String penolakanRapatFromPage(@PathVariable String id, Model model){
        RapatModel rapat = rapatService.getRapatByUuid(id);
        if (rapat == null){
            return "error/404.html";
        }

        model.addAttribute("nama",rapat.getNama());
        model.addAttribute("rapat",rapat);
        return "pengajuan-rapat/form-feedback-ajuan";
    }

    @PostMapping(value = "/penolakan/{id}")
    public String penolakanRapatSubmitPage(@ModelAttribute RapatModel rapat, RedirectAttributes redirAttrs) {
        rapat.setStatus("Ditolak");
        rapatService.updateRapat(rapat);
        redirAttrs.addFlashAttribute("success", "Penolakan pengajuan rapat berhasil dilakukan");
        return "redirect:/rapat/viewall-pengajuan";
    }

    @GetMapping("/ajuan/update/{uuid}")
    public String ubahPengajuanRapatPage(@PathVariable String uuid, Model model, Principal principal) {
        RapatModel rapat = rapatService.getRapatByUuid(uuid);
        if (rapat == null){
            return "error/404.html";
        }
        List<UserModel> listPesertaExisting = userService.getUserExceptAdmin();
        model.addAttribute("rapat", rapat);
        model.addAttribute("listPesertaExisting", listPesertaExisting);
        return "pengajuan-rapat/form-update-pengajuan-rapat";
    }

    @PostMapping(value = "/ajuan/update", params = {"addRowPeserta"})
    public String addRowPesertaUpdateMultiple(@ModelAttribute RapatModel rapat, Model model) {
        List<UserModel> listPesertaRapat = rapat.getListPeserta();

        Set<String> setPeserta = new HashSet<>();
        for (UserModel peserta : listPesertaRapat) {
            setPeserta.add(peserta.getUuid());
        }
        if (setPeserta.size() < listPesertaRapat.size()) {
            model.addAttribute("error", "Terdapat peserta yang duplikat di list");
        } else {
            rapat.getListPeserta().add(new UserModel());
            model.addAttribute("success", "Peserta berhasil ditambahkan ke list");
        }

        List<UserModel> listPesertaExisting = userService.getUserExceptAdmin();

        model.addAttribute("rapat", rapat);
        model.addAttribute("listPesertaExisting", listPesertaExisting);
        return "pengajuan-rapat/form-update-pengajuan-rapat";
    }

    @PostMapping(value = "/ajuan/update", params = {"deleteRowPeserta"})
    public String deleteRowPesertaUpdateMultiple(@ModelAttribute RapatModel rapat, @RequestParam("deleteRowPeserta") Integer row, Model model, Principal principal) {
        RapatModel currRapat = rapatService.getRapatByUuid(rapat.getUuid());
        final Integer rowId = Integer.valueOf(row);
        rapat.getListPeserta().remove(rowId.intValue());

        List<UserModel> listPesertaExisting = userService.getUserExceptAdmin();

        model.addAttribute("success", "Peserta berhasil dihapus dari list");
        model.addAttribute("rapat", rapat);
        model.addAttribute("listPesertaExisting", listPesertaExisting);
        return "pengajuan-rapat/form-update-pengajuan-rapat";
    }

    @PostMapping(value = "/ajuan/update", params = {"save"})
    public String updatePengajuanRapatSubmit(@ModelAttribute RapatModel rapat, Model model, Principal principal, RedirectAttributes redirAttrs) {
        List<UserModel> listPesertaExisting = userService.getUserExceptAdmin();
        List<UserModel> listPesertaRapat = rapat.getListPeserta();
        UserModel updater = userService.getUserByUsername(principal.getName());

        Set<String> setPeserta = new HashSet<>();
        for (UserModel peserta : listPesertaRapat) {
            setPeserta.add(peserta.getUuid());
        }
        if (setPeserta.size() < listPesertaRapat.size()) {
            model.addAttribute("error", "Terdapat peserta yang duplikat di list");
            model.addAttribute("rapat", rapat);
            model.addAttribute("listPesertaExisting", listPesertaExisting);
            return "pengajuan-rapat/form-update-pengajuan-rapat";
        }

        if (updater.getRole().equals("ceo") && rapat.getWaktu().minusHours(7).isBefore(LocalDateTime.now().plusDays(3))) {
            // Validasi H+3
            model.addAttribute("error", "Waktu rapat yang diubah oleh CEO minimal H+3 dari waktu pengajuan");
            model.addAttribute("rapat", rapat);
            model.addAttribute("listPesertaExisting", listPesertaExisting);
            return "pengajuan-rapat/form-update-pengajuan-rapat";
        } else if (!updater.getRole().equals("ceo") && rapat.getWaktu().minusHours(7).isBefore(LocalDateTime.now().plusDays(7))){
            // Validasi H+3
            model.addAttribute("error", "Waktu rapat yang diubah oleh karyawan minimal H+7 dari waktu pengajuan");
            model.addAttribute("rapat", rapat);
            model.addAttribute("listPesertaExisting", listPesertaExisting);
            return "pengajuan-rapat/form-update-pengajuan-rapat";
        }

        for (UserModel peserta : rapat.getListPeserta()) {
            System.out.println(peserta.getListRapat());
            if (peserta.getListRapat() == null || peserta.getListRapat().size() == 0) {
                peserta.setListRapat(new ArrayList<>());
            }
            if (!peserta.getListRapat().contains(rapat)){
                peserta.getListRapat().add(rapat);
            }

        }

        if (updater.getRole().equals("ceo")) {
            rapat.setStatus("Disetujui");
        } else {
            rapat.setStatus("Menunggu Persetujuan");
        }

        rapatService.updateRapat(rapat);
        redirAttrs.addFlashAttribute("success", "Detail pengajuan rapat berhasil diubah");
        return "redirect:/rapat/viewall-pengajuan";
    }

    @GetMapping("/update/{id}")
    public String updateRapatFormPage(@PathVariable String id, Model model){
        RapatModel rapat = rapatService.getRapatByUuid(id);
        if (rapat == null){
            return "error/404.html";
        }
        model.addAttribute("rapat",rapat);
        return "rapat/form-update-rapat";
    }

    @PostMapping(value = "/update")
    public String updateRapatSubmitPage(@ModelAttribute RapatModel rapat, RedirectAttributes redirAttrs) {
        rapatService.updateRapat(rapat);
        redirAttrs.addFlashAttribute("success", "Detail rapat berhasil diubah");
        return "redirect:/rapat/viewall-rapat";
    }
}
