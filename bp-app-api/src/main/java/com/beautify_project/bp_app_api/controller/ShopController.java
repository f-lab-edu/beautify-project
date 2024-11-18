package com.beautify_project.bp_app_api.controller;

import com.beautify_project.bp_app_api.records.ImageFiles;
import com.beautify_project.bp_app_api.service.ShopService;
import com.beautify_project.bp_dto.shop.ShopRegistrationRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @PostMapping("/v1/shops")
    ResponseEntity<?> registerShop(
        @RequestPart(value = "images", required = false) final List<MultipartFile> imageFiles,
        @Valid @RequestPart(value = "shopRegistrationInfo") final ShopRegistrationRequest shopRegistrationRequest) {

        return new ResponseEntity<>(
            shopService.registerShop(new ImageFiles(imageFiles), shopRegistrationRequest),
            HttpStatus.OK);
    }
}
