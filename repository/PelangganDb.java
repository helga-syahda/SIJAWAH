package propensi.SIJAWAH.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import propensi.SIJAWAH.model.PelangganModel;

public interface PelangganDb extends JpaRepository<PelangganModel, String> {
    PelangganModel findByNomorTelepon(String nomorTelepon);
}
