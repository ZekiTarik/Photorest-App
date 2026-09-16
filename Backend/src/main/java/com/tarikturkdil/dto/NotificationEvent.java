package com.tarikturkdil.dto;

import com.tarikturkdil.entity.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private Long recipientId;
    private Long actorId;
    private NotificationType type;
    private Long referenceId;
}