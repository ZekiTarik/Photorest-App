package com.tarikturkdil.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tarikturkdil.dto.PinCreateRequest;
import com.tarikturkdil.dto.PinResponse;
import com.tarikturkdil.entity.Pin;
import com.tarikturkdil.entity.User;
import com.tarikturkdil.exception.BaseException;
import com.tarikturkdil.exception.ErrorMessage;
import com.tarikturkdil.exception.MessageType;
import com.tarikturkdil.repository.CommentRepository;
import com.tarikturkdil.repository.LikeRepository;
import com.tarikturkdil.repository.PinRepository;
import com.tarikturkdil.repository.SavedPinRepository;
import com.tarikturkdil.repository.UserRepository;
import com.tarikturkdil.service.CloudinaryUploadResult;
import com.tarikturkdil.service.ICloudinaryService;
import com.tarikturkdil.service.IPinService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PinServiceImpl implements IPinService {

    @Autowired
    private PinRepository pinRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ICloudinaryService cloudinaryService;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private SavedPinRepository savedPinRepository;

    //1.PİN OLUŞTURMA
    @Override
    @Transactional
    public PinResponse createPin(PinCreateRequest input) {
        User currentUser = getCurrentUser();

        CloudinaryUploadResult uploadResult = cloudinaryService.uploadImage(input.getImage());

        Pin pin = new Pin();
        pin.setTitle(input.getTitle());
        pin.setDescription(input.getDescription());
        pin.setImageUrl(uploadResult.getUrl());
        pin.setImagePublicId(uploadResult.getPublicId());
        pin.setUser(currentUser);

        Pin savedPin = pinRepository.save(pin);
        log.info("Yeni pin oluşturuldu: {} (sahibi: {})", savedPin.getTitle(), currentUser.getEmail());

        // Yeni oluşturulan pin, sahibi tarafından henüz beğenilmemiştir
        return mapToPinResponse(savedPin, false, false);
    }

    //2.KENDİ PİNLERİNİ LİSTELEME
    @Override
    public List<PinResponse> getMyPins() {
        User currentUser = getCurrentUser();
        List<Pin> pins = pinRepository.findByUserId(currentUser.getId());
        Set<Long> likedPinIds = likeRepository.findLikedPinIdsByUserId(currentUser.getId());
        Set<Long> savedPinIds = savedPinRepository.findSavedPinIdsByUserId(currentUser.getId());

        return pins.stream()
                .map(pin -> mapToPinResponse(pin, likedPinIds.contains(pin.getId()), savedPinIds.contains(pin.getId())))
                .collect(Collectors.toList());
    }

    //2.5 HERKESE AÇIK AKIŞ (FEED)
    @Override
    public List<PinResponse> getFeed() {
        User currentUser = getCurrentUser();
        List<Pin> pins = pinRepository.findAllByOrderByCreateTimeDesc();
        Set<Long> likedPinIds = likeRepository.findLikedPinIdsByUserId(currentUser.getId());
        Set<Long> savedPinIds = savedPinRepository.findSavedPinIdsByUserId(currentUser.getId());

        return pins.stream()
                .map(pin -> mapToPinResponse(pin, likedPinIds.contains(pin.getId()), savedPinIds.contains(pin.getId())))
                .collect(Collectors.toList());
    }

    //2.6 TEK BİR PİNİ ID İLE GETİRME
    @Override
    public PinResponse getPinById(Long pinId) {
        User currentUser = getCurrentUser();

        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PIN_NOT_FOUND, null)));

        boolean isLiked = likeRepository.existsByUserIdAndPinId(currentUser.getId(), pinId);
        boolean isSaved = savedPinRepository.existsByPinIdAndBoard_UserId(pinId, currentUser.getId());

        return mapToPinResponse(pin, isLiked, isSaved);
    }

    //3.PIN SİLME
    @Override
    @Transactional
    public void deletePin(Long pinId) {
        User currentUser = getCurrentUser();

        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PIN_NOT_FOUND, null)));

        if (!pin.getUser().getId().equals(currentUser.getId())) {
            log.warn("Yetkisiz pin silme denemesi: {} kullanıcısı başkasının pinini silmeye çalıştı.", currentUser.getEmail());
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED_ACTION, null));
        }

        likeRepository.deleteAll(likeRepository.findByPinId(pinId));
        commentRepository.deleteAll(commentRepository.findByPinIdOrderByCreateTimeDesc(pinId));
        savedPinRepository.deleteAll(savedPinRepository.findByPinId(pinId));

        cloudinaryService.deleteImage(pin.getImagePublicId());
        pinRepository.delete(pin);

        log.info("Pin silindi: {} (sahibi: {})", pin.getTitle(), currentUser.getEmail());
    }

    
    @Override
    public List<PinResponse> searchPins(String query) {
        User currentUser = getCurrentUser();
        List<Pin> pins = pinRepository.findByTitleContainingIgnoreCaseOrderByCreateTimeDesc(query);

        Set<Long> likedPinIds = likeRepository.findLikedPinIdsByUserId(currentUser.getId());
        Set<Long> savedPinIds = savedPinRepository.findSavedPinIdsByUserId(currentUser.getId());

        return pins.stream()
                .map(pin -> mapToPinResponse(pin, likedPinIds.contains(pin.getId()), savedPinIds.contains(pin.getId())))
                .collect(Collectors.toList());
    }
    
    
    //YARDIMCI METOTLAR
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, null)));
    }

    private PinResponse mapToPinResponse(Pin pin, boolean isLikedByMe, boolean isSavedByMe) {
        PinResponse response = new PinResponse();
        BeanUtils.copyProperties(pin, response);
        response.setOwnerUsername(pin.getUser().getName());
        response.setLikedByMe(isLikedByMe);
        response.setSavedByMe(isSavedByMe);
        return response;
    }
}