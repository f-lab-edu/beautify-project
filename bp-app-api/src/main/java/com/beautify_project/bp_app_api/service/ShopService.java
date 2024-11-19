package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_dto.common.response.ResponseMessage;
import com.beautify_project.bp_dto.shop.ImageFiles;
import com.beautify_project.bp_dto.shop.ShopFindListRequestParameters;
import com.beautify_project.bp_dto.shop.ShopRegistrationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShopService {

    public ResponseMessage registerShop(final ImageFiles images,
        final ShopRegistrationRequest shopRegistrationRequest) {
        return null;
    }

    public ResponseMessage findShopList(final ShopFindListRequestParameters parameters) {
        return null;
    }
}
