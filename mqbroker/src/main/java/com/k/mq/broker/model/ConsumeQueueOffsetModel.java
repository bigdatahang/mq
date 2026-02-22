package com.k.mq.broker.model;

import java.util.HashMap;
import java.util.Map;

public class ConsumeQueueOffsetModel {
    private OffsetTable offsetTable;

    public OffsetTable getOffsetTable() {
        return offsetTable;
    }

    public void setOffsetTable(OffsetTable offsetTable) {
        this.offsetTable = offsetTable;
    }

    public static class OffsetTable {
        private Map<String, ConsumeGroupDetail> topicConsumeGroupDetail = new HashMap<>();

        public Map<String, ConsumeGroupDetail> getTopicConsumeGroupDetail() {
            return topicConsumeGroupDetail;
        }

        public void setTopicConsumeGroupDetail(Map<String, ConsumeGroupDetail> topicConsumeGroupDetail) {
            this.topicConsumeGroupDetail = topicConsumeGroupDetail;
        }
    }

    public static class ConsumeGroupDetail {
        private Map<String, Map<String, String>> consumeGroupDetail = new HashMap<>();

        public Map<String, Map<String, String>> getConsumeGroupDetail() {
            return consumeGroupDetail;
        }

        public void setConsumeGroupDetail(Map<String, Map<String, String>> consumeGroupDetail) {
            this.consumeGroupDetail = consumeGroupDetail;
        }
    }
}
