package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.shop.ImageFiles;
import com.beautify_project.bp_app_api.exception.FileLoadException;
import com.beautify_project.bp_app_api.exception.FileStoreException;

public interface StorageService {

    void storeImageFiles(final ImageFiles imageFiles, final String shopId)
        throws FileStoreException;

    ImageFiles loadImageFiles(final String shopId) throws FileLoadException;

    byte[] loadThumbnail(final String shopId) throws FileLoadException;
}
