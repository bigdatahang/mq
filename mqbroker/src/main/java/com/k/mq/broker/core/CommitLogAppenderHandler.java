package com.k.mq.broker.core;

import com.k.mq.broker.cache.CommonCache;
import com.k.mq.broker.model.CommitLogMessageModel;

import java.io.IOException;

import static com.k.mq.broker.constants.BrokerConstants.COMMIT_LOG_DEFAULT_MAPPED_SIZE;

/**
 * CommitLog消息追加处理器
 * 负责管理Topic的CommitLog文件映射和消息的读写操作
 *
 * @author yihang07
 */
public class CommitLogAppenderHandler {
    /**
     * 准备CommitLog文件的内存映射加载
     *
     * @param topic 主题名称
     * @throws IOException 当文件操作失败时抛出
     */
    public void prepareMMapLoading(String topic) throws IOException {
        CommitLogMMapFileModel commitLogMMapFileModelCommitLog = new CommitLogMMapFileModel();
        commitLogMMapFileModelCommitLog.loadFileInMMap(topic, 0, COMMIT_LOG_DEFAULT_MAPPED_SIZE);
        CommonCache.getCommitLogMMapFileModelManager().put(topic, commitLogMMapFileModelCommitLog);
    }

    /**
     * 追加消息到指定Topic的CommitLog
     *
     * @param topic   主题名称
     * @param content 消息内容
     * @throws RuntimeException 当Topic不存在时抛出
     */
    public void appendMessage(String topic, byte[] content) throws IOException {
        CommitLogMMapFileModel commitLogMMapFileModel = CommonCache.getCommitLogMMapFileModelManager().get(topic);
        if (commitLogMMapFileModel == null) {
            throw new RuntimeException("topic does not exist");
        }
        CommitLogMessageModel commitLogMessageModel = new CommitLogMessageModel();
        commitLogMessageModel.setContent(content);
        commitLogMMapFileModel.writeContent(commitLogMessageModel);
    }
}
