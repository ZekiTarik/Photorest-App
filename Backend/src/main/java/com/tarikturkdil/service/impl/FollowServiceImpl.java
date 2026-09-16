package com.tarikturkdil.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tarikturkdil.dto.NotificationEvent;
import com.tarikturkdil.dto.UserSummaryResponse;
import com.tarikturkdil.entity.Follow;
import com.tarikturkdil.entity.NotificationType;
import com.tarikturkdil.entity.User;
import com.tarikturkdil.exception.BaseException;
import com.tarikturkdil.exception.ErrorMessage;
import com.tarikturkdil.exception.MessageType;
import com.tarikturkdil.kafka.NotificationEventProducer;
import com.tarikturkdil.repository.FollowRepository;
import com.tarikturkdil.repository.UserRepository;
import com.tarikturkdil.service.IFollowService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FollowServiceImpl implements IFollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationEventProducer notificationEventProducer;


    // --- 1. TAKİP ET ---
    @Override
    @Transactional
    public void followUser(Long targetUserId) {
        User currentUser = getCurrentUser();

        if (currentUser.getId().equals(targetUserId)) {
            throw new BaseException(new ErrorMessage(MessageType.CANNOT_FOLLOW_YOURSELF, null));
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, null)));

        if (followRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), targetUserId)) {
            throw new BaseException(new ErrorMessage(MessageType.ALREADY_FOLLOWING, null));
        }

        Follow follow = new Follow();
        follow.setFollower(currentUser);
        follow.setFollowing(targetUser);
        followRepository.save(follow);
        
        notificationEventProducer.publish(new NotificationEvent(
                targetUser.getId(), currentUser.getId(), NotificationType.NEW_FOLLOWER, null
        ));

        log.info("Takip edildi: {} -> {}", currentUser.getEmail(), targetUser.getEmail());
    }

    // --- 2. TAKİBİ BIRAK ---
    @Override
    @Transactional
    public void unfollowUser(Long targetUserId) {
        User currentUser = getCurrentUser();

        if (!followRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), targetUserId)) {
            throw new BaseException(new ErrorMessage(MessageType.FOLLOW_NOT_FOUND, null));
        }

        followRepository.deleteByFollowerIdAndFollowingId(currentUser.getId(), targetUserId);
        log.info("Takip bırakıldı: {} -> targetUserId={}", currentUser.getEmail(), targetUserId);
    }

    // --- 3. TAKİPÇİLERİ LİSTELE ---
    @Override
    public List<UserSummaryResponse> getFollowers(Long userId) {
        List<Follow> follows = followRepository.findByFollowingId(userId);
        return follows.stream()
                .map(follow -> mapToUserSummary(follow.getFollower()))
                .collect(Collectors.toList());
    }

    // --- 4. TAKİP EDİLENLERİ LİSTELE ---
    @Override
    public List<UserSummaryResponse> getFollowing(Long userId) {
        List<Follow> follows = followRepository.findByFollowerId(userId);
        return follows.stream()
                .map(follow -> mapToUserSummary(follow.getFollowing()))
                .collect(Collectors.toList());
    }

    // --- 5-6. SAYILAR ---
    @Override
    public long getFollowerCount(Long userId) {
        return followRepository.countByFollowingId(userId);
    }

    @Override
    public long getFollowingCount(Long userId) {
        return followRepository.countByFollowerId(userId);
    }

    // --- YARDIMCI METOTLAR ---

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, null)));
    }

    private UserSummaryResponse mapToUserSummary(User user) {
        UserSummaryResponse response = new UserSummaryResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setAvatarUrl(user.getAvatarUrl());
        return response;
    }
}