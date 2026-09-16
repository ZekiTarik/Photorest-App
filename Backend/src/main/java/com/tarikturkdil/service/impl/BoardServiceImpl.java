package com.tarikturkdil.service.impl;

import java.util.List;import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tarikturkdil.dto.BoardCreateRequest;
import com.tarikturkdil.dto.BoardResponse;
import com.tarikturkdil.entity.Board;
import com.tarikturkdil.entity.SavedPin;
import com.tarikturkdil.entity.User;
import com.tarikturkdil.exception.BaseException;
import com.tarikturkdil.exception.ErrorMessage;
import com.tarikturkdil.exception.MessageType;
import com.tarikturkdil.repository.BoardRepository;
import com.tarikturkdil.repository.SavedPinRepository;
import com.tarikturkdil.repository.UserRepository;
import com.tarikturkdil.service.IBoardService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BoardServiceImpl implements IBoardService{

	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private SavedPinRepository savedPinRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	//1.PANO OLUŞTURMA
	@Override
	@Transactional
	public BoardResponse createBoard(BoardCreateRequest input) {
		User currentUser = getCurrentUser();
		
		Board board = new Board();
		board.setName(input.getName());
		board.setDescription(input.getDescription());
		board.setSecret(input.isSecret());
		board.setUser(currentUser);
		
		Board savedBoard = boardRepository.save(board);
		log.info("Yeni bir pano oluşturuldu: {} (sahibi: {})", savedBoard.getName(), currentUser.getEmail());
		
		return mapToBoardResponse(savedBoard);
	}

	//2.KENDİ PANOLARINI LİSTELEME
	@Override
	public List<BoardResponse> getMyBoards() {
		User currentUser = getCurrentUser();
		List<Board> boards = boardRepository.findByUserId(currentUser.getId());
		
		return boards.stream()
				.map(this::mapToBoardResponse)
				.collect(Collectors.toList());
	}

	//3.PANO SİLME
	@Override
	@Transactional
	public void deleteBoard(Long boardId) {
		User currentUser = getCurrentUser();
		
		Board board = boardRepository.findById(boardId)
				.orElseThrow(()-> new BaseException(new ErrorMessage(MessageType.BOARD_NOT_FOUND,null)));
		
		//Sadece panonun sahibi silebilir
		if(!board.getUser().getId().equals(currentUser.getId())) {
			log.warn("Yetkisiz pano silme denemesi: {} kullanıcısı başkasının panosunu silmeye çalıştı.", currentUser.getEmail());
			throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED_ACTION,null));
		}
		// Pano silinirken içindeki pinler SİLİNMEZ, sadece bu panoya ait
        // "kaydedilmiş pin" bağlantıları (SavedPin) temizlenir.
		
		List<SavedPin> savedPins = savedPinRepository.findByBoardId(boardId);
        savedPinRepository.deleteAll(savedPins);

        boardRepository.delete(board);
        log.info("Pano silindi: {} (sahibi: {})", board.getName(), currentUser.getEmail());
	}
	
	//YARDIMCI METOTLAR
	private User getCurrentUser() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByEmail(email)
				.orElseThrow(()-> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND,null)));
	}
	
	private BoardResponse mapToBoardResponse(Board board) {
		BoardResponse response = new BoardResponse();
		BeanUtils.copyProperties(board, response);
		return response;
	}

}
