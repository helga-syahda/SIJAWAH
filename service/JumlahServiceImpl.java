package propensi.SIJAWAH.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.SIJAWAH.model.JumlahModel;
import propensi.SIJAWAH.repository.JumlahDb;

import java.util.List;

@Service
public class JumlahServiceImpl implements JumlahService{
    @Autowired
    JumlahDb jumlahDb;

    @Override
    public JumlahModel addJumlah(JumlahModel jumlah) {
        return jumlahDb.save(jumlah);
    }

    @Override
    public void removeJumlah(JumlahModel jumlah) {jumlahDb.delete(jumlah);}

    @Override
    public List<JumlahModel> getListJumlah() {return jumlahDb.findAll();}

    @Override
    public JumlahModel isExist(JumlahModel jumlah) {
        List<JumlahModel> listJumlah = jumlahDb.findAll();
        for (JumlahModel temp : listJumlah) {
            if(temp.getPelanggan().getUuid().equals(jumlah.getPelanggan().getUuid()) && jumlah.getProduk().getUuid().equals(jumlah.getProduk().getUuid())){
                return temp;
            }
        }
        return null;
    }
}
