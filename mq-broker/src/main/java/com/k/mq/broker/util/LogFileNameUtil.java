package com.k.mq.broker.util;

import com.k.mq.broker.cache.CommonCache;

import static com.k.mq.common.constants.BrokerConstants.*;

/**
 * Log文件名工具类
 * 用于生成和管理Log文件的命名规则
 * 文件名格式：8位数字，如"00000000"、"00000001"等
 *
 * @author yihang07
 */
public class LogFileNameUtil {
    /**
     * 文件名长度固定为8位
     */
    private static final int FILE_NAME_LENGTH = 8;

    /**
     * 最大文件编号（8位数字的最大值）
     */
    private static final int MAX_FILE_NUMBER = 99999999;

    /**
     * 创建第一个CommitLog文件名
     *
     * @return 返回"00000000"
     */
    public static String createFirstCommitLogName() {
        return "00000000";
    }

    /**
     * 根据旧文件名生成下一个CommitLog文件名
     * 将旧文件名对应的数字加1，并补齐8位数字
     * <p>
     * 示例：
     * - "00000000" -> "00000001"
     * - "00000099" -> "00000100"
     * - "12345678" -> "12345679"
     *
     * @param oldFileName 旧文件名，必须是8位数字字符串
     * @return 新的文件名，格式为8位数字字符串
     * @throws IllegalArgumentException 如果文件名长度不是8位
     * @throws IllegalStateException    如果文件编号已达到最大值
     */
    public static String incrCommitLogName(String oldFileName) {
        // 校验文件名长度
        if (oldFileName == null || oldFileName.length() != FILE_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Invalid CommitLog filename: '%s'. Expected %d characters, but got %d",
                            oldFileName == null ? "null" : oldFileName,
                            FILE_NAME_LENGTH,
                            oldFileName == null ? 0 : oldFileName.length())
            );
        }

        // 解析当前文件编号
        int fileNumber;
        try {
            fileNumber = Integer.parseInt(oldFileName);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("filename must be numeric: " + oldFileName, e);
        }

        // 检查是否达到最大值
        if (fileNumber >= MAX_FILE_NUMBER) {
            throw new IllegalStateException("CommitLog file number has reached maximum: " + MAX_FILE_NUMBER);
        }

        // 递增并格式化为8位数字
        fileNumber++;
        return String.format("%08d", fileNumber);
    }

    public static String incrConsumeQueueName(String oldFileName) {
        return incrCommitLogName(oldFileName);
    }

    public static String buildCommitLogFileName(String topic, String commitLogFileName) {
        return CommonCache.getGlobalProperties().getMqHome()
                + BASE_COMMIT_LOG_PATH
                + topic
                + SPLIT
                + commitLogFileName;
    }

    public static String buildConsumeQueueFileName(String topic, Integer queueId, String fileName) {
        return CommonCache.getGlobalProperties().getMqHome()
                + BASE_CONSUME_QUEUE_PATH
                + topic
                + SPLIT
                + queueId
                + SPLIT
                + fileName;
    }
}
