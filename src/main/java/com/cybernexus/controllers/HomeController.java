package com.cybernexus.controllers;

import com.cybernexus.models.ChatRoom;
import com.cybernexus.models.Message;
import com.cybernexus.models.Report;
import com.cybernexus.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

@Controller
public class HomeController {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/")
    public String handleRoot(HttpSession session) {
        // Verificar si el usuario está en la sesión
        User currentUser = (User) session.getAttribute("user");

        if (currentUser != null) {
            return "redirect:/index";
        } else {
            return "redirect:/login";
        }
    }

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

    @GetMapping("/chat/{id}")
    public String showChatRoom(@PathVariable("id") Long id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        ChatRoom chatRoom = entityManager.find(ChatRoom.class, id);

        if (chatRoom == null) {
            return "redirect:/index?error=notFound";
        }

        List<Message> messages = entityManager.createQuery("SELECT m FROM Message m WHERE m.chatRoom.id = :chatRoomId", Message.class)
                .setParameter("chatRoomId", id).getResultList();

        Map<Long, Boolean> reportedMessages = new HashMap<>();
        List<Long> reportedMessageIds = entityManager.createQuery(
                        "SELECT r.message.id FROM Report r WHERE r.reportedBy.id = :userId", Long.class)
                .setParameter("userId", currentUser.getId())
                .getResultList();

        for (Long messageId : reportedMessageIds) {
            reportedMessages.put(messageId, true);
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("chatRoom", chatRoom);
        model.addAttribute("messages", messages);
        model.addAttribute("reportedMessages", reportedMessages);

        return "chatRoom";
    }


    @PostMapping("/chat/{id}/message")
    @Transactional
    public String addMessage(@PathVariable("id") Long chatRoomId, @RequestParam String content, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        ChatRoom chatRoom = entityManager.find(ChatRoom.class, chatRoomId);

        if (chatRoom == null) {
            return "redirect:/index?error=notFound";
        }

        Message message = new Message(chatRoom, currentUser, content);
        entityManager.persist(message);

        return "redirect:/chat/" + chatRoomId;
    }

    @PostMapping("/chat/{chatRoomId}/message/delete/{messageId}")
    @Transactional
    public String deleteMessage(@PathVariable("chatRoomId") Long chatRoomId, @PathVariable("messageId") Long messageId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        Message message = entityManager.find(Message.class, messageId);

        if (message != null && message.getUser().getId().equals(currentUser.getId())) {
            entityManager.remove(message);
        }

        return "redirect:/chat/" + chatRoomId;
    }

    @GetMapping("/create-chat-room")
    public String showCreateChatRoomForm(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);
        return "createChatRoom";
    }

    @PostMapping("/create-chat-room")
    @Transactional
    public String createChatRoom(@RequestParam String name, @RequestParam String description, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(name);
        chatRoom.setDescription(description);
        chatRoom.setCreatedBy(currentUser);
        chatRoom.setCreatedAt(LocalDateTime.now());

        entityManager.persist(chatRoom);

        return "redirect:/index";
    }

    @PostMapping("/chat-room/delete/{id}")
    @Transactional
    public String deleteChatRoom(@PathVariable("id") Long chatRoomId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        ChatRoom chatRoom = entityManager.find(ChatRoom.class, chatRoomId);

        if (chatRoom != null && chatRoom.getCreatedBy().getId().equals(currentUser.getId())) {
            entityManager.remove(chatRoom);
        }

        return "redirect:/index";
    }

    @PostMapping("/chat/{chatRoomId}/message/report/{messageId}")
    @Transactional
    public String reportMessage(@PathVariable("chatRoomId") Long chatRoomId,
                                @PathVariable("messageId") Long messageId,
                                @RequestParam String reason,
                                HttpSession session) {
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        Message message = entityManager.find(Message.class, messageId);

        if (message != null) {
            Report report = new Report();
            report.setMessage(message);
            report.setReportedBy(currentUser);
            report.setReason(reason);
            report.setCreatedAt(LocalDateTime.now());

            entityManager.persist(report);
        }

        return "redirect:/chat/" + chatRoomId;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
