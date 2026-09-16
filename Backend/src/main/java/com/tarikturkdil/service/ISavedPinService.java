package com.tarikturkdil.service;

import java.util.List;

import com.tarikturkdil.dto.PinResponse;

public interface ISavedPinService {

	void savePinToBoard(Long boardId, Long pinId);

    void removePinFromBoard(Long boardId, Long pinId);

    List<PinResponse> getPinsInBoard(Long boardId);
}
