package com.cybernexus.controllers;

import com.cybernexus.models.User;
import com.cybernexus.service.CustomUserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;

@Controller
public class ProfileController {

    private final CustomUserDetailsService userDetailsService;

    public ProfileController(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        User user = userDetailsService.getCurrentUser();
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") User updatedUser,
                                @RequestParam("profilePictureFile") MultipartFile profilePictureFile) {
        if (!profilePictureFile.isEmpty()) {
            try {
                String fileName = profilePictureFile.getOriginalFilename();
                String uploadDir = "user-profile-photos/";

                saveFile(uploadDir, fileName, profilePictureFile);

                updatedUser.setProfilePicture(uploadDir + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        userDetailsService.updateUserProfile(updatedUser);
        return "redirect:/profile";
    }

    public void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Impossible d'enregistrer le fichier : " + fileName, e);
        }
    }
}
