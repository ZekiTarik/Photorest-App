package com.tarikturkdil.service;

import java.util.List;

import com.tarikturkdil.dto.ConversationResponse;
import com.tarikturkdil.dto.MessageResponse;
import com.tarikturkdil.dto.SendMessageRequest;

public interface IConversationService {

    ConversationResponse startOrGetConversation(Long targetUserId);

    List<ConversationResponse> getMyConversations();

    List<MessageResponse> getMessages(Long conversationId);

    MessageResponse sendMessage(Long conversationId, SendMessageRequest input);
    
    void markMessagesAsRead(Long conversationId);
}