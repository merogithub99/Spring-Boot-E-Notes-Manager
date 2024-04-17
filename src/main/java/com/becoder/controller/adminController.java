package com.becoder.controller;


import com.becoder.entity.User;
import com.becoder.service.UserService;
import com.becoder.service.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class adminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/adminLogin")
    public String adminLogin(){
        return "adminLogin";
    }


    @GetMapping("/manageUsers")
    public String manageUsers(Model model){
        List<User> users=userService.getAllUsers();
        model.addAttribute("users", users);
        System.out.println(users);
        return "manageUsers";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam("userId") int userId, RedirectAttributes redirectAttributes) {
        userService.deleteUser(userId);
        redirectAttributes.addFlashAttribute("successMessage", "User successfully deleted.");

        return "redirect:/manageUsers"; // Redirect back to the manage users page
    }

    @PostMapping("/postLogin")
    public String postLogin(@RequestParam String email, UserServiceImpl userService1, @RequestParam String password, HttpSession session) {

        // Remove any existing error message
        userService1.removeSessionMessage();


        // Retrieve user by email from the database
        User user = userService.findByEmail(email);
        System.out.println(user);

        // Check if the user exists and has admin privileges
        if (user != null && "admin".equals(user.getUserType()) && user.getRole().equals("ROLE_ADMIN")) {
            // Check if the provided password matches the stored password
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Password matches, redirect to admin dashboard
                return "adminDashboard";
            } else {
                // Password does not match, show error message
                session.setAttribute("error", "Invalid email or password");
                return "adminLogin";
            }
        } else {
            // User is not an admin or does not exist, show error message
            session.setAttribute("error", "You are not admin ");
            return "adminLogin";
        }
    }
}
