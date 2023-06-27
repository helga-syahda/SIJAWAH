package propensi.SIJAWAH.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.SIJAWAH.model.KontenModel;
import propensi.SIJAWAH.model.PekerjaanModel;
import propensi.SIJAWAH.model.RapatModel;
import propensi.SIJAWAH.model.UserModel;
import propensi.SIJAWAH.repository.KontenDb;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class KontenServiceImpl implements KontenService {
    @Autowired
    KontenDb kontenDb;

    @Override
    public KontenModel tambahKonten(KontenModel kontenModel) {
        kontenDb.save(kontenModel);
        return kontenModel;
    }

    @Override
    public List<KontenModel> getAllKonten() {
        return kontenDb.findAll();
    }

    @Override
    public KontenModel getKontenByUuid(String uuid) {
        KontenModel konten = kontenDb.findByUuid(uuid);
        return konten;
    }

}
