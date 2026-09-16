package com.tarikturkdil.dto;

import lombok.Data;

@Data
public class MessageResponse extends DtoBase {
    private Long senderId;
    private String content;
    private boolean isRead;
    private Long conversationId;
}