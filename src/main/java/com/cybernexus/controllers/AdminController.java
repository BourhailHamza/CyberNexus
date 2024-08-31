package com.cybernexus.controllers;

import com.cybernexus.models.Message;
import com.cybernexus.models.Report;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/reports")
    public String viewReportedMessages(Model model) {
        List<Report> reports = entityManager.createQuery("SELECT r FROM Report r", Report.class).getResultList();
        model.addAttribute("reports", reports);
        return "admin";
    }

    @PostMapping("/message/delete/{id}")
    @Transactional
    public String deleteMessage(@PathVariable("id") Long messageId) {
        Message message = entityManager.find(Message.class, messageId);
        if (message != null) {
            entityManager.remove(message);
        } else {
            return "redirect:/admin/reports?error=messageNotFound";
        }
        return "redirect:/admin/reports";
    }

    @PostMapping("/message/unreport/{id}")
    @Transactional
    public String unreportMessage(@PathVariable("id") Long reportId) {
        Report report = entityManager.find(Report.class, reportId);
        if (report != null) {
            entityManager.remove(report);
        } else {
            return "redirect:/admin/reports?error=reportNotFound";
        }
        return "redirect:/admin/reports";
    }
}
