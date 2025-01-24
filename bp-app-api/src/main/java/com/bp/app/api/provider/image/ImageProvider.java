package com.bp.app.api.provider.image;

import com.bp.app.api.response.image.PreSignedGetUrlResult;
import com.bp.app.api.response.image.PreSignedPutUrlResult;
import java.util.List;

public interface ImageProvider {

    PreSignedPutUrlResult providePreSignedPutUrl();

    PreSignedGetUrlResult providePreSignedGetUrlByFileId(final String fileId);

    List<PreSignedGetUrlResult> provideAllPreSignedGetUrlsByFileId(final List<String> fileIds);
}
