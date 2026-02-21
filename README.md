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
  
- [x] **内存管理**
  - MappedByteBuffer 自动释放（解决 JDK8 文件删除 bug）
  - 参考 RocketMQ 的 Cleaner 实现
  
- [x] **并发控制**
  - 自旋锁（SpinLock）实现
  - 非公平可重入锁（UnfairReentrantLock）
  - 写入消息锁（PutMessageLock）接口

- [x] **配置管理**
  - Topic 配置加载（JSON 格式）
  - 全局配置管理
  - CommitLog 元数据管理

- [x] **工具类**
  - 字节转换工具（ByteConvertUtil）
  - 文件名生成工具（CommitLogFileNameUtil）
  - 文件内容读写工具（FileContentUtil）

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
│   │       ├── cache/           # 缓存层
│   │       ├── config/          # 配置管理
│   │       ├── constants/       # 常量定义
│   │       ├── core/            # 核心功能
│   │       │   ├── CommitLogAppenderHandler.java
│   │       │   ├── MMapFileModel.java
│   │       │   └── MMapFileModelManager.java
│   │       ├── model/           # 数据模型
│   │       │   ├── CommitLogModel.java
│   │       │   ├── CommitLogMessageModel.java
│   │       │   ├── MQTopicModel.java
│   │       │   └── QueueModel.java
│   │       └── util/            # 工具类
│   │           ├── ByteConvertUtil.java
│   │           ├── CommitLogFileNameUtil.java
│   │           ├── FileContentUtil.java
│   │           ├── MMapUtil.java
│   │           ├── PutMessageLock.java
│   │           ├── SpinLock.java
│   │           └── UnfairReentrantLock.java
│   └── pom.xml
├── broker/                      # Broker 数据目录
│   ├── config/                  # 配置文件
│   │   └── mq-topic.json
│   └── store/                   # 消息存储
│       └── {topic}/             # 按 Topic 分目录
│           └── {commitlog}      # CommitLog 文件
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
        "id": 1,
        "minOffset": 1001,
        "maxOffset": 10001,
        "currentOffset": 2000
      }
    ]
  }
]
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

### 2. 文件自动切换
- 当 `offset` 达到 `offsetLimit` 时自动创建新文件
- 文件名递增：`00000000` → `00000001` → `00000002`
- 每个文件默认大小：1MB（可配置）

### 3. 并发安全
- 写入操作使用锁保护（SpinLock 或 ReentrantLock）
- Offset 使用 AtomicLong 保证原子性
- MappedByteBuffer position 精确控制

## 🔧 开发中的功能

> ⚠️ **注意**：以下功能正在开发中，尚未完成

- [ ] **消费者（Consumer）**
  - 消息拉取
  - 消费位点管理
  - 消费组（Consumer Group）

- [ ] **索引（Index）**
  - ConsumeQueue 索引
  - IndexFile 消息索引
  - Key 索引

- [ ] **网络通信**
  - Netty 网络层
  - Producer 客户端
  - Consumer 客户端
  - 通信协议设计

- [ ] **高级特性**
  - 消息过滤
  - 延迟消息
  - 事务消息
  - 顺序消息

- [ ] **运维功能**
  - 消息查询
  - 性能监控
  - 管理控制台
  - 集群支持

## 📝 代码规范

- 所有类和方法都有完整的 JavaDoc 注释
- 遵循阿里巴巴 Java 开发规范
- 代码格式统一，命名清晰

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

---

⭐ 如果这个项目对你有帮助，请给个 Star 支持一下！

**项目状态**：🚧 持续开发中...
