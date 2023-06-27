package propensi.SIJAWAH.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import propensi.SIJAWAH.model.KontenModel;
import propensi.SIJAWAH.model.RapatModel;
import propensi.SIJAWAH.model.SocialMediaSpecialistModel;
import propensi.SIJAWAH.model.UserModel;
import propensi.SIJAWAH.service.KontenService;
import propensi.SIJAWAH.service.SocialMediaSpecialistService;
import propensi.SIJAWAH.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

@Controller
@RequestMapping("/konten")
public class KontenController {

    @Autowired
    KontenService kontenService;

    @Autowired
    SocialMediaSpecialistService socialMediaSpecialistService;

    @Autowired
    UserService userService;

    @GetMapping(value = "/add")
    private String addKontenFormPage(Model model, Principal principal){
        KontenModel kontenModel = new KontenModel();
        model.addAttribute("konten", kontenModel);
        return "ide-konten/form-add-konten";
    }

    @PostMapping(value = "/add", params = {"save"})
    public String addKontenSubmitPage(@ModelAttribute KontenModel konten, Model model, RedirectAttributes redirAttrs, Principal principal) {
        if (konten.getTanggal_publish().minusHours(7).isAfter(LocalDateTime.now().plusDays(5))) {
            konten.setStatus("Menunggu Persetujuan");
            SocialMediaSpecialistModel socmed = socialMediaSpecialistService.getSocmedByUuid(userService.getUserByUsername(principal.getName()).getUuid());
            konten.setSocmed(socmed);
            List<KontenModel> kontenModelList = socmed.getKonten();
            kontenModelList.add(konten);
            socmed.setKonten(kontenModelList);
            kontenService.tambahKonten(konten);
            redirAttrs.addFlashAttribute("success", "Ide konten baru berhasil ditambahkan");
        } else {
            redirAttrs.addFlashAttribute("error", "Ide konten baru gagal ditambahkan");
        }
        return "redirect:/konten/viewall";
    }

    @GetMapping(value = "/viewall")
    public String viewAllPengajuanKonten(HttpServletRequest request, Model model) {
        String username = request.getUserPrincipal().getName();
        UserModel user = userService.getUserByUsername(username);
        List<KontenModel> kontenList = userService.getKontenByUser(user);
        model.addAttribute("role", user.getRole());
        model.addAttribute("listKonten", kontenList);
        return "ide-konten/viewall-konten";
    }

    @GetMapping("/{uuid}")
    public String detailKonten(@PathVariable String uuid, Model model) {
        KontenModel konten = kontenService.getKontenByUuid(uuid);
        model.addAttribute("konten", konten);
        return "ide-konten/view-detail-konten";
    }

}
