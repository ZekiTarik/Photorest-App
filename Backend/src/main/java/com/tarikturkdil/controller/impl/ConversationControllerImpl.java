package com.tarikturkdil.controller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tarikturkdil.controller.IConversationController;
import com.tarikturkdil.controller.RestBaseController;
import com.tarikturkdil.controller.RootEntity;
import com.tarikturkdil.dto.ConversationResponse;
import com.tarikturkdil.dto.MessageResponse;
import com.tarikturkdil.service.IConversationService;

@RestController
@RequestMapping("/conversations")
public class ConversationControllerImpl extends RestBaseController implements IConversationController {

    @Autowired
    private IConversationService conversationService;

    @PostMapping("/{targetUserId}")
    @Override
    public RootEntity<ConversationResponse> startOrGetConversation(@PathVariable Long targetUserId) {
        ConversationResponse response = conversationService.startOrGetConversation(targetUserId);
        return ok(response);
    }

    @GetMapping
    @Override
    public RootEntity<List<ConversationResponse>> getMyConversations() {
        List<ConversationResponse> response = conversationService.getMyConversations();
        return ok(response);
    }

    @GetMapping("/{conversationId}/messages")
    @Override
    public RootEntity<List<MessageResponse>> getMessages(@PathVariable Long conversationId) {
        List<MessageResponse> response = conversationService.getMessages(conversationId);
        return ok(response);
    }
    
    @PostMapping("/{conversationId}/read")
    @Override
    public RootEntity<String> markMessagesAsRead(@PathVariable Long conversationId) {
        conversationService.markMessagesAsRead(conversationId);
        return ok("Mesajlar okundu olarak işaretlendi.");
    }
}