package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.shop.ImageFiles;
import com.beautify_project.bp_app_api.exception.FileLoadException;
import com.beautify_project.bp_app_api.exception.FileStoreException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class FileSystemStorageService implements StorageService {

    public static final int FILE_ORDER_FIRST = 0;
    @Value("${storage.path:null}")
    private String storagePath;

    @Override
    public void storeImageFiles(final ImageFiles imageFiles, final String shopId)
        throws FileStoreException {
        try {
            final String directoryPathToBeStored = getDirectoryPathByShopId(shopId);
            writeFiles(imageFiles, directoryPathToBeStored, shopId);
        } catch (IOException | NullPointerException exception) {
            log.error("", exception);
            throw new FileStoreException(exception.getMessage());
        }
    }

    @Override
    public ImageFiles loadImageFiles(final String shopId) throws FileLoadException {

        return null;
    }

    @Override
    public byte[] loadThumbnail(final String shopId) throws FileLoadException {
        return new byte[0];
    }

    private String getDirectoryPathByShopId(final String shopId) {
        return storagePath + "/" + shopId.charAt(0) + "/" + shopId;
    }

    private void writeFiles(final ImageFiles imageFiles, final String directoryPath,
        final String shopId) throws IOException, NullPointerException {

        int fileOrder = FILE_ORDER_FIRST;
        for (MultipartFile imageFile : imageFiles.files()) {
            writeFile(directoryPath, shopId, imageFile, fileOrder);
            fileOrder++;
        }
    }

    private void writeFile(final String directoryPath, final String shopId,
        final MultipartFile imageFile, final int fileOrder) throws IOException {

        File fileToBeStored;
        if (fileOrder == FILE_ORDER_FIRST) {
            fileToBeStored = new File(directoryPath + "/" + shopId + "_thumbnail");
            createDataDirectoryIfNotExists(fileToBeStored);
        } else {
            fileToBeStored = new File(directoryPath + "/" + shopId + "_" + fileOrder);
        }
        FileCopyUtils.copy(imageFile.getInputStream(), new FileOutputStream(fileToBeStored));
    }

    private void createDataDirectoryIfNotExists(File fileToCreate) {
        File parentDirectory = fileToCreate.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists()) {
            parentDirectory.mkdirs();
        }
    }
}
