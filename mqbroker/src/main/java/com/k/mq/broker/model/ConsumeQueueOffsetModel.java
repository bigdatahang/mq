package com.k.mq.broker.model;

import java.util.Map;

public class ConsumeQueueOffsetModel {
    private OffsetTable offsetTable;

    public OffsetTable getOffsetTable() {
        return offsetTable;
    }

    public void setOffsetTable(OffsetTable offsetTable) {
        this.offsetTable = offsetTable;
    }

    private static class OffsetTable {
        private Map<String, ConsumeGroupDetail> topicConsumeGroupDetail;

        public Map<String, ConsumeGroupDetail> getTopicConsumeGroupDetail() {
            return topicConsumeGroupDetail;
        }

        public void setTopicConsumeGroupDetail(Map<String, ConsumeGroupDetail> topicConsumeGroupDetail) {
            this.topicConsumeGroupDetail = topicConsumeGroupDetail;
        }
    }

    private static class ConsumeGroupDetail {
        private Map<String, Map<String, String>> consumeGroupDetail;

        public Map<String, Map<String, String>> getConsumeGroupDetail() {
            return consumeGroupDetail;
        }

        public void setConsumeGroupDetail(Map<String, Map<String, String>> consumeGroupDetail) {
            this.consumeGroupDetail = consumeGroupDetail;
        }
    }
}
