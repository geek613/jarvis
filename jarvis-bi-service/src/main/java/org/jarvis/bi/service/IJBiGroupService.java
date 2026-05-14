package org.jarvis.bi.service;

import org.jarvis.bi.domain.JBiGroup;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 报表分组表 (用户隔离版) 服务类
 * </p>
 *
 * @author hspro
 * @since 2026-05-11
 */
public interface IJBiGroupService extends IService<JBiGroup> {

    List<JBiGroup> queryList(JBiGroup group);
}
