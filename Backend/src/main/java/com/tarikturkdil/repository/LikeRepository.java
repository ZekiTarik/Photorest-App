package com.tarikturkdil.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tarikturkdil.entity.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long>{

	Optional<Like> findByUserIdAndPinId(Long userId, Long pinId);

    boolean existsByUserIdAndPinId(Long userId, Long pinId);

    long countByPinId(Long pinId);

    void deleteByUserIdAndPinId(Long userId, Long pinId);
    
    List<Like> findByPinId(Long pinId);
    
    @Query("SELECT l.pin.id FROM Like l WHERE l.user.id = :userId")
    Set<Long> findLikedPinIdsByUserId(@Param("userId") Long userId);
}
