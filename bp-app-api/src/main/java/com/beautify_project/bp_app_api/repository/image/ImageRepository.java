package com.beautify_project.bp_app_api.repository.image;

import com.beautify_project.bp_app_api.dto.image.PreSignedGetUrlResult;
import com.beautify_project.bp_app_api.dto.image.PreSignedPutUrlResult;
import java.util.List;

public interface ImageRepository {

    PreSignedPutUrlResult createPutUrlResult();

    PreSignedGetUrlResult findImageLinkByFileId(final String fileId);

    List<PreSignedGetUrlResult> findAllImageLinksByFileIds(final List<String> fileId);
}
