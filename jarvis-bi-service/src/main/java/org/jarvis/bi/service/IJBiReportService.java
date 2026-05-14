package org.jarvis.bi.service;

import org.jarvis.bi.domain.JBiReport;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 报表管理明细表 服务类
 * </p>
 *
 * @author hspro
 * @since 2026-05-11
 */
public interface IJBiReportService extends IService<JBiReport> {

    List<JBiReport> queryList(JBiReport report);
}
