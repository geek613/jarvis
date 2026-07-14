package org.jarvis.agent.core.store;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.Jarvis.common.utils.StringUtils;
import org.jarvis.agent.chat.domain.JChatMessages;
import org.jarvis.agent.chat.mapper.JChatMessagesMapper;
import org.jarvis.agent.chat.service.JarvisGetChatTitleService;
import org.jarvis.agent.factory.AiServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class PersistentChatMemoryStore implements ChatMemoryStore {

    @Autowired
    private JChatMessagesMapper jChatMessagesMapper;
    @Autowired
    @Lazy
    private AiServiceFactory aiServiceFactory;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String[] parts = parseMemoryId(memoryId);
        Long userId = Long.valueOf(parts[0]);
        String chatId = parts[1];
        LambdaQueryWrapper<JChatMessages> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JChatMessages::getUserId, userId)
                .eq(JChatMessages::getChatId, chatId);
        JChatMessages record = jChatMessagesMapper.selectOne(queryWrapper);
        if (record != null && record.getText() != null) {
            return ChatMessageDeserializer.messagesFromJson(record.getText());
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        JarvisGetChatTitleService service = aiServiceFactory.createService(JarvisGetChatTitleService.class, false);
        String[] parts = parseMemoryId(memoryId);
        Long userId = Long.valueOf(parts[0]);
        String chatId = parts[1];
        String fullHistoryJson = ChatMessageSerializer.messagesToJson(messages);
        LambdaQueryWrapper<JChatMessages> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JChatMessages::getUserId, userId)
                .eq(JChatMessages::getChatId, chatId);
        JChatMessages existingRecord = jChatMessagesMapper.selectOne(queryWrapper);
        if (existingRecord != null) {
            // 如果数据库里已经有这个会话的记录，直接覆盖更新 text 字段
            existingRecord.setText(fullHistoryJson);
            //从messages里面获取到用户消息
            String text = "";
            JsonArray jsonArray = JsonParser.parseString(fullHistoryJson).getAsJsonArray();
            if (jsonArray.size() >= 2 && StringUtils.isEmpty(existingRecord.getChatTitle())) {
                JsonObject userObject = jsonArray.get(1).getAsJsonObject();
                text = userObject.getAsJsonArray("contents")
                        .get(0)
                        .getAsJsonObject()
                        .get("text")
                        .getAsString();
                existingRecord.setChatTitle(service.getChatTitle("问题："+text));
            }
            jChatMessagesMapper.updateById(existingRecord);
        } else {
            JChatMessages newRecord = new JChatMessages();
            newRecord.setUserId(userId);
            newRecord.setChatId(chatId);
            newRecord.setType("HISTORY");
            newRecord.setText(fullHistoryJson);
            jChatMessagesMapper.insert(newRecord);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMessages(Object memoryId) {
        String[] parts = parseMemoryId(memoryId);
        Long userId = Long.valueOf(parts[0]);
        String chatId = parts[1];
        LambdaQueryWrapper<JChatMessages> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(JChatMessages::getUserId, userId)
                .eq(JChatMessages::getChatId, chatId);
        jChatMessagesMapper.delete(deleteWrapper);
    }

    private String[] parseMemoryId(Object memoryId) {
        if (memoryId instanceof String) {
            return ((String) memoryId).split(":");
        }
        throw new IllegalArgumentException("无效的 memoryId 格式，应为 'userId:chatId'");
    }
}