package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.shop.ImageFiles;
import com.beautify_project.bp_app_api.exception.FileLoadException;
import com.beautify_project.bp_app_api.exception.FileStoreException;

public class S3StorageService implements StorageService {

    @Override
    public void storeImageFiles(final ImageFiles imageFiles, final String shopId)
        throws FileStoreException {

    }

    @Override
    public ImageFiles loadImageFiles(final String filePath) throws FileLoadException {
        return null;
    }

    @Override
    public byte[] loadThumbnail(final String shopId) throws FileLoadException {
        return new byte[0];
    }
}
