package propensi.SIJAWAH.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import propensi.SIJAWAH.model.JumlahModel;

import java.util.List;

public interface JumlahDb extends JpaRepository<JumlahModel, Long> {
    @Query("SELECT jumlah FROM JumlahModel jumlah WHERE jumlah.pelanggan.uuid = :uuidPelanggan and jumlah.produk.uuid = :uuidProduk")
    JumlahModel findByPelangganAndProduk(@Param("uuidPelanggan") String uuidPelanggan, @Param("uuidProduk") String uuidProduk);
}
