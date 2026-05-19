# Jarvis 电商智能交互助手

## 项目概述

Jarvis 是一个基于 **Spring Cloud 微服务架构** 的电商智能交互平台，核心能力包括 **AI 对话助手**、**多 Agent 图表自动生成**、**BI 报表管理**、**人脸识别认证** 等。项目采用前后端分离架构，以 Spring Cloud Gateway 作为统一入口，Nacos 作为注册中心和配置中心。

- **项目名称**: org.jarvis:jarvis
- **版本**: 1.0-SNAPSHOT
- **Java 版本**: 17
- **构建工具**: Maven (多模块)
- **模块数量**: 13 个

---

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 基础框架 | Spring Boot | 3.5.10 |
| 微服务 | Spring Cloud | 2025.0.0 |
| 微服务生态 | Spring Cloud Alibaba | 2025.0.0.0 |
| 网关 | Spring Cloud Gateway (WebFlux) | - |
| 服务注册/配置 | Nacos | 2.x |
| ORM | MyBatis-Plus | 3.5.15 |
| 数据库 | MySQL | 8.x (mysql-connector 8.3.0) |
| 连接池 | Druid | 1.2.21 |
| 缓存 | Redis (Lettuce 客户端) | commons-pool2 2.11.1 |
| 认证 | JWT (jjwt) | 0.11.5 |
| AI 框架 | LangChain4j | 1.12.1-beta21 |
| LLM | DeepSeek (OpenAI 兼容 API) | deepseek-v4-flash |
| 对象存储 | 七牛云 (Qiniu) | 7.18.0 |
| Excel 解析 | Apache Fesod (fesod-sheet) | 2.0.1-incubating |
| JSON | Gson | 2.10.1 |
| 向量数据库 | Milvus (人脸特征) | - |
| 外部 AI 服务 | Python 人脸识别服务 | - |

---

## 模块架构

```
jarvis (父 POM)
├── jarvis-common                  # 公共模块：基础实体、异常、统一返回、工具类
├── jarvis-common-core             # 公共核心：Redis 配置
├── jarvis-common-security         # 公共安全：JWT 解析、Security 过滤器
├── jarvis-commom-api              # 公共 API DTO（Feign 共享）
├── jarvis-gateway-service         # 网关服务 (:6130)
├── jarvis-auth-service            # 认证服务 (:16130)
├── jarvis-agent-service           # AI Agent 服务 (:6132)
├── jarvis-bi-service              # BI 报表服务 (:6134)
├── jarvis-settings-service        # 系统设置服务 (:6135)
├── jarvis-face-recognize-service  # 人脸识别服务 (:6131)
├── jarvis-oss-service             # 对象存储服务 (:6133)
├── jarvis-system-health           # 系统健康检查 (:8081)
└── jarvice-code-generate          # MyBatis-Plus 代码生成器
```

---

## 网关路由表

网关端口 **6130**，所有请求经 Gateway 路由到对应微服务：

| 路由 ID | 请求路径 | 目标服务 | 说明 |
|---------|----------|----------|------|
| system-health-route | `/api/health/**` | jarvis-system-health | 健康检查 |
| face-recognize-route | `/api/face/**` | jarvis-face-recognition-service | 人脸识别 |
| auth-route | `/api/system/**`, `/api/jSysUser/**` | jarvis-auth-service | 登录/注册/用户管理 |
| talk-chat-route | `/api/jarvis/**`, `/api/jChatMessages/**` | jarvis-agent-service | AI 对话/图表生成(超时300s) |
| oss-service-route | `/api/oss/**` | jarvis-oss-service | 文件上传/下载 |
| bi-service-route | `/api/jBiGroup/**`, `/api/jBiReport/**` | jarvis-bi-service | BI 报表管理 |
| settings-service-route | `/api/jLlmConfig/**` | jarvis-settings-service | LLM 配置管理 |

---

## 核心功能详解

### 1. 认证与授权 (jarvis-auth-service)

- **账号密码登录**: `POST /api/system/login` → 验证用户名密码 → 返回 JWT Token（2小时过期）
- **用户注册**: `POST /api/system/register` → 创建用户，关联人脸 ID（可选）
- **用户管理**: CRUD 接口，支持按用户名/邮箱/手机号筛选
- **安全机制**:
  - Gateway 层 JWT 校验（OAuth2 Resource Server 模式）
  - `JwtTokenRelayGlobalFilter` 将 Token 透传给下游服务
  - `jarvis-common-security` 模块提供内部服务间的 JWT 解析过滤器
  - 白名单路径: `/api/system/login`, `/api/system/register` 等

### 2. AI 智能对话 (jarvis-agent-service)

基于 **LangChain4j** 框架，集成 **DeepSeek V4 Flash** 大模型（通过 OpenAI 兼容 API）。

#### 对话接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/jarvis/chat` | GET | 普通对话（非流式） |
| `/api/jarvis/streamChat` | GET | 流式对话（SSE，`text/event-stream`） |
| `/api/jarvis/generateChart` | GET | 多 Agent 图表生成 |

#### 对话记忆

- 基于 `PersistentChatMemoryStore` 将对话历史持久化到 MySQL（`j_chat_messages` 表）
- 记忆窗口: 最近 10 条消息（`MessageWindowChatMemory`）
- 自动生成对话标题（15-20 字）
- 记忆 ID 格式: `userId:chatId`

#### 动态模型配置

- 支持**按用户维度**配置不同的 LLM 提供商（OpenAI / DeepSeek / Ollama）
- 配置存储: MySQL (`j_llm_config` 表) + Redis 缓存（1天 TTL）
- 通过 Feign 调用 `jarvis-settings-service` 获取用户配置
- `AiModelFactory` 为每个用户创建独立的 `OpenAiChatModel` / `OpenAiStreamingChatModel` 实例

#### 系统角色

Jarvis 助手的 System Prompt 定位为：**电商智能交互助手，专注于电商数据分析、报表创建、数据看板**。

### 3. 多 Agent 图表生成（核心亮点）

这是一个 **4 Agent 协作流水线**，将自然语言需求自动转化为 ECharts 可视化图表：

```
用户需求 + Excel 数据
        │
        ▼
┌─────────────────┐
│  LeaderAgent    │  总指挥：协调整个流程
│  (指挥官)        │
└───────┬─────────┘
        │
   ┌────┼────┐
   │    │    │
   ▼    ▼    ▼
┌──────┐ ┌──────┐ ┌──────┐
│Data  │ │Data  │ │Chart │
│Pro.. │ │Check │ │Config│
│Agent │ │Agent │ │Agent │
│(数据 │ │(数据 │ │(图表 │
│处理) │ │校验) │ │配置) │
└──────┘ └──────┘ └──────┘

输出: LeaderResult { chartName, chartType, chartOption (ECharts JSON) }
```

#### Agent 职责

| Agent | 角色 | 输入 | 输出 |
|-------|------|------|------|
| **LeaderAgent** | 总指挥 | 用户需求 + 文件路径 + 图表类型 | LeaderResult (含 ECharts JSON) |
| **DataProcessAgent** | 数据分析专家 | Excel 样本数据 + 需求 + 图表类型 | DataProcessRule (聚合规则 JSON) |
| **DataCheckAgent** | 数据质量工程师 | 聚合后的数据 + 图表类型 | 校验结果 (通过/不通过) |
| **ChartConfigAgent** | ECharts 专家 | ECharts 模板 + 实际数据 + 需求 | 最终 ECharts option JSON |

#### 数据处理引擎 (DataProcessRuleEngine)

- **过滤操作**: `EQUALS`, `CONTAINS`, `GREATER_THAN`, `LESS_THAN`, `BETWEEN`（支持日期范围）
- **聚合模式**:
  - `AGGREGATE`: 适用于柱状图/折线图/饼图，按维度分组，对指标进行 SUM/COUNT
  - `EXTRACT`: 适用于散点图/明细表，直接提取指定字段

#### 支持的图表类型

| 类型 | 模板文件 | 说明 |
|------|----------|------|
| bar | bar.json | 柱状图，暗色主题 `#00d4ff` |
| line | line.json | 折线图，平滑曲线 + 面积填充 |
| pie | pie.json | 饼图，环形样式（半径 0%-70%） |
| scatter | scatter.json | 散点图，数值轴 |

#### Excel 读取

- 支持**本地文件路径**和 **HTTP/HTTPS 云端 URL**（流式下载，5s 连接超时，60s 读取超时）
- 基于 Apache Fesod 库解析，输出 `List<Map<String, Object>>`

### 4. BI 报表管理 (jarvis-bi-service)

#### 报表分组 (j_bi_group)

- 树形结构，支持 `parentId` 无限嵌套
- 按 `userId` 数据隔离（userId=0 为系统公共分组）
- 支持 `groupType` 分类（业务分类/看板分类）
- 按 `sortOrder` 排序

#### 报表定义 (j_bi_report)

- 关联分组，支持 5 种图表类型: `bar`, `line`, `pie`, `scatter`, `table`
- 存储 SQL 查询语句 (`querySql`)
- ECharts 图表配置以 JSON 格式存储在 `chartConfig` 字段（JacksonTypeHandler）
- 状态管理: 1=启用, 0=草稿
- 逻辑删除 (`isDeleted`)

### 5. 人脸识别认证 (jarvis-face-recognize-service)

作为**桥接服务**，连接 Java 后端与 Python 人脸识别微服务：

```
客户端上传图片 → FaceRecognizeController
    → ImageUtil 转 Base64
    → Feign 调用 python-face-service (/api/v1/face/extract)
    → 返回 FaceVectorEntity { vector: [], dimension }
    → 向量存入 Milvus，关联 JSysUser.faceId
```

### 6. 对象存储 (jarvis-oss-service)

对接**七牛云**对象存储：

- 客户端直传 Token 发放（`GET /oss/token`）
- 覆盖上传 Token（先删旧文件再传新文件）
- 文件元数据管理（`j_file` 表）
- MD5 去重（`identifier` 字段）
- 私有下载链接生成（3600s 有效期）
- 存储平台可扩展: local / qiniu / aliyun / minio

### 7. 系统设置 (jarvis-settings-service)

管理用户级别的 LLM 配置：

| 字段 | 说明 |
|------|------|
| userId | 用户 ID |
| provider | 提供商 (openai/deepseek/ollama) |
| apiKey | API 密钥 |
| baseUrl | API 地址 (如 `https://api.deepseek.com/v1`) |
| modelName | 模型名称 (如 `deepseek-chat`) |
| temperature | 温度参数 (0.0-2.0) |
| isActive | 是否激活 |

### 8. 系统健康检查 (jarvis-system-health)

- `/api/health` → 返回可配置的测试字符串
- `/api/health/getUUid` → 返回随机 UUID
- `/api/health/getServerTime` → 返回服务器时间

---

## 数据库表结构

| 表名 | 所属模块 | 说明 |
|------|----------|------|
| j_sys_user | auth-service | 系统用户（userId, username, password, email, phone, status, faceId） |
| j_chat_messages | agent-service | 对话消息（userId, chatId, chatTitle, type, text[JSON]） |
| j_llm_config | settings-service | LLM 配置（userId, provider, apiKey, baseUrl, modelName, temperature, isActive） |
| j_bi_group | bi-service | BI 报表分组（userId, groupName, groupType, parentId, sortOrder, isDeleted） |
| j_bi_report | bi-service | BI 报表定义（reportName, groupId, userId, reportType, dataSourceId, querySql, chartConfig[JSON], status, isDeleted） |
| j_file | oss-service | 文件管理（fileName, fileSize, filePath, downloadUrl, identifier[MD5], storagePlatform, userId） |

所有表均包含审计字段: `create_by`, `create_time`, `update_by`, `update_time` (继承自 `BaseEntity`)。

---

## 项目设计规范

### 命名约定

- 所有实体类以 `J` 前缀命名: `JSysUser`, `JBiReport`, `JFile`, `JChatMessages`, `JLlmConfig`
- 所有控制器提供标准 CRUD: `list`, `getById`, `add`(POST), `edit`(PUT), `removeById`(DELETE)

### 统一返回格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

使用 `JarvisResult<T>` 封装，状态码枚举: `StatusCode` (200/401/403/404/500 等)。

### 服务间通信

- Feign 声明式调用 + Nacos 负载均衡
- 共享 DTO 放在 `jarvis-commom-api` 模块

### 安全架构

- **外部请求**: Gateway JWT 校验 → Token 透传 → 下游服务
- **内部请求**: `InternalTokenFilter` 解析 JWT → 设置 SecurityContext
- 白名单可配置 (`security.ignore.urls`)

---

## 基础架构依赖

```
Client
  │
  ▼
Spring Cloud Gateway (:6130) ─── Nacos (:8848) 注册中心/配置中心
  │                                  │
  ├── jarvis-system-health            │
  ├── jarvis-face-recognize  ─── Python 人脸服务 + Milvus
  ├── jarvis-auth-service     ─── MySQL (j_sys_user)
  ├── jarvis-agent-service    ─── DeepSeek API + MySQL (j_chat_messages)
  │                                + Redis (LLM 配置缓存)
  ├── jarvis-oss-service      ─── 七牛云 + MySQL (j_file)
  ├── jarvis-bi-service       ─── MySQL (j_bi_group, j_bi_report)
  └── jarvis-settings-service ─── MySQL (j_llm_config) + Redis
```

---

## 当前开发状态

- **最新提交**: `f5ecea9` (2026-05-14) - 新增对话管理与记忆
- **初始提交**: `b71a343` (2026-05-11) - 项目初稿
- **分支**: master
- **环境**: dev (namespace: `devhspro`)
- **Nacos**: 127.0.0.1:8848
