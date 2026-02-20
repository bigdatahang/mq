package com.k.mq.broker.config;

import com.alibaba.fastjson.JSON;
import com.k.mq.broker.cache.CommonCache;
import com.k.mq.broker.model.MQTopicModel;
import com.k.mq.broker.util.FileContentUtil;
import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * MQ主题配置加载器
 * 从配置文件中加载Topic相关的配置信息
 *
 * @author yihang07
 */
public class MQTopicLoader {
    private String filePath;

    /**
     * 加载Topic配置
     * 从mq-topic.json文件中读取并解析所有Topic的配置信息
     */
    public void loadProperties() {
        GlobalProperties globalProperties = CommonCache.getGlobalProperties();
        String mqHome = globalProperties.getMqHome();
        if (StringUtil.isNullOrEmpty(mqHome)) {
            throw new IllegalArgumentException("MQ_HOME IS NULL");
        }
        filePath = mqHome + "/broker/config/mq-topic.json";
        List<MQTopicModel> mqTopicModelList = JSON.parseArray(FileContentUtil.readFromFile(filePath), MQTopicModel.class);
        CommonCache.setMqTopicModelList(mqTopicModelList);
    }

    public void startRefreshMQTopicTask() {
        CommonThreadPoolConfig.refreshThreadExecutor.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        Map<String, MQTopicModel> mqTopicModelMap = CommonCache.getMqTopicModelMap();
                        List<MQTopicModel> mqTopicModelList = new ArrayList<>(mqTopicModelMap.values());
                        FileContentUtil.overwriteToFile(filePath, JSON.toJSONString(mqTopicModelList));
                    }
                }, 0, 15, TimeUnit.SECONDS
        );
    }
}
