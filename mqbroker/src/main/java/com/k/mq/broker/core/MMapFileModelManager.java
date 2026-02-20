package com.k.mq.broker.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 内存映射文件模型管理器
 * 管理Topic与MMapFileModel的映射关系
 * 
 * @author yihang07
 */
public class MMapFileModelManager {
    /** Topic到内存映射文件模型的映射Map */
    private Map<String, MMapFileModel> mmapFileModelMap = new HashMap<>();

    /**
     * 添加Topic的内存映射文件模型
     * 
     * @param topic 主题名称
     * @param mmapFileModel 内存映射文件模型
     */
    public void put(String topic, MMapFileModel mmapFileModel) {
        mmapFileModelMap.put(topic, mmapFileModel);
    }

    /**
     * 获取Topic的内存映射文件模型
     * 
     * @param topic 主题名称
     * @return 内存映射文件模型，如果不存在则返回null
     */
    public MMapFileModel get(String topic) {
        return mmapFileModelMap.get(topic);
    }
}
