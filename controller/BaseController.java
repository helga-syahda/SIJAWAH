package propensi.SIJAWAH.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import propensi.SIJAWAH.model.RapatModel;
import propensi.SIJAWAH.model.UserModel;
import propensi.SIJAWAH.service.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller

public class BaseController {

    @Autowired
    PekerjaanService pekerjaanService;

    @Autowired
    PelangganService pelangganService;

    @Autowired
    ProdukService produkService;

    @Autowired
    RapatService rapatService;

    @Autowired
    UserService userService;

    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        UserModel userModel = userService.getUserByUsername(request.getUserPrincipal().getName());
        String userRole = "";

        if (userModel == null) {
            model.addAttribute("name", request.getUserPrincipal().getName());
            model.addAttribute("role", "admin");
        } else {
            model.addAttribute("name", userModel.getNama());
            userRole = userModel.getRole();
            model.addAttribute("role", userRole);
        }

        if (userRole.equals("finance") || userRole.equals("ceo")) {
            // Total Pelanggan
            model.addAttribute("totalPelanggan", pelangganService.getTotalPelanggan());

            // Total Revenue
            model.addAttribute("totalRevenue", produkService.getTotalRevenueRupiahFormat());

            // Progress Pekerjaan
            model.addAttribute("persentaseProgressPekerjaan", pekerjaanService.getPersentaseProgressPekerjaan(userModel));

            // Total Order
            model.addAttribute("totalOrder", produkService.getTotalOrder());

            // Chart Jumlah Produk Terjual
            model.addAttribute("listNamaProduk", produkService.getNamaProduk());
            model.addAttribute("listDataPenjualan", produkService.getDataPenjualan());
        }

        // Tabel Jadwal Rapat Mendatang
        if (userModel != null) {
            model.addAttribute("jadwalRapatMendatang", rapatService.getAllRapatPribadiCoomingSoon(userModel));
        }

        return "home";

    }
}
