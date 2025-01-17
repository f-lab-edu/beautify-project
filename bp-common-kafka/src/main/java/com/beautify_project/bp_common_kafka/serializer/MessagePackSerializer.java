package com.beautify_project.bp_common_kafka.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

public class MessagePackSerializer<T> implements Serializer<T> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public byte[] serialize(final String s, final T data) {
        if (data == null) {
            return null;
        }

        try (MessageBufferPacker packer = MessagePack.newDefaultBufferPacker()) {
            // Object를 JSON으로 변환 후 MessagePack으로 직렬화
            packer.packString(OBJECT_MAPPER.writeValueAsString(data));
            return packer.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object to MessagePack", e);
        }

    }
}
