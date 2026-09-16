package com.tarikturkdil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tarikturkdil.entity.Follow;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

    List<Follow> findByFollowerId(Long followerId); // ben kimleri takip ediyorum

    List<Follow> findByFollowingId(Long followingId); // beni kimler takip ediyor

    long countByFollowingId(Long followingId); // takipçi sayısı

    long countByFollowerId(Long followerId); // takip edilen sayısı
}