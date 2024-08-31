package com.cybernexus.controllers;

import com.cybernexus.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/loginPage")
    public String showLoginPage() {
        return "loginPage";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminOnly() {
        return "admin"; // Cambiado para retornar la vista de admin de Thymeleaf
    }

    @Secured("ROLE_USER")
    @GetMapping("/user")
    public String userOnly() {
        return "index"; // Cambia esto a la vista que deseas mostrar al usuario
    }

    @PostMapping("/loginPage")
    @Transactional
    public String handleLogin(@RequestParam String username, @RequestParam String password, HttpSession session) {
        logger.debug("Arrive login");

        String query = "SELECT u FROM User u WHERE u.username = :username";
        List<User> users = entityManager.createQuery(query, User.class)
                .setParameter("username", username)
                .getResultList();

        if (users.isEmpty()) {
            logger.debug("Utilisateur non trouvé avec l'email: {}", username);
            return "redirect:/loginPage?error=userNotFound";
        } else {
            User existingUser = users.get(0);
            logger.debug("User trouvé : {}", existingUser.getPasswordHash().trim());
            logger.debug("Mot de passe introduit : {}", password.trim());

            PasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(password, existingUser.getPasswordHash().trim())) {
                logger.debug("Mot de passe correcte");
                session.setAttribute("user", existingUser);
                return "redirect:/index";
            } else {
                logger.debug("Mot de passe incorrect");
                return "redirect:/loginPage?error=invalidPassword";
            }
        }
    }
}
