package com.beautify_project.bp_kafka_event_consumer.openfeign;

import com.beautify_project.bp_kafka_event_consumer.event.ShopLikeCancelEvent;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

// TODO: 외부 설정 파일화
@FeignClient(name = "shop-like-cancel-api", url = "http://127.0.0.1:8080")
public interface ShopLikeCancelApi {

    @DeleteMapping("/v1/shops/likes")
    void batchShopLikeCancel(@RequestBody final List<ShopLikeCancelEvent> shopLikeCancelEvents);

}
