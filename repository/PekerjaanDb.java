package propensi.SIJAWAH.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import propensi.SIJAWAH.model.PekerjaanModel;
import propensi.SIJAWAH.model.RapatModel;

@Repository
public interface PekerjaanDb extends JpaRepository<PekerjaanModel, String> {
    PekerjaanModel findByUuid(String uuid);
}
