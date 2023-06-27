package propensi.SIJAWAH.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import propensi.SIJAWAH.model.KontenModel;

public interface KontenDb extends JpaRepository<KontenModel, String> {
    KontenModel findByUuid(String uuid);
}
