package com.tarikturkdil.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.tarikturkdil.exception.BaseException;
import com.tarikturkdil.exception.ErrorMessage;
import com.tarikturkdil.exception.MessageType;
import com.tarikturkdil.service.CloudinaryUploadResult;
import com.tarikturkdil.service.ICloudinaryService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CloudinaryServiceImpl implements ICloudinaryService{

	@Autowired
	private Cloudinary cloudinary;
	
	@Value("${cloudinary.folder}")
    private String folder;
	
	@Override
	public CloudinaryUploadResult uploadImage(MultipartFile file) {
		try {
			Map<String, Object> options = new HashMap<>();
			options.put("folder", folder);
			
			@SuppressWarnings("unchecked")
			Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
			
			String url = (String) uploadResult.get("secure_url");
			String publicId = (String) uploadResult.get("public_id");
			
			log.info("Fotoğraf Cloudinary'ye yüklendi: {}", publicId);

            return new CloudinaryUploadResult(url, publicId);
 			
		} catch (IOException e) {
            log.error("Cloudinary'ye yükleme sırasında hata oluştu", e);
            throw new BaseException(new ErrorMessage(MessageType.PHOTO_NOT_UPLOAD_TO_CLOUD, null));
		}
	}

	@Override
	public void deleteImage(String publicId) {
		try {
            Map<String, Object> options = new HashMap<>();

            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, options);

            log.info("Cloudinary'den fotoğraf silindi: {} - sonuç: {}", publicId, result.get("result"));

        } catch (IOException e) {
            // Silme başarısız olsa bile, uygulamanın akışını durdurmuyoruz —
            // en kötü ihtimalle Cloudinary'de yetim bir dosya kalır, ama
            // veritabanı işlemi (pin silme) buna takılmamalı.
            log.error("Cloudinary'den silme sırasında hata oluştu: {}", publicId, e);
        }
		
	}

}
