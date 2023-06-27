package propensi.SIJAWAH.service;

import propensi.SIJAWAH.model.JumlahModel;

import java.util.List;

public interface JumlahService {
    JumlahModel addJumlah(JumlahModel jumlah);

    void removeJumlah(JumlahModel jumlah);

    List<JumlahModel> getListJumlah();

    JumlahModel isExist(JumlahModel appointment);
}
