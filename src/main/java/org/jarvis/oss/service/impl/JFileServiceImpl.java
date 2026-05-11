package org.jarvis.oss.service.impl;

import org.jarvis.oss.domain.JFile;
import org.jarvis.oss.mapper.JFileMapper;
import org.jarvis.oss.service.IJFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 文件存储表 服务实现类
 * </p>
 *
 * @author hspro
 * @since 2026-05-08
 */
@Service
public class JFileServiceImpl extends ServiceImpl<JFileMapper, JFile> implements IJFileService {

}
