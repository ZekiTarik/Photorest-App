package com.tarikturkdil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tarikturkdil.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderByCreateTimeAsc(Long conversationId);

    long countByConversationIdAndIsReadFalseAndSenderIdNot(Long conversationId, Long currentUserId);
    
    List<Message> findByConversationIdAndIsReadFalseAndSenderIdNot(Long conversationId, Long senderId);
}