package org.jarvis.face.recognize.controller;

import org.Jarvis.common.result.JarvisResult;
import org.jarvis.face.recognize.domain.dto.FaceExtractReq;
import org.jarvis.face.recognize.domain.entity.FaceVectorEntity;
import org.jarvis.face.recognize.feign.FaceServiceFeignClient;
import org.jarvis.face.recognize.utils.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/face")
public class FaceRecognizeController {
    @Autowired
    private FaceServiceFeignClient faceServiceFeignClient;
    @PostMapping("/recognize")
    public JarvisResult<FaceVectorEntity> extractFeature(@RequestParam("file")MultipartFile file) {
        String base64 = null;
        try {
            base64 = ImageUtil.convertToBase64(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FaceExtractReq faceExtractReq = new FaceExtractReq();
        faceExtractReq.setImage64(base64);
        return faceServiceFeignClient.extractFeature(faceExtractReq);
    }
}
