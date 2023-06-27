package propensi.SIJAWAH.service;

import propensi.SIJAWAH.model.KontenModel;
import propensi.SIJAWAH.model.RapatModel;
import propensi.SIJAWAH.model.UserModel;

import java.util.List;

public interface UserService {
    UserModel addUser(UserModel user);
    UserModel getUserByUuid(String uuid);
    UserModel getUserByUsername(String username);
    UserModel getUserByEmail(String email);
    String encrypt(String password);
    List<UserModel> getUserExceptAdminCeo();
    List<UserModel> getAllUser();
    List<UserModel> getUserExceptAdmin();
    int validateUserForm(String email, String username, String password);
    int validateUpdateUserForm(UserModel user, String email, String username);
    UserModel updateUser(UserModel user);
    int updatePassword(UserModel user, String password);
    List<RapatModel> getRapatByUser(UserModel userModel);
    List<KontenModel> getKontenByUser(UserModel userModel);
    String deleteUserByUsername(String Username, String uname);
    boolean IsValid(String passwordLama, String passwordDb);
    UserModel updateUserPassword(UserModel user);
}
