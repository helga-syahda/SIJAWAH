package propensi.SIJAWAH.service;
import propensi.SIJAWAH.model.PekerjaanModel;
import propensi.SIJAWAH.model.UserModel;

import java.util.List;

public interface PekerjaanService {
    PekerjaanModel tambahPekerjaan(PekerjaanModel pekerjaan);
    List<PekerjaanModel> getListPekerjaanCEO(UserModel userModel);
    List<PekerjaanModel> getListPekerjaanKaryawan(UserModel userModel);
    void deletePekerjaan(PekerjaanModel pekerjaan);
    PekerjaanModel getPekerjaanByUuid(String uuid);
    PekerjaanModel updateStatusPekerjaan(PekerjaanModel pekerjaan);
    String getPersentaseProgressPekerjaan(UserModel userModel);
}
