package com.tarikturkdil.controller;

import java.util.List;

import com.tarikturkdil.dto.BoardCreateRequest;
import com.tarikturkdil.dto.BoardResponse;
import com.tarikturkdil.dto.PinResponse;

public interface IBoardController {
	public RootEntity<BoardResponse> createBoard(BoardCreateRequest input);

    public RootEntity<List<BoardResponse>> getMyBoards();

    public RootEntity<String> deleteBoard(Long boardId);
    
 // --- Pano içindeki pinlerle ilgili işlemler ---
    public RootEntity<String> savePinToBoard(Long boardId, Long pinId);

    public RootEntity<String> removePinFromBoard(Long boardId, Long pinId);

    public RootEntity<List<PinResponse>> getPinsInBoard(Long boardId);
}
