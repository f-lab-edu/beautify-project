package com.bp.cache.serializer;

import com.bp.cache.compression.GZIPCompression;
import java.io.IOException;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * 기존 RedisSerializer 를 확장하여 특정 임계값 이상의 데이터는 자동으로 압축
 */
public class RedisCompressionSerializer<T> implements RedisSerializer<T> {

    private static final byte[] EMPTY_ARRAY = new byte[0];
    private final RedisSerializer<T> delegateSerializer;
    private final int compressionThreshold;

    public RedisCompressionSerializer(RedisSerializer<T> delegateSerializer, int compressionThreshold) {
        this.delegateSerializer = delegateSerializer;
        this.compressionThreshold = compressionThreshold;
    }

    @Override
    public byte[] serialize(final T value) throws SerializationException {
        if (value == null) {
            return EMPTY_ARRAY;
        }
        try {
            byte[] serialized = delegateSerializer.serialize(value);
            serialized = compressIfNeeded(serialized);
            return serialized;
        } catch (IOException e) {
            throw new SerializationException("Failed to serialize", e);
        }
    }

    private byte[] compressIfNeeded(byte[] serialized) throws IOException {
        if (serialized != null && serialized.length >= compressionThreshold) {
            return GZIPCompression.compress(serialized);
        }
        return serialized;
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null) {
            return null;
        }
        try {
            bytes = decompressIfNeeded(bytes);
            return delegateSerializer.deserialize(bytes);
        } catch (IOException e) {
            throw new SerializationException("Failed to deserialize", e);
        }
    }

    private byte[] decompressIfNeeded(byte[] bytes) throws IOException {
        return isCompressed(bytes) ? GZIPCompression.decompress(bytes) : bytes;
    }

    private boolean isCompressed(byte[] bytes) {
        return bytes != null && bytes.length >= 2 && isGzipHeader(bytes);
    }

    private boolean isGzipHeader(byte[] bytes) {
        return bytes[0] == (byte) 0x1f && bytes[1] == (byte) 0x8b;
    }
}
