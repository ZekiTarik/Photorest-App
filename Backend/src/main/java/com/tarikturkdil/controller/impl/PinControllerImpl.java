package com.tarikturkdil.controller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tarikturkdil.controller.IPinController;
import com.tarikturkdil.controller.RestBaseController;
import com.tarikturkdil.controller.RootEntity;
import com.tarikturkdil.dto.CommentCreateRequest;
import com.tarikturkdil.dto.CommentResponse;
import com.tarikturkdil.dto.PinCreateRequest;
import com.tarikturkdil.dto.PinResponse;
import com.tarikturkdil.service.ICommentService;
import com.tarikturkdil.service.ILikeService;
import com.tarikturkdil.service.IPinService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pins")
public class PinControllerImpl extends RestBaseController implements IPinController {

    @Autowired
    private IPinService pinService;
    
    @Autowired
    private ILikeService likeService;
    
    @Autowired
    private ICommentService commentService;

    @PostMapping(consumes = "multipart/form-data")
    @Override
    public RootEntity<PinResponse> createPin(@Valid @ModelAttribute PinCreateRequest input) {
        PinResponse response = pinService.createPin(input);
        return ok(response);
    }

    @GetMapping
    @Override
    public RootEntity<List<PinResponse>> getMyPins() {
        List<PinResponse> response = pinService.getMyPins();
        return ok(response);
    }
    
    @GetMapping("/feed")
    @Override
    public RootEntity<List<PinResponse>> getFeed() {
        List<PinResponse> response = pinService.getFeed();
        return ok(response);
    }
    
    @GetMapping("/{pinId}")
    @Override
    public RootEntity<PinResponse> getPinById(@PathVariable Long pinId) {
        PinResponse response = pinService.getPinById(pinId);
        return ok(response);
    }

    @DeleteMapping("/{pinId}")
    @Override
    public RootEntity<String> deletePin(@PathVariable Long pinId) {
        pinService.deletePin(pinId);
        return ok("Pin başarıyla silindi.");
    }

    //BEĞENİ İŞLEMLERİ
    @PostMapping("/{pinId}/like")
    @Override
    public RootEntity<String> likePin(@PathVariable Long pinId) {
        likeService.likePin(pinId);
        return ok("Pin beğenildi.");
    }

    @DeleteMapping("/{pinId}/like")
    @Override
    public RootEntity<String> unlikePin(@PathVariable Long pinId) {
        likeService.unlikePin(pinId);
        return ok("Beğeni geri alındı.");
    }

    @GetMapping("/{pinId}/like/count")
    @Override
    public RootEntity<Long> getLikeCount(@PathVariable Long pinId) {
        return ok(likeService.getLikeCount(pinId));
    }
    
    //YORUM İŞLEMLERİ
    @PostMapping("/{pinId}/comments")
    @Override
    public RootEntity<CommentResponse> addComment(@PathVariable Long pinId, @Valid @RequestBody CommentCreateRequest input) {
        CommentResponse response = commentService.addComment(pinId, input);
        return ok(response);
    }

    @GetMapping("/{pinId}/comments")
    @Override
    public RootEntity<List<CommentResponse>> getComments(@PathVariable Long pinId) {
    	List<CommentResponse> response = commentService.getCommentsByPin(pinId);
        return ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    @Override
    public RootEntity<String> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ok("Yorum silindi.");
    }
    
    @GetMapping("/search")
    @Override
    public RootEntity<List<PinResponse>> searchPins(@RequestParam String query) {
        List<PinResponse> response = pinService.searchPins(query);
        return ok(response);
    }
}