package org.jarvis.face.recognize.feign;

import com.alibaba.nacos.api.model.v2.Result;
import org.Jarvis.common.result.JarvisResult;
import org.jarvis.face.recognize.domain.dto.FaceExtractReq;
import org.jarvis.face.recognize.domain.entity.FaceVectorEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "python-face-service")
public interface FaceServiceFeignClient {
    @PostMapping(value = "/api/v1/face/extract")
    JarvisResult<FaceVectorEntity> extractFeature(@RequestBody FaceExtractReq request);
}
