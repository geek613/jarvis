package org.jarvis.bi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.Jarvis.common.StringUtils;
import org.jarvis.bi.domain.JBiGroup;
import org.jarvis.bi.mapper.JBiGroupMapper;
import org.jarvis.bi.service.IJBiGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 报表分组表 (用户隔离版) 服务实现类
 * </p>
 *
 * @author hspro
 * @since 2026-05-11
 */
@Service
public class JBiGroupServiceImpl extends ServiceImpl<JBiGroupMapper, JBiGroup> implements IJBiGroupService {

    @Override
    public List<JBiGroup> queryList(JBiGroup group) {
        LambdaQueryWrapper<JBiGroup> qw = new LambdaQueryWrapper<>();
        if (group.getUserId() != null) {
            qw.eq(JBiGroup::getUserId, group.getUserId());
        }
        if (StringUtils.isNotEmpty(group.getGroupType())) {
            qw.eq(JBiGroup::getGroupType, group.getGroupType());
        }
        if (group.getParentId() != null) {
            qw.eq(JBiGroup::getParentId, group.getParentId());
        }
        if (StringUtils.isNotEmpty(group.getGroupName())) {
            qw.like(JBiGroup::getGroupName, group.getGroupName());
        }
        qw.orderByAsc(JBiGroup::getSortOrder);
        return this.list(qw);
    }
}
