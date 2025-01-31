package com.bp.common.kafka.partitioner;

import java.util.Map;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.InvalidRecordException;

public class ShopLikeEventCustomPartitioner implements Partitioner {

    @Override
    public int partition(final String topic, final Object key, final byte[] keyBytes, final Object value,
        final byte[] valueByes, final Cluster cluster) {
        if (keyBytes == null) {
            throw new InvalidRecordException("메세지 키를 입력해주세요.");
        }

        if (!(key instanceof Long)) {
            throw new InvalidRecordException("파티션 키는 Long type 이어야 합니다.");
        }

        final Long shopId = (Long) key;
        final int partitionCount = cluster.partitionsForTopic(topic).size();
        // IMPORTANT: 동일한 shopId는 동일 파티션에서 처리 필요 (좋아요, 좋아요 취소 이벤트들에 대한 순서 보장 필요)
        return Math.toIntExact(shopId % partitionCount);
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(final Map<String, ?> map) {

    }
}
