package com.cybernexus.controller;

import com.cybernexus.models.User;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping("/create")
    @Transactional
    public User createUser(@RequestBody User user) {
        // Stocker le mot de passe tel quel, sans le hacher
        entityManager.persist(user);
        return user;
    }

    @GetMapping("/{username}")
    public User getUserByUsername(@PathVariable String username) {
        String query = "SELECT u FROM User u WHERE u.username = :username";
        List<User> users = entityManager.createQuery(query, User.class)
                .setParameter("username", username)
                .getResultList();
        return users.isEmpty() ? null : users.get(0);
    }

    @PutMapping("/{id}")
    @Transactional
    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());

            // Mettre à jour le mot de passe directement
            if (updatedUser.getPasswordHash() != null && !updatedUser.getPasswordHash().isEmpty()) {
                user.setPasswordHash(updatedUser.getPasswordHash());
            }

            user.setProfilePicture(updatedUser.getProfilePicture());
            user.setBio(updatedUser.getBio());
            entityManager.merge(user);
        }
        return user;
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void deleteUser(@PathVariable Long id) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            entityManager.remove(user);
        }
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        String query = "SELECT u FROM User u";
        return entityManager.createQuery(query, User.class).getResultList();
    }
}
