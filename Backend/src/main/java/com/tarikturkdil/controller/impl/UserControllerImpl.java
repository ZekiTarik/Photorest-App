package com.tarikturkdil.controller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tarikturkdil.controller.IUserController;
import com.tarikturkdil.controller.RestBaseController;
import com.tarikturkdil.controller.RootEntity;
import com.tarikturkdil.dto.MyAccountResponse;
import com.tarikturkdil.dto.UserSummaryResponse;
import com.tarikturkdil.service.IFollowService;
import com.tarikturkdil.service.IUserService;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UserControllerImpl extends RestBaseController implements IUserController {

    @Autowired
    private IFollowService followService;

    @Autowired
    private IUserService userService;

    @GetMapping("/me")
    @Override
    public RootEntity<MyAccountResponse> getMyAccount() {
        return ok(userService.getMyAccount());
    }

    @PostMapping("/{userId}/follow")
    @Override
    public RootEntity<String> followUser(@PathVariable Long userId) {
        followService.followUser(userId);
        return ok("Kullanıcı takip edildi.");
    }
    
    @PostMapping(value = "/me/avatar", consumes = "multipart/form-data")
    @Override
    public RootEntity<MyAccountResponse> updateAvatar(@RequestPart("image") MultipartFile file) {
        return ok(userService.updateAvatar(file));
    }

    @DeleteMapping("/{userId}/follow")
    @Override
    public RootEntity<String> unfollowUser(@PathVariable Long userId) {
        followService.unfollowUser(userId);
        return ok("Takip bırakıldı.");
    }

    @GetMapping("/{userId}/followers")
    @Override
    public RootEntity<List<UserSummaryResponse>> getFollowers(@PathVariable Long userId) {
        return ok(followService.getFollowers(userId));
    }

    @GetMapping("/{userId}/following")
    @Override
    public RootEntity<List<UserSummaryResponse>> getFollowing(@PathVariable Long userId) {
        return ok(followService.getFollowing(userId));
    }
    
    @GetMapping("/search")
    @Override
    public RootEntity<List<UserSummaryResponse>> searchUsers(@RequestParam String query) {
        return ok(userService.searchUsers(query));
    }
}