package propensi.SIJAWAH.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.SIJAWAH.model.RapatModel;
import propensi.SIJAWAH.model.UserModel;
import propensi.SIJAWAH.repository.RapatDb;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RapatServiceImpl implements RapatService {
    @Autowired
    private RapatDb rapatDb;

    @Override
    public List<RapatModel> getAllRapat() {
        return rapatDb.findAll();
    }

    @Override
    public RapatModel addRapat(RapatModel rapat) {
        return rapatDb.save(rapat);
    }

    @Override
    public RapatModel updateRapat(RapatModel rapat) {
        return rapatDb.save(rapat);
    }

    @Override
    public RapatModel getRapatByUuid(String uuid) {
        RapatModel rapat = rapatDb.findByUuid(uuid);
        return rapat;
    }

    @Override
    public void deletePengajuanRapat(RapatModel rapat) {
        rapatDb.delete(rapat);
    }

    @Override
    public List<RapatModel> getAllRapatPribadiCoomingSoon(UserModel user) {
        List<RapatModel> rapatComingSoon = new ArrayList<>();

        for (RapatModel rapat : rapatDb.findAll()) {
            if (rapat.getStatus().equals("Disetujui")) {
                if (rapat.getWaktu().minusHours(7).isAfter(LocalDateTime.now())) {
                    if (user.getRole().equals("ceo")){
                        rapatComingSoon.add(rapat);
                    } else {
                        for (UserModel peserta : rapat.getListPeserta()) {
                            if (peserta.getUuid().equals(user.getUuid())) {
                                rapatComingSoon.add(rapat);
                            }
                        }
                    }
                }
            }
        }
        return rapatComingSoon;
    }

    @Override
    public List<RapatModel> getAllRapatPribadiDone(UserModel user) {
        List<RapatModel> rapatDone = new ArrayList<>();

        for (RapatModel rapat : rapatDb.findAll()) {
            if (rapat.getStatus().equals("Disetujui")) {
                if (rapat.getWaktu().minusHours(7).isEqual(LocalDateTime.now()) || rapat.getWaktu().minusHours(7).isBefore(LocalDateTime.now())) {
                    if (user.getRole().equals("ceo")){
                        rapatDone.add(rapat);
                    } else {
                        for (UserModel peserta : rapat.getListPeserta()) {
                            if (peserta.getUuid().equals(user.getUuid())) {
                                rapatDone.add(rapat);
                            }
                        }
                    }
                }
            }
        }
        return rapatDone;
    }

    @Override
    public RapatModel tolakRapat (RapatModel rapat){return rapatDb.save(rapat);}

}
