package propensi.SIJAWAH.service;

import propensi.SIJAWAH.model.PelangganModel;

import java.util.List;

public interface PelangganService {

    PelangganModel addPelanggan(PelangganModel pelanggan);

    List<PelangganModel> getAllPelanggan();

    PelangganModel getPelangganByUUID(String uuid);

    PelangganModel getPelanggan(String nomorTelepon);

    List<PelangganModel> getListPelanggan();

    void deletePelanggan(String uuid);

    long getTotalPelanggan();
}
