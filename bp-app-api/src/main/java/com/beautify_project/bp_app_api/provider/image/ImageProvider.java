package com.beautify_project.bp_app_api.provider.image;

import com.beautify_project.bp_app_api.response.image.PreSignedGetUrlResult;
import com.beautify_project.bp_app_api.response.image.PreSignedPutUrlResult;
import java.util.List;

public interface ImageProvider {

    PreSignedPutUrlResult providePreSignedPutUrl();

    PreSignedGetUrlResult providePreSignedGetUrlByFileId(final String fileId);

    List<PreSignedGetUrlResult> provideAllPreSignedGetUrlsByFileId(final List<String> fileIds);
}
