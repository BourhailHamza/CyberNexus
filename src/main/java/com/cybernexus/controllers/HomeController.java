package com.cybernexus.controllers;

import com.cybernexus.models.ChatRoom;
import com.cybernexus.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class HomeController {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/index")
    public String showIndex(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        String query = "SELECT c FROM ChatRoom c";
        List<ChatRoom> chatRooms = entityManager.createQuery(query, ChatRoom.class).getResultList();

        model.addAttribute("user", currentUser);
        model.addAttribute("chatRooms", chatRooms);

        return "index";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
