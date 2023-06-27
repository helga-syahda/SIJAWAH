package propensi.SIJAWAH.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.SIJAWAH.model.PelangganModel;
import propensi.SIJAWAH.repository.PelangganDb;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PelangganServiceImpl implements PelangganService{

    @Autowired
    private PelangganDb pelangganDb;

    @Override
    public PelangganModel addPelanggan(PelangganModel pelanggan){
        return pelangganDb.save(pelanggan);
    }

    @Override
    public List<PelangganModel> getAllPelanggan(){
        return pelangganDb.findAll();
    }
    @Override
    public PelangganModel getPelangganByUUID(String uuid) {
        Optional<PelangganModel> pelanggan = pelangganDb.findById(uuid);
        if(pelanggan!=null) {
            return pelanggan.get();
        }
        return null;
    }

    @Override
    public PelangganModel getPelanggan(String nomorTelepon) {
        return pelangganDb.findByNomorTelepon(nomorTelepon);
    }

    @Override
    public List<PelangganModel> getListPelanggan() {
        List<PelangganModel> listPelanggan = pelangganDb.findAll();
        for (PelangganModel pelanggan : listPelanggan) {
            if (pelanggan.getLastUpdate().isBefore(LocalDateTime.now().minusMonths(5))) {
                pelangganDb.delete(pelanggan);
            }
        }

        return pelangganDb.findAll();
    }

    @Override
    public void deletePelanggan(String uuid) {
        PelangganModel pelanggan = getPelangganByUUID(uuid);
        if(pelanggan != null) {
            pelangganDb.delete(pelanggan);
        }
    }

    @Override
    public long getTotalPelanggan() {
        return pelangganDb.count();
    }
}
