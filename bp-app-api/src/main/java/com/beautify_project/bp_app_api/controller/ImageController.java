package com.beautify_project.bp_app_api.controller;

import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
import com.beautify_project.bp_app_api.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    /**
     * Shop 이미지 등록을 위한 preSignedUrl 발급
     */
    @GetMapping("/v1/images/presigned-put-url")
    @ResponseStatus(code = HttpStatus.OK)
    ResponseMessage issuePreSignedPutUrl() {
        return imageService.issuePreSignedPutUrl();
    }
}
