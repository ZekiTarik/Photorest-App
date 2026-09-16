package com.tarikturkdil.controller;

import java.util.List;

import com.tarikturkdil.dto.CommentCreateRequest;
import com.tarikturkdil.dto.CommentResponse;
import com.tarikturkdil.dto.PinCreateRequest;
import com.tarikturkdil.dto.PinResponse;

public interface IPinController {

	public RootEntity<PinResponse> createPin(PinCreateRequest input);

    public RootEntity<List<PinResponse>> getMyPins();
    
    public RootEntity<List<PinResponse>> getFeed();
    
    public RootEntity<PinResponse> getPinById(Long pinId);

    public RootEntity<String> deletePin(Long pinId);
    
 // --- Beğeni işlemleri ---
    public RootEntity<String> likePin(Long pinId);

    public RootEntity<String> unlikePin(Long pinId);

    public RootEntity<Long> getLikeCount(Long pinId);
    
 // --- Yorum İşlemleri --- 
    public RootEntity<CommentResponse> addComment(Long pinId, CommentCreateRequest input);

    public RootEntity<List<CommentResponse>> getComments(Long pinId);

    public RootEntity<String> deleteComment(Long commentId);
    
    // --- Arama İşlemi ---
    public RootEntity<List<PinResponse>> searchPins(String query);
}
