package org.jarvis.bi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.Jarvis.common.utils.StringUtils;
import org.jarvis.bi.domain.JBiReport;
import org.jarvis.bi.mapper.JBiReportMapper;
import org.jarvis.bi.service.IJBiReportService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 报表管理明细表 服务实现类
 * </p>
 *
 * @author hspro
 * @since 2026-05-11
 */
@Service
public class JBiReportServiceImpl extends ServiceImpl<JBiReportMapper, JBiReport> implements IJBiReportService {

    @Override
    public List<JBiReport> queryList(JBiReport report) {
        LambdaQueryWrapper<JBiReport> qw = new LambdaQueryWrapper<>();
        if (report.getUserId() != null) {
            qw.eq(JBiReport::getUserId, report.getUserId());
        }
        if (report.getGroupId() != null) {
            qw.eq(JBiReport::getGroupId, report.getGroupId());
        }
        if (StringUtils.isNotEmpty(report.getReportType())) {
            qw.eq(JBiReport::getReportType, report.getReportType());
        }
        if (report.getStatus() != null) {
            qw.eq(JBiReport::getStatus, report.getStatus());
        }
        if (StringUtils.isNotEmpty(report.getReportName())) {
            qw.like(JBiReport::getReportName, report.getReportName());
        }
        qw.orderByAsc(JBiReport::getSortOrder);
        return this.list(qw);
    }
}
