package com.beautify_project.bp_mysql.entity.adapter;

import com.beautify_project.bp_mysql.entity.Shop;
import com.beautify_project.bp_mysql.entity.embedded.Address;
import com.beautify_project.bp_mysql.entity.embedded.BusinessTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class ShopAdapter {

    private Long id;
    private String name;
    private String contact;
    private String url;
    private String introduction;
    private String rate;
    private Long likes;
    private Long registeredTime;
    private Long updated;
    private final List<String> imageFileIds = new ArrayList<>();
    private Address shopAddress;
    private BusinessTime businessTime;

    private ShopAdapter(final Long id, final String name, final String contact, final String url,
        final String introduction, final String rate, final Long likes, final Long registeredTime,
        final Long updated, final List<String> imageFileIds, final Address shopAddress, final BusinessTime businessTime) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.url = url;
        this.introduction = introduction;
        this.rate = rate;
        this.likes = likes;
        this.registeredTime = registeredTime;
        this.updated = updated;
        this.imageFileIds.addAll(imageFileIds);
        this.shopAddress = shopAddress;
        this.businessTime = businessTime;
    }

    public static ShopAdapter toAdapter(final Shop entity) {
        return new ShopAdapter(entity.getId(), entity.getName(), entity.getContact(),
            entity.getUrl(), entity.getIntroduction(), entity.getRate(), entity.getLikes(),
            entity.getRegisteredTime(), entity.getUpdated(), entity.getImageFileIds(),
            entity.getShopAddress(), entity.getBusinessTime());
    }

    public void increaseLikeCount(final int countToIncrease) {
        likes += countToIncrease;
    }

    public void decreaseLikeCount(final int countToDecrease) {
        likes -= countToDecrease;
        if (likes < 0) {
            likes = 0L;
        }
    }

    @Override
    public String toString() {
        return "ShopAdapter{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", contact='" + contact + '\'' +
            ", url='" + url + '\'' +
            ", introduction='" + introduction + '\'' +
            ", rate='" + rate + '\'' +
            ", likes=" + likes +
            ", registeredTime=" + registeredTime +
            ", updated=" + updated +
            ", imageFileIds=" + imageFileIds +
            ", shopAddress=" + shopAddress +
            ", businessTime=" + businessTime +
            '}';
    }
}
