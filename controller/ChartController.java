package propensi.SIJAWAH.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import propensi.SIJAWAH.model.PelangganModel;
import propensi.SIJAWAH.service.PelangganService;
import propensi.SIJAWAH.service.ProdukService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/chart")
public class ChartController {

    @Autowired
    ProdukService produkService;

    @Autowired
    PelangganService pelangganService;

    @GetMapping("")
    public String chartDefault(Model model){
        model.addAttribute("listProduk", produkService.getNamaProduk());
        model.addAttribute("listData", produkService.getDataPenjualan());
        model.addAttribute("listDataPendapatan", produkService.getDataPendapatan(""));
        model.addAttribute("listPelanggan", pelangganService.getAllPelanggan());
        return "chart/default-chart";
    }
    @PostMapping("/generate")
    public String formChart(@RequestParam("uuid") String uuid, Model model){
        model.addAttribute("listData", produkService.getDataPenjualanByPelanggan(uuid));
        model.addAttribute("listDataPendapatan", produkService.getDataPendapatan(uuid));
        model.addAttribute("listProduk", produkService.getNamaProduk());
        model.addAttribute("listPelanggan", pelangganService.getAllPelanggan());
        PelangganModel pelangganModel = pelangganService.getPelangganByUUID(uuid);
        model.addAttribute("pelanggan", pelangganModel.getNama());
        return "chart/default-chart";
    }

}
