package org.jarvis.oss.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.util.Auth;
import org.Jarvis.common.result.JarvisResult;
import org.jarvis.oss.domain.JFile;
import org.jarvis.oss.service.IJFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/oss")
public class OssController {

    @Value("${qiniu.access-key}")
    private String accessKey;

    @Value("${qiniu.secret-key}")
    private String secretKey;

    @Value("${qiniu.bucket}")
    private String bucket;

    @Value("${qiniu.domain}")
    private String domain;



    @Autowired
    private IJFileService ijFileService;

    /**
     * 前端调用这个接口来获取上传凭证
     */
    @GetMapping("/token")
    public JarvisResult<Map<String, Object>> getUploadToken() {
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        Map<String, Object> map = new HashMap<>();
        map.put("token", upToken);
        map.put("domain", domain);
        return new JarvisResult<>(map);
    }

    @GetMapping("/overwriteToken")
    public JarvisResult<Map<String, Object>> getOverwriteToken(@RequestParam("oldFileKey") String oldFileKey, @RequestParam("newFileKey") String newFileKey) {
        Configuration cfg = new Configuration(Region.qvmHuabei());
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, oldFileKey);
        } catch (QiniuException ex) {
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
        // 覆盖上传token
        String upToken = auth.uploadToken(bucket, newFileKey);
        Map<String, Object> map = new HashMap<>();
        map.put("token", upToken);
        map.put("domain", domain);
        return new JarvisResult<>(map);
    }

    @GetMapping("/checkFileExists")
    public JarvisResult<Boolean> checkFileExists(@RequestParam("fileName") String fileName) {
        JFile jFile = ijFileService.getOne(new QueryWrapper<JFile>().eq("file_name", fileName).last("LIMIT 1"));
        return new JarvisResult<>(jFile != null);
    }

    @DeleteMapping("/remove/{id}")
    public JarvisResult<String> deleteFile(@PathVariable("id") Long id) {
        Configuration cfg = new Configuration(Region.qvmHuabei());
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        JFile jFile = ijFileService.getById(id);
        try {
            // 4. 执行删除操作
            bucketManager.delete(bucket, jFile.getFileKey());
            ijFileService.removeById(id);
            return JarvisResult.success("删除成功");
        } catch (QiniuException ex) {
            // 如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
            return JarvisResult.error("删除失败");
        }
    }

    @GetMapping("/getQiniuDownloadUrl")
    public JarvisResult<String> getQiniuDownloadUrl(@RequestParam("fileKey") String fileKey) {
        Auth auth = Auth.create(accessKey, secretKey);
        // 生成下载链接，有效期 3600 秒
        return JarvisResult.success(auth.privateDownloadUrl(domain + "/" + fileKey, 3600));
    }
}