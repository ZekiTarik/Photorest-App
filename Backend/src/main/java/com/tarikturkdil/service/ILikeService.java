package com.tarikturkdil.service;

public interface ILikeService {

	void likePin(Long pinId);

    void unlikePin(Long pinId);

    long getLikeCount(Long pinId);

    boolean isLikedByCurrentUser(Long pinId);
}
