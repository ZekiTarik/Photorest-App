package com.tarikturkdil.service;

import org.springframework.web.multipart.MultipartFile;

public interface ICloudinaryService {

	CloudinaryUploadResult uploadImage(MultipartFile file);

    void deleteImage(String publicId);
}
