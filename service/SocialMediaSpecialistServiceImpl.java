package propensi.SIJAWAH.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.SIJAWAH.model.SocialMediaSpecialistModel;
import propensi.SIJAWAH.repository.SocialMediaSpecialistDb;

import javax.transaction.Transactional;

@Service
@Transactional
public class SocialMediaSpecialistServiceImpl implements SocialMediaSpecialistService {
    @Autowired
    private SocialMediaSpecialistDb socialMediaSpecialistDb;

    @Override
    public SocialMediaSpecialistModel getSocmedByUuid(String uuid) {
        SocialMediaSpecialistModel socmed = socialMediaSpecialistDb.findByUuid(uuid);
        return socmed;
    }
}
