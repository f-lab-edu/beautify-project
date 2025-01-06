package com.beautify_project.bp_kafka_event_consumer.openfeign;

import com.beautify_project.bp_kafka_event_consumer.event.ShopLikeEvent;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient(name = "shop-like-api", url = "http://127.0.0.1:8080")
public interface ShopLikeApi {

    @PostMapping("/v1/shops/likes")
    @ResponseStatus(HttpStatus.OK)
    void batchLikeShops(@RequestBody List<ShopLikeEvent> shopLikeEvents);
}
