package propensi.SIJAWAH.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import propensi.SIJAWAH.model.ProdukModel;

public interface ProdukDb extends JpaRepository<ProdukModel, String> {
    ProdukModel findByNama(String nama);

}
