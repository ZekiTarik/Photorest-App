package com.tarikturkdil.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tarikturkdil.dto.NotificationEvent;
import com.tarikturkdil.entity.Like;
import com.tarikturkdil.entity.NotificationType;
import com.tarikturkdil.entity.Pin;
import com.tarikturkdil.entity.User;
import com.tarikturkdil.exception.BaseException;
import com.tarikturkdil.exception.ErrorMessage;
import com.tarikturkdil.exception.MessageType;
import com.tarikturkdil.kafka.NotificationEventProducer;
import com.tarikturkdil.repository.LikeRepository;
import com.tarikturkdil.repository.PinRepository;
import com.tarikturkdil.repository.UserRepository;
import com.tarikturkdil.service.ILikeService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LikeServiceImpl implements ILikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PinRepository pinRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationEventProducer notificationEventProducer;

    // --- 1. BEĞEN ---
    @Override
    @Transactional
    public void likePin(Long pinId) {
        User currentUser = getCurrentUser();

        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PIN_NOT_FOUND, null)));

        if (likeRepository.existsByUserIdAndPinId(currentUser.getId(), pinId)) {
            throw new BaseException(new ErrorMessage(MessageType.ALREADY_LIKED, null));
        }

        Like like = new Like();
        like.setUser(currentUser);
        like.setPin(pin);
        likeRepository.save(like);

        log.info("Pin beğenildi: pinId={}, kullanıcı={}", pinId, currentUser.getEmail());

        if (!pin.getUser().getId().equals(currentUser.getId())) {
            notificationEventProducer.publish(new NotificationEvent(
                    pin.getUser().getId(), currentUser.getId(), NotificationType.PIN_LIKED, pinId
            ));
        }
    }

    // --- 2. BEĞENİYİ GERİ AL ---
    @Override
    @Transactional
    public void unlikePin(Long pinId) {
        User currentUser = getCurrentUser();

        if (!likeRepository.existsByUserIdAndPinId(currentUser.getId(), pinId)) {
            throw new BaseException(new ErrorMessage(MessageType.LIKE_NOT_FOUND, null));
        }

        likeRepository.deleteByUserIdAndPinId(currentUser.getId(), pinId);
        log.info("Beğeni geri alındı: pinId={}, kullanıcı={}", pinId, currentUser.getEmail());
    }

    // --- 3. BEĞENİ SAYISI ---
    @Override
    public long getLikeCount(Long pinId) {
        return likeRepository.countByPinId(pinId);
    }

    // --- 4. GİRİŞ YAPMIŞ KULLANICI BEĞENMİŞ Mİ? ---
    @Override
    public boolean isLikedByCurrentUser(Long pinId) {
        User currentUser = getCurrentUser();
        return likeRepository.existsByUserIdAndPinId(currentUser.getId(), pinId);
    }

    // --- YARDIMCI METOT ---
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, null)));
    }
}