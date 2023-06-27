package propensi.SIJAWAH.service;

import org.apache.catalina.User;
import propensi.SIJAWAH.model.RapatModel;
import propensi.SIJAWAH.model.UserModel;

import java.util.List;

import java.util.List;

public interface RapatService {
    List<RapatModel> getAllRapat();
    RapatModel getRapatByUuid(String uuid);
    RapatModel addRapat(RapatModel rapat);
    RapatModel updateRapat(RapatModel rapat);
    void deletePengajuanRapat(RapatModel rapat);
    List<RapatModel> getAllRapatPribadiCoomingSoon(UserModel user);
    List<RapatModel> getAllRapatPribadiDone(UserModel user);
    RapatModel tolakRapat(RapatModel rapat);
}
