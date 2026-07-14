package org.jarvis.oss.domain;

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
 * 文件存储表
 * </p>
 *
 * @author hspro
 * @since 2026-05-08
 */
@Getter
@Setter
@ToString
@TableName("j_file")
public class JFile extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 原始文件名
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件后缀
     */
    @TableField("file_suffix")
    private String fileSuffix;

    /**
     * 文件大小(Byte)
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 物理存储路径/对象存储Key
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件类型
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 文件下载完整地址
     */
    @TableField("download_url")
    private String downloadUrl;

    /**
     * 文件唯一标识(如MD5)，用于秒传
     */
    @TableField("identifier")
    private String identifier;

    /**
     * 文件随机名字，用于秒传
     */
    @TableField("file_key")
    private String fileKey;

    /**
     * 存储平台(local, qiniu, aliyun, minio)
     */
    @TableField("storage_platform")
    private String storagePlatform;

    /**
     * 上传者用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 状态(1:正常, 0:禁用)
     */
    @TableField("status")
    private Boolean status;

    /**
     * 是否删除(0:未删, 1:已删)
     */
    @TableField("is_deleted")
    private Boolean isDeleted;

}
