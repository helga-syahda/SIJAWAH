package propensi.SIJAWAH.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import propensi.SIJAWAH.model.SocialMediaSpecialistModel;

@Repository
public interface SocialMediaSpecialistDb extends JpaRepository<SocialMediaSpecialistModel, String> {
    SocialMediaSpecialistModel findByUuid(String uuid);
}
