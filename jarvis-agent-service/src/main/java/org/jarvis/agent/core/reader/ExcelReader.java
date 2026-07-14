package org.jarvis.agent.core.reader;

import org.apache.fesod.sheet.FesodSheet;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExcelReader {

    /**
     * 统一读取入口
     */
    public List<Map<String, Object>> readExcelAsList(String fileLocation) {
        if (fileLocation == null || fileLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("文件路径或URL不能为空");
        }

        fileLocation = fileLocation.trim();

        // 自动路由：判断是否为网络资源
        if (fileLocation.startsWith("http://") || fileLocation.startsWith("https://")) {
            return readFromCloudUrl(fileLocation);
        } else {
            return readFromLocalFile(fileLocation);
        }
    }

    /**
     * 策略 A：读取本地文件
     */
    private List<Map<String, Object>> readFromLocalFile(String filePath) {
        try {
            List<Map<Integer, String>> list = FesodSheet.read(filePath)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();

            if (list == null || list.isEmpty()) {
                return new ArrayList<>();
            }
            return getMapList(list);
        } catch (Exception e) {
            throw new RuntimeException("读取本地 Excel 失败: " + e.getMessage());
        }
    }

    /**
     * 策略 B：流式读取云端文件
     */
    private List<Map<String, Object>> readFromCloudUrl(String fileUrl) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(fileUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // 5秒连接超时
            connection.setReadTimeout(60000);   // 60秒读取超时

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("云端文件获取失败，HTTP状态码：" + connection.getResponseCode());
            }

            // 直接将网络流 InputStream 交给 Fesod 解析
            try (InputStream inputStream = connection.getInputStream()) {
                // 读取出的结构：List[map(1, v1->xx,v2->yy,v3->zz....)]
                List<Map<Integer, String>> list = FesodSheet.read(inputStream)
                        .sheet()
                        .headRowNumber(0)
                        .doReadSync();

                if (list == null || list.isEmpty()) {
                    return new ArrayList<>();
                }
                return getMapList(list); // 复用你的格式化逻辑
            }
        } catch (Exception e) {
            throw new RuntimeException("流式读取云端 Excel 失败: " + e.getMessage());
        } finally {
            // 确保网络连接被正确释放
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 数据格式化与表头映射 (完全保留你写的方法)
     */
    private static @NonNull List<Map<String, Object>> getMapList(List<Map<Integer, String>> list) {
        // 表头
        Map<Integer, String> headerMap = list.get(0);
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = 1; i < list.size(); i++) {
            // 每一行的数据，取出来的都是map
            Map<Integer, String> rowData = list.get(i);
            //
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