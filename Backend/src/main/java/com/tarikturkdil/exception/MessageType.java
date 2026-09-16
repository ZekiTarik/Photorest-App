package com.tarikturkdil.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum MessageType {
	
	NO_RECORD_EXIST("1004", "Kayıt Bulunamadı", HttpStatus.NOT_FOUND),
    TOKEN_IS_EXPIRED("1005", "Token Süresi Bitmiştir", HttpStatus.UNAUTHORIZED),
    USERNAME_NOT_FOUND("1006", "Kullanıcı Adı Bulunamadı", HttpStatus.NOT_FOUND),
    USERNAME_OR_PASSWORD_INVALID("1007", "Kullanıcı Adı veya Şifre Hatalı", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_NOT_FOUND("1008", "Refresh Token Bulunamadı", HttpStatus.NOT_FOUND),
    REFRESH_TOKEN_IS_EXPIRED("1009", "Refresh Token Süresi Bitmiştir.", HttpStatus.UNAUTHORIZED),
    NOT_CONNECT_AI_SERVER("1010", "Yapay Zeka Sunucusuna Bağlanılamadı", HttpStatus.SERVICE_UNAVAILABLE),
    PIN_NOT_FOUND("1011", "Pin Bulunamadı!", HttpStatus.NOT_FOUND),
    PHOTO_NOT_UPLOAD_TO_CLOUD("1012", "Fotoğraf buluta yüklenirken bir hata oluştu", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_ALREADY_EXISTS("1013", "Bu e-posta adresi zaten kayıtlı", HttpStatus.CONFLICT),
    GENERAL_EXCEPTION("9999", "Genel Bir Hata Oluştu", HttpStatus.INTERNAL_SERVER_ERROR),
	BOARD_NOT_FOUND("1015", "Pano bulunamadı", HttpStatus.NOT_FOUND),
	UNAUTHORIZED_ACTION("1016", "Bu işlem için yetkiniz yok", HttpStatus.FORBIDDEN),
	PIN_ALREADY_SAVED("1018", "Bu pin zaten bu panoya kaydedilmiş", HttpStatus.CONFLICT),
	ALREADY_LIKED("1019", "Bu pini zaten beğendiniz", HttpStatus.CONFLICT),
	LIKE_NOT_FOUND("1020", "Beğeni bulunamadı", HttpStatus.NOT_FOUND),
	COMMENT_NOT_FOUND("1021", "Yorum bulunamadı", HttpStatus.NOT_FOUND),
	CANNOT_FOLLOW_YOURSELF("1022", "Kendinizi takip edemezsiniz", HttpStatus.BAD_REQUEST),
	ALREADY_FOLLOWING("1023", "Bu kullanıcıyı zaten takip ediyorsunuz", HttpStatus.CONFLICT),
	FOLLOW_NOT_FOUND("1024", "Takip kaydı bulunamadı", HttpStatus.NOT_FOUND),
	CONVERSATION_NOT_FOUND("1025", "Konuşma bulunamadı", HttpStatus.NOT_FOUND),
	CANNOT_MESSAGE_YOURSELF("1026", "Kendinize mesaj gönderemezsiniz", HttpStatus.BAD_REQUEST),
	NOTIFICATION_NOT_FOUND("1027", "Bildirim bulunamadı", HttpStatus.NOT_FOUND),
	TOO_MANY_REQUESTS("1028", "Çok fazla deneme yaptınız. Lütfen bir süre sonra tekrar deneyin.", HttpStatus.TOO_MANY_REQUESTS);
	
	private final String code;
	private final String message;
	private final HttpStatus status; 
	
	private MessageType(String code, String message,HttpStatus status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}
}
