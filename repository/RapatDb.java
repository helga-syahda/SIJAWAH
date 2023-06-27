package propensi.SIJAWAH.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import propensi.SIJAWAH.model.RapatModel;
import propensi.SIJAWAH.model.UserModel;

import java.util.List;

@Repository
public interface RapatDb extends JpaRepository<RapatModel, String> {
    RapatModel findByUuid(String uuid);
}

