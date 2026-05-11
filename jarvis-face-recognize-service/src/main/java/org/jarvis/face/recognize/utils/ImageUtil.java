package org.jarvis.face.recognize.utils;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

public class ImageUtil {
    /**
     * 将MultipartFile转换为Base64
     */
    public static String convertToBase64(MultipartFile file) throws IOException {
        // 判空校验
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传的图片不能为空");
        }
        // 获取文件的字节数组
        byte[] imageBytes = file.getBytes();

        // 将字节数组编码为 Base64 字符串
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
