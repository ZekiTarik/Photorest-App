package com.tarikturkdil.controller;

import java.util.List;

import com.tarikturkdil.dto.MyAccountResponse;
import com.tarikturkdil.dto.UserSummaryResponse;

public interface IUserController {

    public RootEntity<MyAccountResponse> getMyAccount();
    
    public RootEntity<MyAccountResponse> updateAvatar(org.springframework.web.multipart.MultipartFile file);

    public RootEntity<String> followUser(Long userId);

    public RootEntity<String> unfollowUser(Long userId);

    public RootEntity<List<UserSummaryResponse>> getFollowers(Long userId);

    public RootEntity<List<UserSummaryResponse>> getFollowing(Long userId);
    
    public RootEntity<List<UserSummaryResponse>> searchUsers(String query);
}