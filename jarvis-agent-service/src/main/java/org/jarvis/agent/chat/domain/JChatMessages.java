package org.jarvis.agent.chat.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.Jarvis.common.domain.BaseEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author hspro
 * @since 2026-05-13
 */
@Getter
@Setter
@ToString
@TableName("j_chat_messages")
public class JChatMessages extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private Long userId;

    @TableField("chat_id")
    private String chatId;

    @TableField("chat_title")
    private String chatTitle;

    @TableField("type")
    private String type;

    @TableField("text")
    private String text;
}
