# MQ - 轻量级消息队列

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/java-8-orange.svg)](https://www.oracle.com/java/)
[![Status](https://img.shields.io/badge/status-developing-yellow.svg)](https://github.com/bigdatahang/mq)

一个基于 Java 实现的轻量级、高性能消息队列系统，采用内存映射文件（MMap）技术实现高效的消息持久化。

## 🎯 项目简介

本项目是一个从零开始实现的消息队列系统，参考了 RocketMQ 的设计思想，旨在深入理解消息队列的核心原理和实现细节。

### 设计目标
- ⚡ **高性能**：基于 MMap 技术实现零拷贝，提升 IO 性能
- 🔒 **线程安全**：支持自旋锁和可重入锁，保证并发安全
- 💾 **数据可靠**：CommitLog 顺序写入，支持同步/异步刷盘
- 🎨 **架构清晰**：模块化设计，代码结构清晰易懂

## ✨ 已实现功能

### 核心功能
- [x] **CommitLog 消息存储**
  - 基于 MMap 的高性能文件映射
  - 消息顺序写入和追加
  - 自动文件切换（当文件达到上限时）
  - 消息读取和定位
  
- [x] **ConsumeQueue 消息索引**
  - 基于 MMap 的索引文件映射
  - 12字节定长索引记录（CommitLog文件名+偏移量+消息长度）
  - 支持多队列并发写入
  - 自动索引文件切换
  
- [x] **消息消费**
  - 支持多消费组独立消费
  - 消费进度持久化（consumequeue-offset.json）
  - ACK 机制确认消费
  - 并发消费支持（多线程消费者）
  
- [x] **内存管理**
  - MappedByteBuffer 自动释放（解决 JDK8 文件删除 bug）
  - 参考 RocketMQ 的 Cleaner 实现
  - 读写 Buffer 分离设计
  
- [x] **并发控制**
  - 自旋锁（SpinLock）实现
  - 非公平可重入锁（UnfairReentrantLock）
  - 写入消息锁（PutMessageLock）接口
  - Offset 原子操作（AtomicInteger/AtomicLong）

- [x] **配置管理**
  - Topic 配置加载（JSON 格式）
  - 全局配置管理
  - CommitLog 元数据管理
  - ConsumeQueue 偏移量管理
  - 定时刷新配置到磁盘

- [x] **工具类**
  - 字节转换工具（ByteConvertUtil）- int/byte[] 互转
  - 文件名生成工具（LogFileNameUtil）- CommitLog/ConsumeQueue 文件名管理
  - 文件内容读写工具（FileContentUtil）
  - 内存映射工具（MMapUtil）- MappedByteBuffer 管理

## 🏗️ 技术架构

### 技术栈
- **语言**：Java 8
- **构建工具**：Maven
- **JSON 库**：Fastjson2
- **网络框架**：Netty（已引入依赖）
- **核心技术**：
  - NIO MappedByteBuffer（内存映射文件）
  - 原子操作（AtomicLong、AtomicInteger）
  - 反射（Reflection）
  - 并发编程（Locks、Thread Pool）

### 项目结构
```
mq/
├── mqbroker/                    # Broker 模块
│   ├── src/main/java/
│   │   └── com/k/mq/broker/
│   │       ├── BrokerStartUp.java           # Broker 启动类
│   │       ├── cache/                       # 缓存层
│   │       │   └── CommonCache.java
│   │       ├── config/                      # 配置管理
│   │       │   ├── CommonThreadPoolConfig.java
│   │       │   ├── ConsumeQueueOffsetLoader.java
│   │       │   ├── GlobalProperties.java
│   │       │   ├── GlobalPropertiesLoader.java
│   │       │   └── MQTopicLoader.java
│   │       ├── constants/                   # 常量定义
│   │       │   └── BrokerConstants.java
│   │       ├── core/                        # 核心功能
│   │       │   ├── CommitLogAppenderHandler.java
│   │       │   ├── CommitLogMMapFileModel.java
│   │       │   ├── CommitLogMMapFileModelManager.java
│   │       │   ├── ConsumeQueueAppenderHandler.java
│   │       │   ├── ConsumeQueueConsumeHandler.java
│   │       │   ├── ConsumeQueueMMapFileModel.java
│   │       │   └── ConsumeQueueMMapFileModelManager.java
│   │       ├── model/                       # 数据模型
│   │       │   ├── CommitLogMessageModel.java
│   │       │   ├── CommitLogModel.java
│   │       │   ├── ConsumeQueueDetailModel.java
│   │       │   ├── ConsumeQueueOffsetModel.java
│   │       │   ├── MQTopicModel.java
│   │       │   └── QueueModel.java
│   │       └── util/                        # 工具类
│   │           ├── ByteConvertUtil.java
│   │           ├── FileContentUtil.java
│   │           ├── LogFileNameUtil.java
│   │           ├── MMapUtil.java
│   │           ├── PutMessageLock.java
│   │           ├── SpinLock.java
│   │           └── UnfairReentrantLock.java
│   └── pom.xml
├── broker/                                  # Broker 数据目录
│   ├── config/                              # 配置文件
│   │   ├── mq-topic.json                   # Topic 配置
│   │   └── consumequeue-offset.json        # 消费进度
│   └── store/                               # 消息存储
│       └── {topic}/                         # 按 Topic 分目录
│           ├── commitlog/                   # CommitLog 文件
│           └── consumequeue/                # ConsumeQueue 索引文件
│               └── {queueId}/               # 按队列ID分目录
└── pom.xml
```

## 🚀 快速开始

### 环境要求
- JDK 8+
- Maven 3.6+

### 构建项目
```bash
# 克隆项目
git clone https://github.com/bigdatahang/mq.git
cd mq

# 编译项目
mvn clean compile

# 运行 Broker
mvn exec:java -pl mqbroker -Dexec.mainClass="com.k.mq.broker.BrokerStartUp"
```

### 配置说明

**Topic 配置文件**：`broker/config/mq-topic.json`
```json
[
  {
    "topic": "order_cancel_topic",
    "latestCommitLog": {
      "fileName": "00000000",
      "offset": 0,
      "offsetLimit": 1048576
    },
    "queueList": [
      {
        "id": 0,
        "fileName": "00000000",
        "lastOffset": 0,
        "latestOffset": 0,
        "offsetLimit": 6291456
      }
    ]
  }
]
```

**消费进度配置文件**：`broker/config/consumequeue-offset.json`
```json
{
  "offsetTable": {
    "topicConsumeGroupDetail": {
      "order_cancel_topic": {
        "consumeGroupDetail": {
          "user_service_group": {
            "0": "00000000#0"
          }
        }
      }
    }
  }
}
```

## 📖 核心设计

### 1. CommitLog 存储模型
```
CommitLog 文件格式：
┌─────────────┬─────────────┬─────────────┐
│  Message 1  │  Message 2  │  Message 3  │
└─────────────┴─────────────┴─────────────┘

每条消息格式：
┌──────────┬────────────────┐
│ Size (4) │ Content (N)    │
└──────────┴────────────────┘
```

### 2. ConsumeQueue 索引模型
```
ConsumeQueue 文件格式（每条记录12字节）：
┌────────┬────────┬────────┬────────┬────────┐
│ Index1 │ Index2 │ Index3 │ Index4 │  ...   │
└────────┴────────┴────────┴────────┴────────┘

每条索引格式（12字节）：
┌─────────────────┬──────────────┬─────────────┐
│FileName (4)     │ MsgIndex (4) │ MsgLen (4)  │
└─────────────────┴──────────────┴─────────────┘
```

### 3. 消息生产和消费流程
```
【生产流程】
1. Producer 发送消息到 Broker
2. 写入 CommitLog（顺序写）
3. 分发到 ConsumeQueue（异步或同步）
4. 更新 Topic 配置（offset）
5. 定时刷新到磁盘

【消费流程】
1. Consumer 从 ConsumeQueue 读取索引
2. 根据索引从 CommitLog 读取消息内容
3. 返回消息给 Consumer
4. Consumer ACK 确认消费
5. 更新消费进度到 consumequeue-offset.json
```

### 4. 文件自动切换
- 当 `offset` 达到 `offsetLimit` 时自动创建新文件
- 文件名递增：`00000000` → `00000001` → `00000002`
- CommitLog 默认：1MB，ConsumeQueue 默认：6MB

### 5. 并发安全
- 写入操作使用锁保护（SpinLock 或 ReentrantLock）
- Offset 使用 AtomicInteger/AtomicLong 保证原子性
- MappedByteBuffer position 精确控制
- 读写 Buffer 分离，避免并发冲突

## 🔧 开发中的功能

> ⚠️ **注意**：以下功能正在开发中或计划开发

- [ ] **网络通信**
  - Netty 网络层
  - Producer 客户端
  - Consumer 客户端
  - 通信协议设计（基于 Netty）

- [ ] **高级特性**
  - 消息过滤
  - 延迟消息
  - 事务消息
  - 顺序消息
  - 消息重试

- [ ] **性能优化**
  - 批量写入
  - 异步刷盘优化
  - 零拷贝传输
  - 读写分离

- [ ] **运维功能**
  - 消息查询
  - 性能监控
  - 管理控制台
  - 集群支持
  - 主从复制

## 📝 代码规范

- 所有类和方法都有完整的 JavaDoc 注释
- 遵循阿里巴巴 Java 开发规范
- 代码格式统一，命名清晰
- 异常处理规范，日志记录完整

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 License

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 👨‍💻 作者

**yihang07** - [GitHub](https://github.com/bigdatahang)

## 🙏 致谢

- 参考了 [Apache RocketMQ](https://rocketmq.apache.org/) 的设计思想
- 感谢所有为本项目做出贡献的开发者

## 📚 学习资源

- [RocketMQ 官方文档](https://rocketmq.apache.org/docs/quick-start/)
- [Java NIO MappedByteBuffer](https://docs.oracle.com/javase/8/docs/api/java/nio/MappedByteBuffer.html)
- [消息队列设计精要](https://github.com/doocs/advanced-java/blob/main/docs/high-concurrency/mq-design.md)

---

⭐ 如果这个项目对你有帮助，请给个 Star 支持一下！

**项目状态**：🚧 持续开发中...

**最后更新**：2026-02-23

## 🏗️ 技术架构

### 技术栈
- **语言**：Java 8
- **构建工具**：Maven
- **核心技术**：
  - NIO MappedByteBuffer（内存映射文件）
  - 原子操作（AtomicLong、AtomicInteger）
  - 反射（Reflection）
  - 并发编程（Locks、Thread Pool）

### 项目结构
```
mq/
├── mqbroker/                    # Broker 模块
│   ├── src/main/java/
│   │   └── com/k/mq/broker/
│   │       ├── BrokerStartUp.java           # Broker 启动类
│   │       ├── cache/                       # 缓存层
│   │       │   └── CommonCache.java
│   │       ├── config/                      # 配置管理
│   │       │   ├── CommonThreadPoolConfig.java
│   │       │   ├── ConsumeQueueOffsetLoader.java
│   │       │   ├── GlobalProperties.java
│   │       │   ├── GlobalPropertiesLoader.java
│   │       │   └── MQTopicLoader.java
│   │       ├── constants/                   # 常量定义
│   │       │   └── BrokerConstants.java
│   │       ├── core/                        # 核心功能
│   │       │   ├── CommitLogAppenderHandler.java
│   │       │   ├── CommitLogMMapFileModel.java
│   │       │   ├── CommitLogMMapFileModelManager.java
│   │       │   ├── ConsumeQueueAppenderHandler.java
│   │       │   ├── ConsumeQueueConsumeHandler.java
│   │       │   ├── ConsumeQueueMMapFileModel.java
│   │       │   └── ConsumeQueueMMapFileModelManager.java
│   │       ├── model/                       # 数据模型
│   │       │   ├── CommitLogMessageModel.java
│   │       │   ├── CommitLogModel.java
│   │       │   ├── ConsumeQueueDetailModel.java
│   │       │   ├── ConsumeQueueOffsetModel.java
│   │       │   ├── MQTopicModel.java
│   │       │   └── QueueModel.java
│   │       └── util/                        # 工具类
│   │           ├── ByteConvertUtil.java
│   │           ├── FileContentUtil.java
│   │           ├── LogFileNameUtil.java
│   │           ├── MMapUtil.java
│   │           ├── PutMessageLock.java
│   │           ├── SpinLock.java
│   │           └── UnfairReentrantLock.java
│   └── pom.xml
├── broker/                                  # Broker 数据目录
│   ├── config/                              # 配置文件
│   │   ├── mq-topic.json                   # Topic 配置
│   │   └── consumequeue-offset.json        # 消费进度
│   └── store/                               # 消息存储
│       └── {topic}/                         # 按 Topic 分目录
│           ├── commitlog/                   # CommitLog 文件
│           └── consumequeue/                # ConsumeQueue 索引文件
│               └── {queueId}/               # 按队列ID分目录
└── pom.xml
```

## 🚀 快速开始

### 环境要求
- JDK 8+
- Maven 3.6+

### 构建项目
```bash
# 克隆项目
git clone https://github.com/bigdatahang/mq.git
cd mq

# 编译项目
mvn clean compile

# 运行 Broker
mvn exec:java -pl mqbroker -Dexec.mainClass="com.k.mq.broker.BrokerStartUp"
```

### 配置说明

**Topic 配置文件**：`broker/config/mq-topic.json`
```json
[
  {
    "topic": "order_cancel_topic",
    "latestCommitLog": {
      "fileName": "00000000",
      "offset": 0,
      "offsetLimit": 1048576
    },
    "queueList": [
      {
        "id": 0,
        "fileName": "00000000",
        "lastOffset": 0,
        "latestOffset": 0,
        "offsetLimit": 6291456
      }
    ]
  }
]
```

**消费进度配置文件**：`broker/config/consumequeue-offset.json`
```json
{
  "offsetTable": {
    "topicConsumeGroupDetail": {
      "order_cancel_topic": {
        "consumeGroupDetail": {
          "user_service_group": {
            "0": "00000000#0"
          }
        }
      }
    }
  }
}
```

## 📖 核心设计

### 1. CommitLog 存储模型
```
CommitLog 文件格式：
┌─────────────┬─────────────┬─────────────┐
│  Message 1  │  Message 2  │  Message 3  │
└─────────────┴─────────────┴─────────────┘

每条消息格式：
┌──────────┬────────────────┐
│ Size (4) │ Content (N)    │
└──────────┴────────────────┘
```

### 2. ConsumeQueue 索引模型
```
ConsumeQueue 文件格式（每条记录12字节）：
┌────────┬────────┬────────┬────────┬────────┐
│ Index1 │ Index2 │ Index3 │ Index4 │  ...   │
└────────┴────────┴────────┴────────┴────────┘

每条索引格式（12字节）：
┌─────────────────┬──────────────┬─────────────┐
│FileName (4)     │ MsgIndex (4) │ MsgLen (4)  │
└─────────────────┴──────────────┴─────────────┘
```

### 3. 消息生产和消费流程
```
【生产流程】
1. Producer 发送消息到 Broker
2. 写入 CommitLog（顺序写）
3. 分发到 ConsumeQueue（异步或同步）
4. 更新 Topic 配置（offset）
5. 定时刷新到磁盘

【消费流程】
1. Consumer 从 ConsumeQueue 读取索引
2. 根据索引从 CommitLog 读取消息内容
3. 返回消息给 Consumer
4. Consumer ACK 确认消费
5. 更新消费进度到 consumequeue-offset.json
```

### 4. 文件自动切换
- 当 `offset` 达到 `offsetLimit` 时自动创建新文件
- 文件名递增：`00000000` → `00000001` → `00000002`
- CommitLog 默认：1MB，ConsumeQueue 默认：6MB

### 5. 并发安全
- 写入操作使用锁保护（SpinLock 或 ReentrantLock）
- Offset 使用 AtomicInteger/AtomicLong 保证原子性
- MappedByteBuffer position 精确控制
- 读写 Buffer 分离，避免并发冲突

## 🔧 开发中的功能

> ⚠️ **注意**：以下功能正在开发中或计划开发

- [ ] **网络通信**
  - Netty 网络层
  - Producer 客户端
  - Consumer 客户端
  - 通信协议设计（基于 Netty）

- [ ] **高级特性**
  - 消息过滤
  - 延迟消息
  - 事务消息
  - 顺序消息
  - 消息重试

- [ ] **性能优化**
  - 批量写入
  - 异步刷盘优化
  - 零拷贝传输
  - 读写分离

- [ ] **运维功能**
  - 消息查询
  - 性能监控
  - 管理控制台
  - 集群支持
  - 主从复制

## 📝 代码规范

- 所有类和方法都有完整的 JavaDoc 注释
- 遵循阿里巴巴 Java 开发规范
- 代码格式统一，命名清晰
- 异常处理规范，日志记录完整

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 License

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 👨‍💻 作者

**yihang07** - [GitHub](https://github.com/bigdatahang)

## 🙏 致谢

- 参考了 [Apache RocketMQ](https://rocketmq.apache.org/) 的设计思想
- 感谢所有为本项目做出贡献的开发者

## 📚 学习资源

- [RocketMQ 官方文档](https://rocketmq.apache.org/docs/quick-start/)
- [Java NIO MappedByteBuffer](https://docs.oracle.com/javase/8/docs/api/java/nio/MappedByteBuffer.html)
- [消息队列设计精要](https://github.com/doocs/advanced-java/blob/main/docs/high-concurrency/mq-design.md)

---

⭐ 如果这个项目对你有帮助，请给个 Star 支持一下！

**项目状态**：🚧 持续开发中...

**最后更新**：2026-02-23
