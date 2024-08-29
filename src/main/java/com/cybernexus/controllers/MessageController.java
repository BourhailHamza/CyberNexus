package com.cybernexus.controllers;

import com.cybernexus.models.Message;
import com.cybernexus.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

@Controller
public class MessageController {

    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping("/message/report/{id}")
    @Transactional
    public String reportMessage(@PathVariable("id") Long messageId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        Message message = entityManager.find(Message.class, messageId);

        if (message != null) {
            message.setReported(true);
            entityManager.merge(message);
        }

        return "redirect:/chat/" + message.getChatRoom().getId();
    }
}
