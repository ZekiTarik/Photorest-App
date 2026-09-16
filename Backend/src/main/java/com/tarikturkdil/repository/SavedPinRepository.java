package com.tarikturkdil.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tarikturkdil.entity.SavedPin;

@Repository
public interface SavedPinRepository extends JpaRepository<SavedPin, Long> {

    List<SavedPin> findByBoardId(Long boardId);

    List<SavedPin> findByPinId(Long pinId);

    Optional<SavedPin> findByBoardIdAndPinId(Long boardId, Long pinId);

    boolean existsByBoardIdAndPinId(Long boardId, Long pinId);

    void deleteByBoardIdAndPinId(Long boardId, Long pinId);
    
    boolean existsByPinIdAndBoard_UserId(Long pinId, Long userId);
    
    @Query("SELECT sp.pin.id FROM SavedPin sp WHERE sp.board.user.id = :userId")
    Set<Long> findSavedPinIdsByUserId(@Param("userId") Long userId);
}