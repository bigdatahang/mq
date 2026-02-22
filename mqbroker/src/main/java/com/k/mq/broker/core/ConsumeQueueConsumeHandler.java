package com.k.mq.broker.core;

import com.k.mq.broker.cache.CommonCache;
import com.k.mq.broker.model.ConsumeQueueDetailModel;
import com.k.mq.broker.model.ConsumeQueueOffsetModel;
import com.k.mq.broker.model.MQTopicModel;
import com.k.mq.broker.model.QueueModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ConsumeQueue消费处理器
 * 负责处理消费者从ConsumeQueue中消费消息的逻辑
 *
 * @author yihang07
 */
public class ConsumeQueueConsumeHandler {

    /**
     * 消费消息
     * 根据消费组和队列ID从ConsumeQueue中读取消息索引，并从CommitLog中获取实际消息内容
     *
     * @param topic        主题名称
     * @param queueId      队列ID
     * @param consumeGroup 消费组名称
     * @return 消息内容的字节数组
     * @throws IllegalArgumentException 当topic或参数不存在时抛出
     * @throws IllegalStateException    当消费偏移量数据格式错误时抛出
     */
    public byte[] consume(String topic, String queueId, String consumeGroup) {
        // 1. 参数校验
        if (topic == null || topic.isEmpty()) {
            throw new IllegalArgumentException("topic cannot be null or empty");
        }
        if (queueId == null || queueId.isEmpty()) {
            throw new IllegalArgumentException("queueId cannot be null or empty");
        }
        if (consumeGroup == null || consumeGroup.isEmpty()) {
            throw new IllegalArgumentException("consumeGroup cannot be null or empty");
        }

        // 2. 获取Topic配置
        Map<String, MQTopicModel> mqTopicModelMap = CommonCache.getMqTopicModelMap();
        if (mqTopicModelMap == null) {
            throw new IllegalStateException("MQTopicModelMap is not initialized");
        }

        MQTopicModel mqTopicModel = mqTopicModelMap.get(topic);
        if (mqTopicModel == null) {
            throw new IllegalArgumentException(String.format("topic does not exist: %s", topic));
        }

        // 3. 获取消费偏移量信息
        ConsumeQueueOffsetModel consumeQueueOffsetModel = CommonCache.getConsumeQueueOffsetModel();
        if (consumeQueueOffsetModel == null) {
            throw new IllegalStateException("ConsumeQueueOffsetModel is not initialized");
        }

        ConsumeQueueOffsetModel.OffsetTable offsetTable = consumeQueueOffsetModel.getOffsetTable();
        if (offsetTable == null) {
            throw new IllegalStateException("OffsetTable is not initialized");
        }

        Map<String, ConsumeQueueOffsetModel.ConsumeGroupDetail> topicConsumeGroupDetail =
                offsetTable.getTopicConsumeGroupDetail();
        if (topicConsumeGroupDetail == null) {
            throw new IllegalStateException("TopicConsumeGroupDetail is not initialized");
        }

        // 4. 获取或创建消费组详情
        ConsumeQueueOffsetModel.ConsumeGroupDetail consumeGroupDetail = topicConsumeGroupDetail.get(topic);
        if (consumeGroupDetail == null) {
            // 首次消费该topic，创建新的消费组详情
            consumeGroupDetail = new ConsumeQueueOffsetModel.ConsumeGroupDetail();
            topicConsumeGroupDetail.put(topic, consumeGroupDetail);
        }

        // 5. 获取或初始化队列偏移量映射
        Map<String, Map<String, String>> consumeGroupOffsetMap = consumeGroupDetail.getConsumeGroupDetail();
        Map<String, String> queueOffsetDetailMap = consumeGroupOffsetMap.get(consumeGroup);

        if (queueOffsetDetailMap == null) {
            // 首次消费该消费组，初始化所有队列的偏移量为0
            queueOffsetDetailMap = new HashMap<>();
            List<QueueModel> queueList = mqTopicModel.getQueueList();

            if (queueList != null) {
                for (QueueModel queueModel : queueList) {
                    // 格式：fileName#offset，例如 "00000000#0"
                    queueOffsetDetailMap.put(String.valueOf(queueModel.getId()), "00000000#0");
                }
            }

            // 将新创建的映射添加回消费组偏移量映射中
            consumeGroupOffsetMap.put(consumeGroup, queueOffsetDetailMap);
        }

        // 6. 获取指定队列的消费偏移量
        String consumeStr = queueOffsetDetailMap.get(queueId);
        if (consumeStr == null) {
            throw new IllegalArgumentException(
                    String.format("queueId does not exist in consumeGroup: queueId=%s, consumeGroup=%s",
                            queueId, consumeGroup)
            );
        }

        // 7. 解析消费偏移量字符串
        // 格式：fileName#offset，例如 "00000000#12"
        String[] consumeArray = consumeStr.split("#");
        if (consumeArray.length != 2) {
            throw new IllegalStateException(
                    String.format("Invalid consume offset format: %s, expected format: fileName#offset",
                            consumeStr)
            );
        }

        String consumeQueueFileName = consumeArray[0];  // ConsumeQueue文件名
        String consumeQueueOffsetStr = consumeArray[1];

        int consumeQueueOffset;
        try {
            consumeQueueOffset = Integer.parseInt(consumeQueueOffsetStr);
        } catch (NumberFormatException e) {
            throw new IllegalStateException(
                    String.format("Invalid consume offset value: %s", consumeQueueOffsetStr), e
            );
        }

        //8. 从ConsumeQueue中读取消息索引
        // 根据 consumeQueueFileName 和 consumeQueueOffset 定位到ConsumeQueue中的位置
        // 读取12字节的索引数据：commitLogFileName(4) + msgIndex(4) + msgLength(4)
        ConsumeQueueMMapFileModelManager consumeQueueMMapFileModelManager = CommonCache.getConsumeQueueMMapFileModelManager();
        ConsumeQueueMMapFileModel consumeQueueMMapFileModel = consumeQueueMMapFileModelManager.get(topic, Integer.parseInt(queueId));
        byte[] content = consumeQueueMMapFileModel.readContent(consumeQueueOffset);
        ConsumeQueueDetailModel consumeQueueDetailModel = new ConsumeQueueDetailModel();
        consumeQueueDetailModel.readFromBytes(content);
        System.out.println("消费到ConsumeQueueDetailModel内容：" + consumeQueueDetailModel);
        //9. 从CommitLog中读取实际消息
        // 根据索引信息，从对应的CommitLog文件中读取消息内容
        CommitLogMMapFileModel commitLogMMapFileModel = CommonCache.getCommitLogMMapFileModelManager().get(topic);
        //10. 更新消费偏移量
        // 消费成功后，更新 queueOffsetDetailMap 中的偏移量
        return commitLogMMapFileModel.readContent(consumeQueueDetailModel.getMsgIndex(), consumeQueueDetailModel.getMsgLength());
    }

    /**
     * 确认消息消费（ACK）
     * 更新consumequeue-offset文件，持久化消费进度
     *
     * @param topic        主题名称
     * @param queueId      队列ID
     * @param consumeGroup 消费组名称
     */
    public boolean ack(String topic, String queueId, String consumeGroup) {
        // 更新内存中的消费偏移量
        ConsumeQueueOffsetModel.OffsetTable offsetTable = CommonCache.getConsumeQueueOffsetModel().getOffsetTable();
        Map<String, ConsumeQueueOffsetModel.ConsumeGroupDetail> topicConsumeGroupDetail = offsetTable.getTopicConsumeGroupDetail();
        ConsumeQueueOffsetModel.ConsumeGroupDetail consumeGroupDetail = topicConsumeGroupDetail.get(topic);
        Map<String, Map<String, String>> consumeOffsetDetailMap = consumeGroupDetail.getConsumeGroupDetail();
        Map<String, String> queueOffsetDetailMap = consumeOffsetDetailMap.get(consumeGroup);
        String offsetStr = queueOffsetDetailMap.get(queueId);
        String fileName = offsetStr.split("#")[0];
        Integer offset = Integer.parseInt(offsetStr.split("#")[1]);
        int currentOffset = offset + 12;
        queueOffsetDetailMap.put(queueId, fileName + "#" + currentOffset);
        return true;
    }
}
