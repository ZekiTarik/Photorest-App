package com.tarikturkdil.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tarikturkdil.entity.Conversation;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    // İki kullanıcı arasında (hangi sırayla olursa olsun) konuşma var mı?
    @Query("SELECT c FROM Conversation c WHERE " +
           "(c.user1.id = :userAId AND c.user2.id = :userBId) OR " +
           "(c.user1.id = :userBId AND c.user2.id = :userAId)")
    Optional<Conversation> findBetweenUsers(@Param("userAId") Long userAId, @Param("userBId") Long userBId);

    // Giriş yapmış kullanıcının tüm konuşmaları (kendisi user1 ya da user2 olabilir)
    @Query("SELECT c FROM Conversation c WHERE c.user1.id = :userId OR c.user2.id = :userId")
    List<Conversation> findAllByUserId(@Param("userId") Long userId);
}