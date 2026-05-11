package org.jarvis.agent.utils;

import com.google.gson.Gson;
import org.apache.fesod.sheet.FesodSheet;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExcelReaderUtils {
    /**
     * 使用 Fesod 读取 Excel 并转为 List<Map> 的 JSON 字符串供 Agent 分析
     */
    public List<Map<String, Object>> readExcelAsList(String filePath) {
        try {
            List<Map<Integer, String>> list = FesodSheet.read(filePath).sheet().headRowNumber(0).doReadSync();
            if (list == null || list.isEmpty()) {
                return new ArrayList<>();
            }
            return getMapList(list);
        } catch (Exception e) {
            throw new RuntimeException("读取 Excel 失败: " + e.getMessage());
        }
    }

    private static @NonNull List<Map<String, Object>> getMapList(List<Map<Integer, String>> list) {
        Map<Integer, String> headerMap = list.get(0);
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = 1; i < list.size(); i++) {
            Map<Integer, String> rowData = list.get(i);
            Map<String, Object> row = new LinkedHashMap<>();
            headerMap.forEach((index, name) -> {
                if (name != null && !name.trim().isEmpty()) {
                    row.put(name, rowData.get(index));
                }
            });
            resultList.add(row);
        }
        return resultList;
    }
}
