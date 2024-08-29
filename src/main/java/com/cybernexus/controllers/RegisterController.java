package com.cybernexus.controller;

import com.cybernexus.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Controller
public class RegisterController {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/register")
    public String showRegistrationPage() {
        return "register";
    }

    @PostMapping("/register")
    @Transactional
    public String handleRegistration(@RequestParam String username, @RequestParam String email, @RequestParam String password) {
        try {
            System.out.println("Received registration request for username: " + username + ", email: " + email);

            String emailQuery = "SELECT u FROM User u WHERE u.email = :email";
            List<User> usersByEmail = entityManager.createQuery(emailQuery, User.class)
                    .setParameter("email", email)
                    .getResultList();

            String usernameQuery = "SELECT u FROM User u WHERE u.username = :username";
            List<User> usersByUsername = entityManager.createQuery(usernameQuery, User.class)
                    .setParameter("username", username)
                    .getResultList();

            if (!usersByEmail.isEmpty() || !usersByUsername.isEmpty()) {
                System.out.println("User already exists with email: " + email + " or username: " + username);
                return "redirect:/register?error";
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPasswordHash(password);

            entityManager.persist(newUser);

            System.out.println("User successfully registered: " + username);

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/register?error=database";
        }

        return "redirect:/login";
    }
}
