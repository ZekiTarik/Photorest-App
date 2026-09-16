package com.tarikturkdil.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tarikturkdil.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	// E-posta adresine göre kullanıcıyı bulur (Login işlemi için çok önemli)
    Optional<User> findByEmail(String email);
    
    // Kullanıcı adına göre kullanıcıyı bulur
    Optional<User> findByUsername(String username);

    // YENİ: İsme göre arama (büyük/küçük harf duyarsız, kısmi eşleşme)
    List<User> findByNameContainingIgnoreCase(String query);
}