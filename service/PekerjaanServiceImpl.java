package propensi.SIJAWAH.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.SIJAWAH.model.PekerjaanModel;
import propensi.SIJAWAH.model.RapatModel;
import propensi.SIJAWAH.model.UserModel;
import propensi.SIJAWAH.repository.PekerjaanDb;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PekerjaanServiceImpl implements PekerjaanService {
    @Autowired
    PekerjaanDb pekerjaanDb;

    @Override
    public PekerjaanModel tambahPekerjaan(PekerjaanModel pekerjaan) {
        pekerjaanDb.save(pekerjaan);
        return pekerjaan;
    }
    @Override
    public List<PekerjaanModel> getListPekerjaanCEO(UserModel userModel){
        List<PekerjaanModel> daftarPekerjaan = pekerjaanDb.findAll();
        return daftarPekerjaan;
    }
    @Override
    public List<PekerjaanModel> getListPekerjaanKaryawan(UserModel userModel){
        List<PekerjaanModel> daftarPekerjaan = new ArrayList<>();

        for (PekerjaanModel pekerjaan : pekerjaanDb.findAll()){
            if (userModel.getUsername().equals(pekerjaan.getAssignee().getUsername())){
                daftarPekerjaan.add(pekerjaan);
            }
        }
        return daftarPekerjaan;
    }

    @Override
    public void deletePekerjaan(PekerjaanModel pekerjaan) {
        pekerjaanDb.delete(pekerjaan);
    }

    @Override
    public PekerjaanModel getPekerjaanByUuid(String uuid) {
        PekerjaanModel pekerjaan = pekerjaanDb.findByUuid(uuid);
        return pekerjaan;
    }

    @Override
    public PekerjaanModel updateStatusPekerjaan(PekerjaanModel pekerjaan) {
        pekerjaanDb.save(pekerjaan);
        return pekerjaan;
    }

    @Override
    public String getPersentaseProgressPekerjaan(UserModel userModel) {
        List<PekerjaanModel> daftarPekerjaan = null;
        if (userModel.getRole().equals("ceo")) {
            daftarPekerjaan = pekerjaanDb.findAll();
        } else {
            for (PekerjaanModel pekerjaan : pekerjaanDb.findAll()) {
                if (userModel.getUsername().equals(pekerjaan.getAssignee().getUsername())) {
                    daftarPekerjaan.add(pekerjaan);
                }
            }
        }

        if (daftarPekerjaan == null) {
            return "0";
        }

        List<PekerjaanModel> daftarPekerjaanDone = null;
        for (PekerjaanModel pekerjaan: daftarPekerjaan) {
            if (pekerjaan.getStatus().equals("2")) {
                daftarPekerjaanDone.add(pekerjaan);
            }
        }

        if (daftarPekerjaanDone == null) {
            return "0";
        }

        Float persentaseProgressPekerjaan = (float) daftarPekerjaanDone.size() / daftarPekerjaan.size();
        return String.format("%.2f%%", persentaseProgressPekerjaan * 100);
    }
}
