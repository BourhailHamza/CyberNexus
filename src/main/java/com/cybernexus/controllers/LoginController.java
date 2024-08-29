package com.cybernexus.controller;

import com.cybernexus.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession; // Assurez-vous d'importer HttpSession
import javax.transaction.Transactional;
import java.util.List;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/login")
    public String showLoginPage() {
        return "loginPage";
    }

    @PostMapping("/login")
    @Transactional
    public String handleLogin(@RequestParam String email, @RequestParam String password, HttpSession session) {
        logger.debug("Arrive login");

        String query = "SELECT u FROM User u WHERE u.email = :email";
        List<User> users = entityManager.createQuery(query, User.class)
                .setParameter("email", email)
                .getResultList();

        if (users.isEmpty()) {
            logger.debug("Utilisateur non trouvé avec l'email: {}", email);
            return "redirect:/login?error=userNotFound";
        } else {
            User existingUser = users.get(0);
            logger.debug("User trouvé : {}", existingUser.getPasswordHash().trim());
            logger.debug("Mot de passe introduit : {}", password.trim());

            if (existingUser.getPasswordHash().trim().equals(password.trim())) {
                logger.debug("Mot de passe correcte");

                // Stocker l'utilisateur dans la session après une connexion réussie
                session.setAttribute("user", existingUser); // Utilisation correcte de la session
                return "redirect:/index";
            } else {
                logger.debug("Mot de passe incorrect");
                return "redirect:/login?error=invalidPassword";
            }
        }
    }
}
