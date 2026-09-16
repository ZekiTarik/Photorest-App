package com.tarikturkdil.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tarikturkdil.dto.MyAccountResponse;
import com.tarikturkdil.dto.UserSummaryResponse;
import com.tarikturkdil.entity.User;
import com.tarikturkdil.exception.BaseException;
import com.tarikturkdil.exception.ErrorMessage;
import com.tarikturkdil.exception.MessageType;
import com.tarikturkdil.repository.UserRepository;
import com.tarikturkdil.service.CloudinaryUploadResult;
import com.tarikturkdil.service.ICloudinaryService;
import com.tarikturkdil.service.IUserService;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ICloudinaryService cloudinaryService;

    @Override
    public MyAccountResponse getMyAccount() {
        User user = getCurrentUser();
        return mapToMyAccountResponse(user);
    }

    @Override
    @Transactional
    public MyAccountResponse updateAvatar(MultipartFile file) {
        User user = getCurrentUser();

        CloudinaryUploadResult uploadResult = cloudinaryService.uploadImage(file);
        user.setAvatarUrl(uploadResult.getUrl());

        User savedUser = userRepository.save(user);
        return mapToMyAccountResponse(savedUser);
    }
    
    @Override
    public List<UserSummaryResponse> searchUsers(String query) {
        List<User> users = userRepository.findByNameContainingIgnoreCase(query);
        return users.stream()
                .map(this::mapToUserSummaryResponse)
                .collect(Collectors.toList());
    }

    private UserSummaryResponse mapToUserSummaryResponse(User user) {
        UserSummaryResponse response = new UserSummaryResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setAvatarUrl(user.getAvatarUrl());
        return response;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, null)));
    }

    private MyAccountResponse mapToMyAccountResponse(User user) {
        MyAccountResponse response = new MyAccountResponse();
        BeanUtils.copyProperties(user, response);
        response.setUsername(user.getName());
        return response;
    }
}