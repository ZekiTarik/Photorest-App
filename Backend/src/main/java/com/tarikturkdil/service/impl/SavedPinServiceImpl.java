package com.tarikturkdil.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tarikturkdil.dto.PinResponse;
import com.tarikturkdil.entity.Board;
import com.tarikturkdil.entity.Pin;
import com.tarikturkdil.entity.SavedPin;
import com.tarikturkdil.entity.User;
import com.tarikturkdil.exception.BaseException;
import com.tarikturkdil.exception.ErrorMessage;
import com.tarikturkdil.exception.MessageType;
import com.tarikturkdil.repository.BoardRepository;
import com.tarikturkdil.repository.PinRepository;
import com.tarikturkdil.repository.SavedPinRepository;
import com.tarikturkdil.repository.UserRepository;
import com.tarikturkdil.service.ISavedPinService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SavedPinServiceImpl implements ISavedPinService{

	@Autowired
	private SavedPinRepository savedPinRepository;
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private PinRepository pinRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	
	//YARDIMCI METOTLAR
	private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, null)));
    }

    private PinResponse mapToPinResponse(Pin pin) {
        PinResponse response = new PinResponse();
        BeanUtils.copyProperties(pin, response);
        return response;
    }

  //1.PİNİ PANOYA KAYDETME
	@Override
	@Transactional
	public void savePinToBoard(Long boardId, Long pinId) {
		User currentUser = getCurrentUser();
		
		Board board = boardRepository.findById(boardId)
				.orElseThrow(()->new BaseException(new ErrorMessage(MessageType.BOARD_NOT_FOUND, null)));
		
		//SadeceKendiPanosunaKaydedebilir
		if(!board.getUser().getId().equals(currentUser.getId())) {
			throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED_ACTION, null));
		}
		
		Pin pin = pinRepository.findById(pinId)
				.orElseThrow(()-> new BaseException(new ErrorMessage(MessageType.PIN_NOT_FOUND, null)));
		
		//Aynı pin aynı panoya tekrar kaydedilmesin
		if(savedPinRepository.existsByBoardIdAndPinId(boardId, pinId)) {
			throw new BaseException(new ErrorMessage(MessageType.PIN_ALREADY_SAVED, null));
		}
		
		SavedPin savedPin = new SavedPin();
		savedPin.setBoard(board);
		savedPin.setPin(pin);
		savedPinRepository.save(savedPin);
		
		log.info("Pin panoya kaydedildi: pinId={}, boardId={}, kullanıcı={}", pinId, boardId, currentUser.getEmail());
		
	}

	//2.PİNİ PANODAN KALDIR
	@Transactional
	@Override
	public void removePinFromBoard(Long boardId, Long pinId) {
		User currentUser = getCurrentUser();
		
		Board board = boardRepository.findById(boardId)
				.orElseThrow(()-> new BaseException(new ErrorMessage(MessageType.BOARD_NOT_FOUND, null)));
		
		if (!board.getUser().getId().equals(currentUser.getId())) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED_ACTION, null));
        }
		
		savedPinRepository.deleteByBoardIdAndPinId(boardId, pinId);
        log.info("Pin panodan kaldırıldı: pinId={}, boardId={}, kullanıcı={}", pinId, boardId, currentUser.getEmail());
		
	}

	//3.PANODAKİ PİNLERİ LİSTELEME
	@Override
	public List<PinResponse> getPinsInBoard(Long boardId) {
		//PanonunVarOlduğunuDoğrulama(yoksaHataDöneriz)
		Board board = boardRepository.findById(boardId)
	            .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BOARD_NOT_FOUND, null)));
		
		if (board.isSecret() && !board.getUser().getId().equals(getCurrentUser().getId())) {
	        throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED_ACTION, null));
	    }
		
		List<SavedPin> savedPins = savedPinRepository.findByBoardId(boardId);
		
		return savedPins.stream()
                .map(savedPin -> mapToPinResponse(savedPin.getPin()))
                .collect(Collectors.toList());
	}
}
