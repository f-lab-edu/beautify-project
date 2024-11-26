package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.shop.ImageFiles;
import com.beautify_project.bp_app_api.exception.FileLoadException;
import com.beautify_project.bp_app_api.exception.FileStoreException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class FileSystemStorageService implements StorageService {

    @Value("${storage.file-system-path:null}")
    private String fileSystemPath;


    @Override
    public void storeImageFiles(final ImageFiles imageFiles, final String shopId)
        throws FileStoreException {
        try {
            createDataDirectoryIfNotExists();
            final String directoryPathToBeStored = getDirectoryPathByShopId(shopId);
            writeFiles(imageFiles, directoryPathToBeStored, shopId);
        } catch (IOException exception) {
            log.error("", exception);
            throw new FileStoreException(exception.getMessage());
        }
    }

    @Override
    public ImageFiles loadImageFiles(final String filePath) throws FileLoadException {

        return null;
    }

    @Override
    public byte[] loadThumbnail(final String shopId) throws FileLoadException {
        return new byte[0];
    }

    private String getDirectoryPathByShopId(final String shopId) {
        return shopId.charAt(0) + shopId;
    }

    private void createDataDirectoryIfNotExists() throws IOException {
        Path dataDirectoryPath = Path.of(fileSystemPath);
        if (!Files.exists(dataDirectoryPath)) {
            Files.createDirectory(dataDirectoryPath);
        }
    }

    private void writeFiles(final ImageFiles imageFiles, final String directoryPath,
        final String shopId) throws IOException {

        boolean isFirstFile = true;
        int i = 1;
        for (MultipartFile imageFile : imageFiles.files()) {
            File fileToBeStored;
            if (isFirstFile) {
                fileToBeStored = new File(directoryPath + "/" + shopId + "_thumbnail");
                isFirstFile = false;
            } else {
                fileToBeStored = new File(directoryPath + "/" + shopId + "_" + i);
            }
            imageFile.transferTo(fileToBeStored);
            i++;
        }
    }
}
