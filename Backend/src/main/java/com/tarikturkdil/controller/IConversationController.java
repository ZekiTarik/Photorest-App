package com.tarikturkdil.controller;

import java.util.List;

import com.tarikturkdil.dto.ConversationResponse;
import com.tarikturkdil.dto.MessageResponse;

public interface IConversationController {

    public RootEntity<ConversationResponse> startOrGetConversation(Long targetUserId);

    public RootEntity<List<ConversationResponse>> getMyConversations();

    public RootEntity<List<MessageResponse>> getMessages(Long conversationId);
    
    public RootEntity<String> markMessagesAsRead(Long conversationId);
}