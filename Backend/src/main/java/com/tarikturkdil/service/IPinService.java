package com.tarikturkdil.service;

import java.util.List;

import com.tarikturkdil.dto.PinCreateRequest;
import com.tarikturkdil.dto.PinResponse;

public interface IPinService {

    PinResponse createPin(PinCreateRequest input);

    List<PinResponse> getMyPins();

    List<PinResponse> getFeed();
    
    List<PinResponse> searchPins(String query);

    PinResponse getPinById(Long pinId);

    void deletePin(Long pinId);
}
