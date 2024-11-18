package com.beautify_project.bp_app_api.records;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record ImageFiles(List<MultipartFile> files) {

    public ImageFiles {
        createEmptyListIfNull(files);
    }

    private void createEmptyListIfNull(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            files = new ArrayList<>();
        }
    }

    public boolean isEmpty() {
        return files == null || files.isEmpty();
    }

    public int size() {
        return files.size();
    }

    public MultipartFile get(final int index) {
        return files().get(index);
    }
}
