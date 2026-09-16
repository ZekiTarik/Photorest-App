package com.tarikturkdil.controller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tarikturkdil.controller.IBoardController;
import com.tarikturkdil.controller.RestBaseController;
import com.tarikturkdil.controller.RootEntity;
import com.tarikturkdil.dto.BoardCreateRequest;
import com.tarikturkdil.dto.BoardResponse;
import com.tarikturkdil.dto.PinResponse;
import com.tarikturkdil.service.IBoardService;
import com.tarikturkdil.service.ISavedPinService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/boards")
public class BoardControllerImpl extends RestBaseController implements IBoardController{

	@Autowired
	private IBoardService boardService;
	
	@Autowired
    private ISavedPinService savedPinService;
	
	@PostMapping
	@Override
	public RootEntity<BoardResponse> createBoard(@Valid @RequestBody BoardCreateRequest input) {
		BoardResponse response = boardService.createBoard(input);
		return ok(response);
	}

	@GetMapping
	@Override
	public RootEntity<List<BoardResponse>> getMyBoards() {
		List<BoardResponse> response = boardService.getMyBoards();
		return ok(response);
	}

	@DeleteMapping("/{boardId}")
	@Override
	public RootEntity<String> deleteBoard(@PathVariable Long boardId) {
		boardService.deleteBoard(boardId);
		return ok("Pano başarıyla silindi");
	}
	
	
	// --- Pano içindeki pinlerle ilgili işlemler ---

    @PostMapping("/{boardId}/pins/{pinId}")
    @Override
    public RootEntity<String> savePinToBoard(@PathVariable Long boardId, @PathVariable Long pinId) {
        savedPinService.savePinToBoard(boardId, pinId);
        return ok("Pin panoya kaydedildi.");
    }

    @DeleteMapping("/{boardId}/pins/{pinId}")
    @Override
    public RootEntity<String> removePinFromBoard(@PathVariable Long boardId, @PathVariable Long pinId) {
        savedPinService.removePinFromBoard(boardId, pinId);
        return ok("Pin panodan kaldırıldı.");
    }

    @GetMapping("/{boardId}/pins")
    @Override
    public RootEntity<List<PinResponse>> getPinsInBoard(@PathVariable Long boardId) {
        List<PinResponse> response = savedPinService.getPinsInBoard(boardId);
        return ok(response);
    }

}
