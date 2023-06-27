package propensi.SIJAWAH.service;

import propensi.SIJAWAH.model.ProdukModel;

import java.util.List;

public interface ProdukService {
    List<ProdukModel> getListProduk();

    ProdukModel getProduk(String nama);

    List<Integer> getDataPenjualan();
    List<String> getNamaProduk();

    List<Integer> getDataPenjualanByPelanggan(String uuid);

    List<Integer> getDataPendapatan(String uuid);

    String getTotalRevenueRupiahFormat();

    Integer getTotalOrder();
}
