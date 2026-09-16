package com.tarikturkdil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tarikturkdil.entity.Pin;

@Repository
public interface PinRepository extends JpaRepository<Pin, Long>{

	// Belirli bir kullanıcının paylaştığı tüm pinleri getirir
    List<Pin> findByUserId(Long userId);
    
 // Herkese açık akış: tüm pinleri en yeniden eskiye sıralı getirir
    List<Pin> findAllByOrderByCreateTimeDesc();
    
    List<Pin> findByTitleContainingIgnoreCaseOrderByCreateTimeDesc(String query);
}
