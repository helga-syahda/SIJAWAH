package propensi.SIJAWAH.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.SIJAWAH.model.JumlahModel;
import propensi.SIJAWAH.model.ProdukModel;
import propensi.SIJAWAH.repository.JumlahDb;
import propensi.SIJAWAH.repository.ProdukDb;

import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class ProdukServiceImpl implements ProdukService {
    @Autowired
    private ProdukDb produkDb;

    @Autowired
    private JumlahDb jumlahDb;

    @Override
    public List<ProdukModel> getListProduk(){
        return produkDb.findAll();
    }

    @Override
    public ProdukModel getProduk(String nama) {
        ProdukModel produk = produkDb.findByNama(nama);
        if(produk!=null) {
            return produk;
        } else return null;
    }

    @Override
    public List<Integer> getDataPenjualan(){
        List<Integer> listData = new ArrayList<>();
        for (ProdukModel produk : produkDb.findAll()){
            int jumlahJual = 0;
            for (JumlahModel jumlah : jumlahDb.findAll()){
                if (jumlah.getProduk().getUuid().equals(produk.getUuid())){
                    jumlahJual += jumlah.getKuantitas();
                }
            }
            listData.add(jumlahJual);
        }
        return listData;
    }

    @Override
    public List<String> getNamaProduk(){
        List<String> nama = new ArrayList<>();
        for (ProdukModel produkModel :getListProduk()){
            nama.add(produkModel.getNama());
        }
        return nama;
    }

    @Override
    public List<Integer> getDataPenjualanByPelanggan(String uuid){
        List<Integer> data = new ArrayList<>();
        for (ProdukModel produk : getListProduk()){
            int total = 0;
            for (JumlahModel jumlah: jumlahDb.findAll()){
                if (jumlah.getProduk().getUuid().equals(produk.getUuid()) &&
                jumlah.getPelanggan().getUuid().equals(uuid)){
                    total += jumlah.getKuantitas();
                }
            }
            data.add(total);
        }
        return data;
    }

    @Override
    public List<Integer> getDataPendapatan(String uuid){
        List<Integer> data = new ArrayList<>();
        List<ProdukModel> listProduk = produkDb.findAll();
        if (uuid.equals("")){
            List<Integer> dataPenjualan = getDataPenjualan();
            for (int i = 0; i < listProduk.size(); i++){
                int pendapatan = listProduk.get(i).getHarga() * dataPenjualan.get(i);
                data.add(pendapatan);
            }
            return data;
        } else {
            List<Integer> dataPenjualan = getDataPenjualanByPelanggan(uuid);
            for (int i = 0; i < listProduk.size(); i++){
                int pendapatan = listProduk.get(i).getHarga() * dataPenjualan.get(i);
                data.add(pendapatan);
            }
            return data;
        }
    }

    @Override
    public String getTotalRevenueRupiahFormat() {
        List<Integer> PendapatanPerProduk = getDataPendapatan("");
        Integer totalRevenue = PendapatanPerProduk.stream().reduce(0, (a, b) -> a + b);

        NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        DecimalFormat decimalFormat = (DecimalFormat)rupiahFormat;
        decimalFormat.applyPattern("#,###");

        return rupiahFormat.format(totalRevenue);
    }

    @Override
    public Integer getTotalOrder() {
        Integer totalOrder = getDataPenjualan().stream().reduce(0, (a, b) -> a + b);
        return totalOrder;
    }
}
