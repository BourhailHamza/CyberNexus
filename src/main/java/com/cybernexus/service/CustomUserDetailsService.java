package com.cybernexus.service;

import com.cybernexus.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String queryUser = "SELECT u FROM User u WHERE u.username = :username";
        List<User> users = entityManager.createQuery(queryUser, User.class)
                .setParameter("username", username)
                .getResultList();

        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        User user = users.get(0);

        String queryRoles = "SELECT r.role_name FROM roles r " +
                "INNER JOIN user_roles ur ON r.id = ur.role_id " +
                "WHERE ur.user_id = :userId";
        List<String> roleNames = entityManager.createNativeQuery(queryRoles)
                .setParameter("userId", user.getId())
                .getResultList();

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String roleName : roleNames) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                authorities
        );
    }

    public User findUserByUsername(String username) {
        String queryUser = "SELECT u FROM User u WHERE u.username = :username";
        List<User> users = entityManager.createQuery(queryUser, User.class)
                .setParameter("username", username)
                .getResultList();

        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return users.get(0);
    }

    /**
     * Met à jour le profil de l'utilisateur.
     *
     * @param updatedUser L'objet User contenant les nouvelles informations de l'utilisateur.
     */
    @Transactional
    public void updateUserProfile(User updatedUser) {
        User existingUser = entityManager.find(User.class, updatedUser.getId());
        if (existingUser != null) {
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setBio(updatedUser.getBio());
            existingUser.setProfilePicture(updatedUser.getProfilePicture());
            entityManager.merge(existingUser);
        } else {
            throw new IllegalArgumentException("User not found with ID: " + updatedUser.getId());
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return findUserByUsername(currentUsername);
    }
}
