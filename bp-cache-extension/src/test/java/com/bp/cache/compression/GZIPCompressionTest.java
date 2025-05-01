package com.bp.cache.compression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class GZIPCompressionTest {

    @DisplayName("String 타입의 원본 데이터를 압축하고 해제하면 원본과 동일하다.")
    @ParameterizedTest(name = "데이터: ''{0}''")
    @MethodSource("dataProvider")
    void sameAsOriginalIfCompressionAndDecompression(final String originalData) {
        try {
            byte[] compressed = GZIPCompression.compress(
                originalData.getBytes(StandardCharsets.UTF_8));
            byte[] decompressed = GZIPCompression.decompress(compressed);
            String decompressedData = new String(decompressed, StandardCharsets.UTF_8);
            assertEquals(originalData, decompressedData);
        } catch (IOException e) {
            fail("압축 및 해제 실패");
        }
    }

    static Stream<Arguments> dataProvider() {
        return Stream.of(
            Arguments.of("abcdef"),
            Arguments.of("한글은"),
            Arguments.of("{\"key2\":\"value2\",\"key1\":\"value1\",\"key4\":1,\"key3\":\"한글\"}")
        );
    }

}
