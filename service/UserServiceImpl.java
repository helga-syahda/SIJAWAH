package propensi.SIJAWAH.service;

import org.apache.catalina.User;
import propensi.SIJAWAH.model.*;
import propensi.SIJAWAH.repository.RapatDb;
import propensi.SIJAWAH.repository.UserDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDb userDb;
    @Autowired
    private RapatService rapatService;
    @Autowired
    private KontenService kontenService;

    @Override
    public String encrypt(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);
        return hashedPassword;
    }

    @Override
    public List<UserModel> getAllUser() {
        List<UserModel> listUser = userDb.findAll();
        return listUser;
    }

    @Override
    public UserModel getUserByUuid(String uuid) {
        Optional<UserModel> user = userDb.findById(uuid);
        if (user.isPresent()){
            return user.get();
        } else return null;
    }

    @Override
    public UserModel getUserByUsername(String username) {
        UserModel user = userDb.findByUsername(username);
        return user;
    }

    @Override
    public UserModel getUserByEmail(String email) {
        UserModel user = userDb.findByEmail(email);
        return user;
    }

    @Override
    public List<UserModel> getUserExceptAdminCeo() {
        List<UserModel> listUser = new ArrayList<>();
        for (UserModel user : userDb.findAll()) {
            if (!(user.getRole().equals("admin") || user.getRole().equals("ceo"))) {
                listUser.add(user);
            }
        }
        return listUser;
    }

    @Override
    public List<UserModel> getUserExceptAdmin() {
        List<UserModel> listUser = new ArrayList<>();
        for (UserModel user : userDb.findAll()) {
            if (!(user.getRole().equals("admin"))) {
                listUser.add(user);
            }
        }
        return listUser;
    }

    @Override
    public UserModel addUser(UserModel user) {
        String roleUser = user.getRole();
        String pass = encrypt(user.getPassword());
        switch (roleUser) {
            case "admin": {
                AdminModel newUser = new AdminModel();
                newUser.setEmail(user.getEmail());
                newUser.setNama(user.getNama());
                newUser.setPassword(pass);
                newUser.setUsername(user.getUsername());
                newUser.setIsSso(user.getIsSso());
                newUser.setRole(user.getRole());
                userDb.save(newUser);
                break;
            }
            case "ceo": {
                CEOModel newUser = new CEOModel();
                newUser.setEmail(user.getEmail());
                newUser.setNama(user.getNama());
                newUser.setPassword(pass);
                newUser.setUsername(user.getUsername());
                newUser.setIsSso(user.getIsSso());
                newUser.setRole(user.getRole());
                userDb.save(newUser);
                break;
            }
            case "finance": {
                FinanceModel newUser = new FinanceModel();
                newUser.setEmail(user.getEmail());
                newUser.setNama(user.getNama());
                newUser.setPassword(pass);
                newUser.setUsername(user.getUsername());
                newUser.setIsSso(user.getIsSso());
                newUser.setRole(user.getRole());
                userDb.save(newUser);
                break;
            }
            case "cs": {
                CustomerServiceModel newUser = new CustomerServiceModel();
                newUser.setEmail(user.getEmail());
                newUser.setNama(user.getNama());
                newUser.setPassword(pass);
                newUser.setUsername(user.getUsername());
                newUser.setIsSso(user.getIsSso());
                newUser.setRole(user.getRole());
                userDb.save(newUser);
                break;
            }
            case "socmed": {
                SocialMediaSpecialistModel newUser = new SocialMediaSpecialistModel();
                newUser.setEmail(user.getEmail());
                newUser.setNama(user.getNama());
                newUser.setPassword(pass);
                newUser.setUsername(user.getUsername());
                newUser.setIsSso(user.getIsSso());
                newUser.setRole(user.getRole());
                userDb.save(newUser);
                break;
            }
        }
        return user;
    }

    @Override
    public int validateUserForm(String email, String username, String password) {
        // Username
        if (getUserByUsername(username) != null) {
            return -1;
        }
        // Email
        if (getUserByEmail(email) != null) {
            return -2;
        }
        if (!Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", password))
            return -3;
        return 1;
    }

    @Override
    public int validateUpdateUserForm(UserModel user, String email, String username) {
        // Username
        if ((getUserByUsername(username) != null) && (!getUserByUsername(username).getUuid().equals(user.getUuid()))) {
            return -1;
        }
        // Email
        if ((getUserByEmail(email) != null) && (!getUserByEmail(email).getUuid().equals(user.getUuid())))  {
            return -2;
        }
        return 1;
    }

    @Override
    public int updatePassword(UserModel user, String password) {
        if (Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", password)){
            String hashpassword = encrypt(password);
            user.setPassword(hashpassword);
            userDb.save(user);
            return 1;
        }
        else {
            return -1;
        }
    }

    @Override
    public boolean IsValid(String password, String passwordDb){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(password,passwordDb);
    }
    @Override
    public UserModel updateUserPassword(UserModel user) {
        String pass = encrypt(user.getPassword());
        user.setPassword(pass);
        userDb.save(user);
        return user;
    }

    @Override
    public UserModel updateUser(UserModel userForm) {
        UserModel oldUser = getUserByUuid(userForm.getUuid());
        oldUser.setEmail(userForm.getEmail());
        oldUser.setNama(userForm.getNama());
        oldUser.setUsername(userForm.getUsername());
        userDb.save(oldUser);
        return oldUser;
    }

    @Override
    public List<RapatModel> getRapatByUser(UserModel userModel){
        List<RapatModel> listRapat = new ArrayList<>();
        if (userModel.getRole().equals("ceo")){
            listRapat = rapatService.getAllRapat();
        } else {
            for (RapatModel rapatModel: rapatService.getAllRapat()){
                if (rapatModel.getPic().getNama().equals(userModel.getNama())){
                    listRapat.add(rapatModel);
                }
            }
        }
        return listRapat;
    }

    @Override
    public List<KontenModel> getKontenByUser(UserModel userModel) {
        List<KontenModel> listKonten = new ArrayList<>();
        if (userModel.getRole().equals("ceo")){
            listKonten = kontenService.getAllKonten();
        } else {
            for (KontenModel kontenModel: kontenService.getAllKonten()){
                if (kontenModel.getSocmed().getNama().equals(userModel.getNama())){
                    listKonten.add(kontenModel);
                }
            }
        }
        return listKonten;
    }


    public String deleteUserByUsername(String username, String uname){
        if (username.equals(uname)){
            return "same-user";
        }
        UserModel user = getUserByUsername(username);
        if (user.getListPengajuanRapat().isEmpty()
                && user.getPekerjaan().isEmpty()
                && user.getListRapat().isEmpty()) {
            userDb.delete(user);
            return "success";
        } else {
            return "error";
        }
    }
}
