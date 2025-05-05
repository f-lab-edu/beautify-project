package com.bp.cache.serializer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.RedisSerializer;

class RedisCompressionSerializerTest {

    @Test
    @DisplayName("null 값을 serialize 할 경우 빈 배열이 리턴된다.")
    void getEmptyArrayIfSerializeNull() {
        // given
        RedisSerializer<String> mockedDelegateSerializer = mock(RedisSerializer.class);
        RedisCompressionSerializer<String> serializer = new RedisCompressionSerializer<>(
            mockedDelegateSerializer, 100);

        // when
        byte[] result = serializer.serialize(null);

        // then
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    @DisplayName("threshold 보다 작은 데이터는 압축을 하지 않는다.")
    void notCompressIfDataIsSmallerThanThreshold() {
        // given
        RedisSerializer<String> mockedDelegatedSerializer = mock(RedisSerializer.class);
        RedisCompressionSerializer<String> serializer = new RedisCompressionSerializer<>(
            mockedDelegatedSerializer, 100);

        String testData = "short";
        byte[] original = testData.getBytes(StandardCharsets.UTF_8);

        when(mockedDelegatedSerializer.serialize(testData)).thenReturn(original);

        // when
        byte[] result = serializer.serialize(testData);

        // then
        assertArrayEquals(original, result);
    }

    @Test
    @DisplayName("threshold 보다 큰 데이터는 압축을 한다.")
    void compressIfDataIsBiggerThanThreshold() {
        // given
        RedisSerializer<String> mockedDelegateSerializer = mock(RedisSerializer.class);
        RedisCompressionSerializer<String> serializer = new RedisCompressionSerializer<>(
            mockedDelegateSerializer, 100);

        String testData = "ABC".repeat(200);
        byte[] original = testData.getBytes(StandardCharsets.UTF_8);

        when(mockedDelegateSerializer.serialize(testData)).thenReturn(original);

        // when
        byte[] result = serializer.serialize(testData);

        // then
        assertNotNull(result);
        assertNotEquals(original.length, result.length);
        assertTrue(isCompressed(result));
    }

    private boolean isCompressed(byte[] bytes) {
        return bytes != null && bytes.length >= 2 && isGzipHeader(bytes);
    }

    private boolean isGzipHeader(byte[] bytes) {
        return bytes[0] == (byte) 0x1f && bytes[1] == (byte) 0x8b;
    }

}
