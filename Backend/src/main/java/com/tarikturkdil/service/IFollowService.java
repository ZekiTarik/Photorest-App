package com.tarikturkdil.service;

import java.util.List;

import com.tarikturkdil.dto.UserSummaryResponse;

public interface IFollowService {

    void followUser(Long targetUserId);

    void unfollowUser(Long targetUserId);

    List<UserSummaryResponse> getFollowers(Long userId);

    List<UserSummaryResponse> getFollowing(Long userId);

    long getFollowerCount(Long userId);

    long getFollowingCount(Long userId);
}