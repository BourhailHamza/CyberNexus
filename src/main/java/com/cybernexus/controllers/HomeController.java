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

@Controller
public class HomeController {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/index")
    public String showIndex(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");

        /*if (currentUser == null) {
            return "redirect:/login";
        }*/

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

        // Formatage de la date
        /*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
        for (Message message : messages) {
            message.setFormattedDate(message.getCreatedAt().format(formatter));
        }*/

        model.addAttribute("user", currentUser);
        model.addAttribute("chatRoom", chatRoom);
        model.addAttribute("messages", messages);

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
        chatRoom.setCreatedBy(currentUser); // Establecemos el usuario como entidad relacionada
        chatRoom.setCreatedAt(LocalDateTime.now());
        chatRoom.setUserId(currentUser.getId());

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
