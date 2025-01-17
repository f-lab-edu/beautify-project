package com.beuatify_project.bp_common.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

public class MessagePackDeserializer<T> implements Deserializer<T> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final Class<T> targetClass;

    public MessagePackDeserializer(final Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try (MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(data)) {
            // JSON 문자열로 변환된 데이터를 다시 POJO로 역직렬화
            String jsonString = unpacker.unpackString();
            // JSON -> Object 변환 (Jackson 또는 Gson 사용 가능)
            return OBJECT_MAPPER.readValue(jsonString, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing object from MessagePack", e);
        }
    }
}

