package propensi.SIJAWAH.controller;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import propensi.SIJAWAH.model.RapatModel;
import propensi.SIJAWAH.model.UserModel;
import propensi.SIJAWAH.service.UserService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.util.List;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;

    @GetMapping("/view-all")
    public String listUser(Model model) {
        List<UserModel> listUser = userService.getAllUser();
        model.addAttribute("listUser", listUser);
        return "manajemen-user/viewall-user";
    }

    @GetMapping("/add")
    public String addUserPage(Model model) {
        UserModel user = new UserModel();
        user.setIsSso(false);
        model.addAttribute("user", user);
        return "manajemen-user/form-add-user";
    }

    @PostMapping(value = "/add", params = {"save"})
    public String addUserPageSubmit(@ModelAttribute UserModel user, Model model, RedirectAttributes redirectAttr) {
        int validationCode = userService.validateUserForm(user.getEmail(), user.getUsername(), user.getPassword());
        if (validationCode == -1) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Username telah terdaftar pada sistem!");
            return "manajemen-user/form-add-user";
        } else if (validationCode == -2) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Email telah terdaftar pada sistem!");
            return "manajemen-user/form-add-user";
        } else if (validationCode == -3) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Password baru dengan panjang minimal 8 dengan kombinasi huruf dan angka!");
            return "manajemen-user/form-add-user";
        }
        userService.addUser(user);
        redirectAttr.addFlashAttribute("success", "User baru berhasil ditambahkan");
        return "redirect:/user/view-all";
    }

    @GetMapping("/delete/{username}")
    public String deleteUser(@PathVariable String username, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request){
        String uname = request.getUserPrincipal().getName();
        String msg = userService.deleteUserByUsername(username, uname);
        String successMessage = "User dengan username " + username + " berhasil dihapus";
        String errorMessage = "User dengan username " + username + " tidak dapat dihapus";
        if (msg.equals("success")) {
            redirectAttributes.addFlashAttribute("success", successMessage);
        } else if (msg.equals("error")){
            redirectAttributes.addFlashAttribute("error", errorMessage);
        } else {
            redirectAttributes.addFlashAttribute("error", "Tidak dapat menghapus diri sendiri");
        }
        return "redirect:/user/view-all";
    }

    @GetMapping("/update/{uuid}")
    public String updateUserPage(@PathVariable String uuid, Model model) {
        UserModel user = userService.getUserByUuid(uuid);
        if (user == null) {
            return "error/404";
        }
        model.addAttribute("user", user);
        return "manajemen-user/form-update-user";
    }


    @PostMapping(value = "/update", params = {"save"})
    public String updateUserPageSubmit(@ModelAttribute UserModel user, Model model, RedirectAttributes redirectAttr) {
        int validationCode = userService.validateUpdateUserForm(user, user.getEmail(), user.getUsername());
        if (validationCode == -1) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Username telah terdaftar pada sistem!");
            return "manajemen-user/form-update-user";
        } else if (validationCode == -2) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Email telah terdaftar pada sistem!");
            return "manajemen-user/form-update-user";
        }
        userService.updateUser(user);
        redirectAttr.addFlashAttribute("success", "Data user " + user.getEmail() + " berhasil diubah");
        return "redirect:/user/view-all";
    }

    @GetMapping("/update-password/{uuid}")
    public String updatePasswordPage(@PathVariable String uuid, Model model) {
        UserModel user = userService.getUserByUuid(uuid);
        if (user == null) {
            return "error/404";
        }
        model.addAttribute("email", user.getEmail());
        return "manajemen-user/form-reset-password";
    }

    @PostMapping(value = "/update-password", params = {"savePassword"})
    public String updatePasswordSubmit(@RequestParam("email") String email,
                                       @RequestParam("password") String password,
                                       Model model,
                                       RedirectAttributes redirectAttr) {
        UserModel user = userService.getUserByEmail(email);
        if (user == null) {
            return "error/404";
        }
        Integer validationCode = userService.updatePassword(user, password);
        if (validationCode == -1) {
            model.addAttribute("email", user.getEmail());
            model.addAttribute("error", "Password baru dengan panjang minimal 8 dengan kombinasi huruf dan angka!");
            return "manajemen-user/form-reset-password";
        }
        redirectAttr.addFlashAttribute("success", "Password user " + user.getEmail() + " berhasil diganti");
        return "redirect:/user/view-all";
    }

    @GetMapping("/profil")
    public String detailUser(Model model, HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        UserModel user = userService.getUserByUsername(username);
        model.addAttribute("user", user);
        return "profil/profil-user";
    }

    @GetMapping("/ubah-password")
    public String updatePasswordForm(Model model, HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        UserModel user = userService.getUserByUsername(username);
        if (user == null) {
            return "error/404";
        }
        model.addAttribute("user", user);
        return "manajemen-user/form-update-password";
    }

    @PostMapping(value = "/ubah-password", params = {"save"})
    private String updatePasswordSubmit(@RequestParam("passwordLama") String passwordLama,
                                        @RequestParam("passwordBaru") String passwordBaru,
                                        @RequestParam("konfirmasi") String konfirmasi,
                                        Principal principal, Model model) {
        UserModel user = userService.getUserByUsername(principal.getName());

        if (!userService.IsValid(passwordLama, user.getPassword())) {
            model.addAttribute("error", "Password lama yang Anda masukan salah!");
            return "manajemen-user/form-update-password";
        }

        if (userService.IsValid(passwordBaru, user.getPassword())) {
            model.addAttribute("error", "Password baru sama dengan password lama!");
            return "manajemen-user/form-update-password";
        }

        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";

        if (!passwordBaru.matches(pattern)) {
            model.addAttribute("error", "Password baru dengan panjang minimal 8 dengan kombinasi huruf dan angka!");
            return "manajemen-user/form-update-password";
        }

        if (!passwordBaru.equals(konfirmasi)) {
            model.addAttribute("error", "Password konfirmasi berbeda!");
            return "manajemen-user/form-update-password";
        }

        user.setPassword(passwordBaru);
        userService.updateUserPassword(user);
        return "redirect:/logout-sso";

    }


}