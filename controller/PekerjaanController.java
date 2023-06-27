package propensi.SIJAWAH.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import propensi.SIJAWAH.model.*;
import propensi.SIJAWAH.service.PekerjaanService;
import propensi.SIJAWAH.service.UserService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/pekerjaan")
public class PekerjaanController {
    @Autowired
    PekerjaanService pekerjaanService;

    @Autowired
    UserService userService;

    @GetMapping(value = "/add")
    private String addPekerjaanFormPage(Model model) {
        PekerjaanModel pekerjaan = new PekerjaanModel();
        List<UserModel> listKaryawan = userService.getUserExceptAdminCeo();
        model.addAttribute("pekerjaan", pekerjaan);
        model.addAttribute("listKaryawan", listKaryawan);
        return "pekerjaan/form-add-pekerjaan";
    }

    @PostMapping(value = "/add", params = {"save"})
    public String addPekerjaanSubmitPage(@ModelAttribute PekerjaanModel pekerjaan, Model model,RedirectAttributes redirAttrs) {
        List<UserModel> listKaryawan = userService.getUserExceptAdminCeo();
        model.addAttribute("listKaryawan", listKaryawan);

        if (pekerjaan.getDeadline().minusHours(7).isBefore(LocalDateTime.now().plusDays(3))) {
            model.addAttribute("error", "Waktu deadline minimal H-3 dari waktu pemberian tugas");
            model.addAttribute("pekerjaan", pekerjaan);
            return "pekerjaan/form-add-pekerjaan";
        }
        pekerjaanService.tambahPekerjaan(pekerjaan);
        redirAttrs.addFlashAttribute("success", "Pekerjaan baru berhasil ditambahkan");
        return "redirect:/pekerjaan/view-all";
    }

    @GetMapping("/view-all")
    public String listPekerjaan(Model model, Principal principal) {
        UserModel userModel = userService.getUserByUsername(principal.getName());
        model.addAttribute("role", userModel.getRole());
        if (userModel.getRole().equals("ceo")) {
            List<PekerjaanModel> listPekerjaan = pekerjaanService.getListPekerjaanCEO(userModel);
            model.addAttribute("listPekerjaan", listPekerjaan);
        } else {
            List<PekerjaanModel> listPekerjaan = pekerjaanService.getListPekerjaanKaryawan(userModel);
            model.addAttribute("listPekerjaan", listPekerjaan);
        }
        return "pekerjaan/viewall-pekerjaan";
    }

    @GetMapping(value = "/delete/{uuid}")
    public String deletePekerjaan(@PathVariable String uuid, Model model, RedirectAttributes redirAttrs) {
        PekerjaanModel pekerjaan = pekerjaanService.getPekerjaanByUuid(uuid);
        String namaPekerjaan = pekerjaan.getNama();
        System.out.println(pekerjaan.getStatus());
        if (pekerjaan.getStatus().equals("0")){
            pekerjaanService.deletePekerjaan(pekerjaan);
            model.addAttribute("pekerjaan", pekerjaan);
            model.addAttribute("nama", namaPekerjaan);
            redirAttrs.addFlashAttribute("batal", "Pembatalan penugasan pekerjaan berhasil dilakukan");
            return "redirect:/pekerjaan/view-all";
        }
        redirAttrs.addFlashAttribute("batal", "Pembatalan penugasan pekerjaan gagal dilakukan");
        return "redirect:/pekerjaan/view-all";
    }


    @GetMapping(value = "/update/{uuid}")
    public String updatePekerjaanFormPage(@PathVariable String uuid, Model model) {
        PekerjaanModel pekerjaan = pekerjaanService.getPekerjaanByUuid(uuid);
        model.addAttribute("assignee", pekerjaan.getAssignee());
        model.addAttribute("pekerjaan", pekerjaan);
        return "pekerjaan/form-update-pekerjaan";
    }


    @PostMapping(value = "/update/{uuid}",params = {"save"})
    public String updatePekerjaanSubmitPage(@ModelAttribute PekerjaanModel pekerjaan, Model model, RedirectAttributes redirAttrs){
        PekerjaanModel updatedStatus = pekerjaanService.updateStatusPekerjaan(pekerjaan);
        model.addAttribute("pekerjaan", pekerjaan);
        model.addAttribute("nama", updatedStatus.getNama());
        redirAttrs.addFlashAttribute("ubah", "Pengubahan status pekerjaan berhasil dilakukan");
        return "redirect:/pekerjaan/view-all";
    }

    @GetMapping("/{id}")
    public String detailPekerjaan(@PathVariable String id, Model model) {
        PekerjaanModel pekerjaan = pekerjaanService.getPekerjaanByUuid(id);
        model.addAttribute("pekerjaan",pekerjaan);
        return "pekerjaan/view-pekerjaan";
    }

}
