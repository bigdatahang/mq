package com.k.mq.broker.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.k.mq.broker.cache.CommonCache;
import com.k.mq.broker.model.ConsumeQueueOffsetModel;
import com.k.mq.broker.util.FileContentUtil;
import io.netty.util.internal.StringUtil;

import java.util.concurrent.TimeUnit;

import static com.k.mq.common.constants.BrokerConstants.DEFAULT_REFRESH_CONSUME_QUEUE_OFFSET_SECOND;


public class ConsumeQueueOffsetLoader {
    private String filePath;

    /**
     * 加载consumequeue配置
     * 从consumequeue-offset.json文件中读取并解析所有consumequeue的配置信息
     */
    public void loadProperties() {
        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String mqHome = globalProperties.getMqHome();
        if (StringUtil.isNullOrEmpty(mqHome)) {
            throw new IllegalArgumentException("MQ_HOME IS NULL");
        }
        filePath = mqHome + "/broker/config/consumequeue-offset.json";
        ConsumeQueueOffsetModel consumeQueueOffsetModel = JSON.parseObject(FileContentUtil.readFromFile(filePath), ConsumeQueueOffsetModel.class);
        CommonCache.setConsumeQueueOffsetModel(consumeQueueOffsetModel);
    }

    /**
     * 开启定时任务刷写JSON文件
     */
    public void startRefreshConsumeQueueOffsetTask() {
        CommonThreadPoolConfig.refreshConsumeQueueOffsetThreadExecutor.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        ConsumeQueueOffsetModel consumeQueueOffsetModel = CommonCache.getConsumeQueueOffsetModel();
                        FileContentUtil.overwriteToFile(filePath, JSON.toJSONString(consumeQueueOffsetModel, SerializerFeature.PrettyFormat));
                        System.out.println("定时线程 开始刷写consumequeue-offset文件");
                    }
                }, 0, DEFAULT_REFRESH_CONSUME_QUEUE_OFFSET_SECOND, TimeUnit.SECONDS
        );
    }
}
