package com.tarikturkdil.service;

import java.util.List;

import com.tarikturkdil.dto.CommentCreateRequest;
import com.tarikturkdil.dto.CommentResponse;

public interface ICommentService {

	CommentResponse addComment(Long pinId, CommentCreateRequest input);

    List<CommentResponse> getCommentsByPin(Long pinId);

    void deleteComment(Long commentId);
}
