package com.k.mq.broker.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 内存映射文件模型管理器
 * 管理Topic与MMapFileModel的映射关系
 * 
 * @author yihang07
 */
public class CommitLogMMapFileModelManager {
    /** Topic到内存映射文件模型的映射Map */
    private Map<String, CommitLogMMapFileModel> mmapFileModelMap = new HashMap<>();

    /**
     * 添加Topic的内存映射文件模型
     * 
     * @param topic 主题名称
     * @param mmapFileModelCommitLog 内存映射文件模型
     */
    public void put(String topic, CommitLogMMapFileModel mmapFileModelCommitLog) {
        mmapFileModelMap.put(topic, mmapFileModelCommitLog);
    }

    /**
     * 获取Topic的内存映射文件模型
     * 
     * @param topic 主题名称
     * @return 内存映射文件模型，如果不存在则返回null
     */
    public CommitLogMMapFileModel get(String topic) {
        return mmapFileModelMap.get(topic);
    }
}
