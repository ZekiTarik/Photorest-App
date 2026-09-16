package com.tarikturkdil.service;

import java.util.List;

import com.tarikturkdil.dto.BoardCreateRequest;
import com.tarikturkdil.dto.BoardResponse;

public interface IBoardService {

	BoardResponse createBoard(BoardCreateRequest input);

    List<BoardResponse> getMyBoards();

    void deleteBoard(Long boardId);
}
