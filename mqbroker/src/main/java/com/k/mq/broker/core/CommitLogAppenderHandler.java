package com.k.mq.broker.core;

import com.k.mq.broker.cache.CommonCache;
import com.k.mq.broker.model.CommitLogModel;
import com.k.mq.broker.model.MQTopicModel;
import com.k.mq.broker.util.CommitLogFileNameUtil;

import java.io.File;
import java.io.IOException;

import static com.k.mq.broker.constants.BrokerConstants.BASE_STORE_PATH;

/**
 * CommitLog消息追加处理器
 * 负责管理Topic的CommitLog文件映射和消息的读写操作
 *
 * @author yihang07
 */
public class CommitLogAppenderHandler {
    /**
     * 内存映射文件模型管理器
     */
    public MMapFileModelManager mmapFileModelManager = new MMapFileModelManager();

    /**
     * 准备CommitLog文件的内存映射加载
     *
     * @param topic 主题名称
     * @throws IOException 当文件操作失败时抛出
     */
    public void prepareMMapLoading(String topic) throws IOException {
        String filePath = getLatestCommitLogFile(topic);
        MMapFileModel mmapFileModel = new MMapFileModel();
        mmapFileModel.loadFileInMMap(filePath, 0, 1 * 1024 * 1024);
        mmapFileModelManager.put(topic, mmapFileModel);
    }

    /**
     * 获取Topic的最新CommitLog文件路径
     * 根据当前offset和offsetLimit判断是使用现有文件还是创建新文件
     *
     * @param topic 主题名称
     * @return CommitLog文件的完整路径
     * @throws IllegalArgumentException 如果Topic不存在
     * @throws IllegalStateException 如果offset超过offsetLimit
     */
    private String getLatestCommitLogFile(String topic) {
        // 从缓存中获取Topic配置
        MQTopicModel mqTopicModel = CommonCache.getMqTopicModelMap().get(topic);
        if (mqTopicModel == null) {
            throw new IllegalArgumentException("topic does not exist, topic is: " + topic);
        }
        
        // 获取CommitLog配置信息
        CommitLogModel commitLogModel = mqTopicModel.getCommitLogModel();
        long diff = commitLogModel.getOffsetLimit() - commitLogModel.getOffset();
        
        String filePath;
        if (diff == 0) {
            // offset已达到上限，需要创建新的CommitLog文件
            filePath = createNewCommitLogFile(topic, commitLogModel);
        } else if (diff > 0) {
            // offset未达到上限，使用当前CommitLog文件
            filePath = CommonCache.getGlobalProperties().getMqHome()
                    + BASE_STORE_PATH
                    + topic
                    + "/"
                    + commitLogModel.getFileName();
        } else {
            // offset超过了offsetLimit，这是异常情况
            throw new IllegalStateException(
                String.format("Invalid CommitLog state for topic '%s': offset=%d, offsetLimit=%d",
                    topic, commitLogModel.getOffset(), commitLogModel.getOffsetLimit())
            );
        }
        return filePath;
    }

    /**
     * 创建新的CommitLog文件
     * 生成新的文件名（递增），创建物理文件，并返回完整路径
     *
     * @param topic 主题名称
     * @param commitLogModel CommitLog配置模型
     * @return 新创建的CommitLog文件的完整路径
     * @throws RuntimeException 当文件创建失败时抛出
     */
    private String createNewCommitLogFile(String topic, CommitLogModel commitLogModel) {
        // 生成新的文件名（在原文件名基础上递增）
        String newFileName = CommitLogFileNameUtil.incrCommitLogName(commitLogModel.getFileName());
        
        // 构建完整的文件路径
        String fullPath = CommonCache.getGlobalProperties().getMqHome()
                + BASE_STORE_PATH
                + topic
                + "/"
                + newFileName;
        
        // 创建物理文件
        try {
            File file = new File(fullPath);  // 使用完整路径而不是只有文件名
            // 确保父目录存在
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            // 创建文件
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create CommitLog file: " + fullPath, e);
        }
        
        return fullPath;
    }

    /**
     * 追加消息到指定Topic的CommitLog
     *
     * @param topic   主题名称
     * @param content 消息内容
     * @throws RuntimeException 当Topic不存在时抛出
     */
    public void appendMessage(String topic, String content) {
        MMapFileModel mmapFileModel = mmapFileModelManager.get(topic);
        if (mmapFileModel == null) {
            throw new RuntimeException("topic does not exist");
        }
        mmapFileModel.writeContent(content.getBytes());
    }

    /**
     * 从指定Topic的CommitLog读取消息内容
     *
     * @param topic 主题名称
     * @throws RuntimeException 当Topic不存在时抛出
     */
    public void readContent(String topic) {
        MMapFileModel mmapFileModel = mmapFileModelManager.get(topic);
        if (mmapFileModel == null) {
            throw new RuntimeException("topic does not exist");
        }
        byte[] content = mmapFileModel.readContent(0, 10);
        System.out.println(new String(content));
    }
}
