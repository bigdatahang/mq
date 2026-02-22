package com.k.mq.broker;

import com.k.mq.broker.cache.CommonCache;
import com.k.mq.broker.config.ConsumeQueueOffsetLoader;
import com.k.mq.broker.config.GlobalPropertiesLoader;
import com.k.mq.broker.config.MQTopicLoader;
import com.k.mq.broker.core.CommitLogAppenderHandler;
import com.k.mq.broker.core.ConsumeQueueAppenderHandler;
import com.k.mq.broker.core.ConsumeQueueConsumeHandler;
import com.k.mq.broker.model.MQTopicModel;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Broker启动类
 * 负责初始化Broker所需的各种配置和处理器
 *
 * @author yihang07
 */
public class BrokerStartUp {
    /**
     * MQ主题配置加载器
     */
    public static MQTopicLoader mqTopicLoader;
    /**
     * 全局配置加载器
     */
    public static GlobalPropertiesLoader globalPropertiesLoader;

    public static ConsumeQueueOffsetLoader consumeQueueOffsetLoader;
    /**
     * CommitLog消息追加处理器
     */
    public static CommitLogAppenderHandler commitLogAppenderHandler;

    public static ConsumeQueueAppenderHandler consumeQueueAppenderHandler;

    public static ConsumeQueueConsumeHandler consumeQueueConsumeHandler;

    /**
     * 初始化Broker的各项配置和资源
     * 包括全局配置、Topic配置、以及CommitLog文件的内存映射加载
     *
     * @throws IOException 当文件操作失败时抛出
     */
    public static void initProperties() throws IOException {
        // 加载全局配置
        globalPropertiesLoader = new GlobalPropertiesLoader();
        globalPropertiesLoader.loadProperties();

        // 加载Topic配置
        mqTopicLoader = new MQTopicLoader();
        mqTopicLoader.loadProperties();

        consumeQueueOffsetLoader = new ConsumeQueueOffsetLoader();
        consumeQueueOffsetLoader.loadProperties();
        consumeQueueOffsetLoader.startRefreshConsumeQueueOffsetTask();

        // 启动异步县线程刷写磁盘
        mqTopicLoader.startRefreshMQTopicTask();

        // 初始化CommitLog处理器
        commitLogAppenderHandler = new CommitLogAppenderHandler();
        consumeQueueAppenderHandler = new ConsumeQueueAppenderHandler();

        consumeQueueConsumeHandler = new ConsumeQueueConsumeHandler();

        // 为每个Topic准备CommitLog文件的内存映射
        for (MQTopicModel mqTopicModel : CommonCache.getMqTopicModelMap().values()) {
            String topic = mqTopicModel.getTopic();
            commitLogAppenderHandler.prepareMMapLoading(topic);
            consumeQueueAppenderHandler.prepareConsumeQueueMMapLoading(topic);
        }
    }

    /**
     * Broker启动入口
     *
     * @param args 命令行参数
     * @throws IOException 当初始化失败时抛出
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        initProperties();
        String topic = "order_cancel_topic";
        String userConsumeGroup = "user_service_group";
        String orderConsumeGroup = "order_service_group";
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    byte[] content = consumeQueueConsumeHandler.consume(topic, 0, userConsumeGroup);
                    if (content != null) {
                        System.out.println(userConsumeGroup + ",消费内容:" + new String(content));
                        consumeQueueConsumeHandler.ack(topic, 0, userConsumeGroup);
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    byte[] content = consumeQueueConsumeHandler.consume(topic, 0, orderConsumeGroup);
                    if (content != null) {
                        System.out.println(orderConsumeGroup + ",消费内容:" + new String(content));
                        consumeQueueConsumeHandler.ack(topic, 0, orderConsumeGroup);
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }).start();
        AtomicInteger i = new AtomicInteger(0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                while (true) {
                    try {
                        commitLogAppenderHandler.appendMessage(topic, ("message_" + (i.getAndIncrement())).getBytes());
                        Thread.sleep(100);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
}
