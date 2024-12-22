package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
import com.beautify_project.bp_app_api.dto.image.PreSignedGetUrlResult;
import com.beautify_project.bp_app_api.repository.image.ImageRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public ResponseMessage issuePreSignedPutUrlWrappingResponseMessage() {

        return ResponseMessage.createResponseMessage(imageRepository.createPutUrlResult());
    }

    public ResponseMessage issuePreSignedGetUrlWrappingResponseMessage(final String fileId) {
        return ResponseMessage.createResponseMessage(imageRepository.findImageLinkByFileId(fileId));
    }

    public String issuePreSignedGetUrl(final String fileId) {
        PreSignedGetUrlResult preSignedGetUrlResult = imageRepository.findImageLinkByFileId(fileId);
        return preSignedGetUrlResult.preSignedUrl();
    }

    public List<String> issuePreSignedGetUrls(final List<String> fileIds) {
        final List<PreSignedGetUrlResult> preSignedGetUrlResults = imageRepository.findAllImageLinksByFileIds(
            fileIds);

        return preSignedGetUrlResults.stream().map(PreSignedGetUrlResult::preSignedUrl)
            .collect(Collectors.toList());
    }
}
