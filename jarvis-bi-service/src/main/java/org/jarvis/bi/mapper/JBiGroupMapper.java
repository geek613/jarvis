package org.jarvis.bi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jarvis.bi.domain.JBiGroup;

/**
 * <p>
 * 报表分组表 (用户隔离版) Mapper 接口
 * </p>
 *
 * @author hspro
 * @since 2026-05-11
 */
@Mapper
public interface JBiGroupMapper extends BaseMapper<JBiGroup> {

}
