package com.tarikturkdil.controller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tarikturkdil.controller.INotificationController;
import com.tarikturkdil.controller.RestBaseController;
import com.tarikturkdil.controller.RootEntity;
import com.tarikturkdil.dto.NotificationResponse;
import com.tarikturkdil.service.INotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationControllerImpl extends RestBaseController implements INotificationController {

    @Autowired
    private INotificationService notificationService;

    @GetMapping
    @Override
    public RootEntity<List<NotificationResponse>> getMyNotifications() {
        return ok(notificationService.getMyNotifications());
    }

    @GetMapping("/unread-count")
    @Override
    public RootEntity<Long> getUnreadCount() {
        return ok(notificationService.getUnreadCount());
    }

    @PostMapping("/{notificationId}/read")
    @Override
    public RootEntity<String> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ok("Bildirim okundu olarak işaretlendi.");
    }

    @PostMapping("/read-all")
    @Override
    public RootEntity<String> markAllAsRead() {
        notificationService.markAllAsRead();
        return ok("Tüm bildirimler okundu olarak işaretlendi.");
    }
}