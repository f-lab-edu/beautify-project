package com.bp.cache.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP 압축, 압축 해제
 */
public class GZIPCompression {

    private static final int BUFFER_SIZE = 1024;

    public static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipStream = new GZIPOutputStream(byteArrayStream)) {
            int offset = 0;
            while (offset < data.length) {
                int length = Math.min(BUFFER_SIZE, data.length - offset);
                gzipStream.write(data, offset, length);
                offset += length;
            }
        }

        return byteArrayStream.toByteArray();
    }

    public static byte[] decompress(byte[] compressedData) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (GZIPInputStream gzipStream = new GZIPInputStream(byteArrayInputStream)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = gzipStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
        }
        return outputStream.toByteArray();
    }
}
