package com.bp.cache.compression;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Disabled
public class GZIPCompressionPerformanceTest {

    private static final Random random = new Random();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd HH:mm");

    private static final int BYTE_TO_KB = 1024;
    private static final int MAX_SIZE = BYTE_TO_KB * BYTE_TO_KB;

    @Test
    @DisplayName("데이터 크기별 GZIP 압축률 테스트")
    void testGZIPCompressionPerformance() throws IOException {
        int startSize = 128; // 128 byte
        for (int size = startSize; size <= MAX_SIZE; size *= 2) {
            byte[] testData = generateJsonData(size);
            double testDataSizeInKB = (double) testData.length / BYTE_TO_KB;
            byte[] compressed = GZIPCompression.compress(testData);
            double compressedSizeInKB = (double) compressed.length / BYTE_TO_KB;

            // 압축률 = (1 - (압축후/압축전)) * 100
            double compressionRatioPercentage = (1.0 - (double) compressed.length / testData.length) * 100;

            System.out.printf(
                "데이터 원본 크기: %d bytes (%.4fKB) | 압축된 데이터 크기: %d bytes (%.4fKB) | 압축률: %.2f%%\n",
                testData.length, testDataSizeInKB, compressed.length, compressedSizeInKB, compressionRatioPercentage
            );
        }
        System.out.println();
    }

    public byte[] generateJsonData(int byteSize) {
        StringBuilder sb = new StringBuilder(byteSize);

        while (sb.length() < byteSize) {
            String json = generateRandomJsonObject();
            sb.append(json);
        }

        String result = sb.substring(0, Math.min(byteSize, sb.length()));
        return result.getBytes(StandardCharsets.UTF_8);
    }

    private String generateRandomJsonObject() {
        StringBuilder json = new StringBuilder();
        json.append("{");

        json.append("\"shopName\":\"").append(randomEngString(random.nextInt(10) + 5)).append("\",");
        json.append("\"userId\":\"").append(randomEngString(random.nextInt(8) + 3)).append("\",");
        json.append("\"reservationId\":").append(random.nextInt(100000)).append(",");

        LocalDateTime startTime = randomDateTime();
        LocalDateTime endTime = startTime.plusHours(random.nextInt(4) + 1); // 시작 시간 이후 1~4시간
        json.append("\"reservationStartTime\":\"").append(startTime.format(DATE_TIME_FORMATTER)).append("\",");
        json.append("\"reservationEndTime\":\"").append(endTime.format(DATE_TIME_FORMATTER)).append("\",");

        json.append("\"itemId\":").append(random.nextInt(100000)).append(",");
        json.append("\"description\":\"").append(randomMixedString(random.nextInt(30) + 10)).append("\"");

        json.append("}");
        return json.toString();
    }

    private LocalDateTime randomDateTime() {
        int year = 2023 + random.nextInt(3); // 2023~2025년
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28); // 단순화 (2월 등 고려 안함)
        int hour = random.nextInt(24);
        int minute = random.nextBoolean() ? 0 : 30; // 00 또는 30분

        return LocalDateTime.of(year, month, day, hour, minute);
    }

    private String randomEngString(int length) {
        char[] engChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(engChars[random.nextInt(engChars.length)]);
        }
        return sb.toString();
    }

    private String randomMixedString(int length) {
        char[] engChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        char[] hangulChars = "가나다라마바사아자차카타파하거너더러머버서어저처커터퍼허".toCharArray();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            if (random.nextBoolean()) {
                sb.append(engChars[random.nextInt(engChars.length)]);
            } else {
                sb.append(hangulChars[random.nextInt(hangulChars.length)]);
            }
        }
        return sb.toString();
    }

}
