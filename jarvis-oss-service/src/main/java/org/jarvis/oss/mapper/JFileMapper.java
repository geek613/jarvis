package org.jarvis.oss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jarvis.oss.domain.JFile;

/**
 * <p>
 * 文件存储表 Mapper 接口
 * </p>
 *
 * @author hspro
 * @since 2026-05-08
 */
@Mapper
public interface JFileMapper extends BaseMapper<JFile> {

}
