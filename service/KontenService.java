package propensi.SIJAWAH.service;

import propensi.SIJAWAH.model.KontenModel;

import java.util.List;

public interface KontenService {
    KontenModel tambahKonten(KontenModel kontenModel);
    List<KontenModel> getAllKonten();
    KontenModel getKontenByUuid(String uuid);
}
