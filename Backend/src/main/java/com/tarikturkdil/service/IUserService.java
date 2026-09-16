package com.tarikturkdil.service;

import java.util.List;

import com.tarikturkdil.dto.MyAccountResponse;
import com.tarikturkdil.dto.UserSummaryResponse;

public interface IUserService {

    MyAccountResponse getMyAccount();
    
    MyAccountResponse updateAvatar(org.springframework.web.multipart.MultipartFile file);
    
    List<UserSummaryResponse> searchUsers(String query);
}