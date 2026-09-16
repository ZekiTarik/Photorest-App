package com.tarikturkdil.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ConversationResponse extends DtoBase {
    private UserSummaryResponse otherUser;
    private String lastMessagePreview;
    private LocalDateTime lastMessageTime;
    private long unreadCount;
}