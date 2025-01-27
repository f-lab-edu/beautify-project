package com.bp.domain.mysql.entity;

import com.bp.domain.mysql.entity.embedded.Address;
import com.bp.domain.mysql.entity.embedded.BusinessTime;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shop")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shop extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    @Column(name = "shop_name")
    private String name;

    @Column(name = "shop_contact")
    private String contact;

    @Column(name = "shop_url")
    private String url;

    @Column(name = "shop_introduction")
    private String introduction;

    @Column(name = "shop_rate")
    private String rate = "0.0";

    @Column(name = "shop_likes")
    private Long likes = 0L;

    @Column(name = "shop_image_file_ids")
    private List<String> imageFileIds = new ArrayList<>();

    @Embedded
    @AttributeOverride(name = "dongCode", column = @Column(name = "shop_dong_code"))
    @AttributeOverride(name = "siDoName", column = @Column(name = "shop_si_do_name"))
    @AttributeOverride(name = "siGoonGooName", column = @Column(name = "shop_si_goon_goo_name"))
    @AttributeOverride(name = "eubMyunDongName", column = @Column(name = "shop_eub_myun_dong_name"))
    @AttributeOverride(name = "roadNameCode", column = @Column(name = "shop_road_name_code"))
    @AttributeOverride(name = "roadName", column = @Column(name = "shop_road_name"))
    @AttributeOverride(name = "underGround", column = @Column(name = "shop_under_ground"))
    @AttributeOverride(name = "roadMainNum", column = @Column(name = "shop_road_main_num"))
    @AttributeOverride(name = "roadSubNum", column = @Column(name = "shop_road_sub_num"))
    @AttributeOverride(name = "siGoonGooBuildingName", column = @Column(name = "shop_si_goon_goo_building_name"))
    @AttributeOverride(name = "zipCode", column = @Column(name = "shop_zip_code"))
    @AttributeOverride(name = "apartComplex", column = @Column(name = "shop_apart_complex"))
    @AttributeOverride(name = "eubMyunDongSerialNumber", column = @Column(name = "shop_eub_myun_dong_serial_number"))
    @AttributeOverride(name = "latitude", column = @Column(name = "shop_latitude"))
    @AttributeOverride(name = "longitude", column = @Column(name = "shop_longitude"))
    private Address shopAddress;

    @Embedded
    @AttributeOverride(name = "openTime", column = @Column(name = "shop_open_time"))
    @AttributeOverride(name = "closeTime", column = @Column(name = "shop_close_time"))
    @AttributeOverride(name = "breakBeginTime", column = @Column(name = "shop_break_begin_time"))
    @AttributeOverride(name = "breakEndTime", column = @Column(name = "shop_break_end_time"))
    @AttributeOverride(name = "offDayOfWeek", column = @Column(name = "shop_off_day_of_week"))
    private BusinessTime businessTime;

    private Shop(final String name, final String contact, final String url,
        final String introduction, final String rate, final Long likes,
        final List<String> imageFileIds, final Address shopAddress,
        final BusinessTime businessTime) {
        this.name = name;
        this.contact = contact;
        this.url = url;
        this.introduction = introduction;
        this.rate = rate;
        this.likes = likes;
        this.imageFileIds = imageFileIds;
        this.shopAddress = shopAddress;
        this.businessTime = businessTime;
    }

    public static Shop newShop(final String name, final String contact, final String url,
        final String introduction, final List<String> imageFileIds, Address address,
        BusinessTime businessTime) {
        return new Shop(name, contact, url, introduction, "0.0", 0L, imageFileIds, address,
            businessTime);
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
        return "Shop{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", contact='" + contact + '\'' +
            ", url='" + url + '\'' +
            ", introduction='" + introduction + '\'' +
            ", rate='" + rate + '\'' +
            ", likes=" + likes +
            ", imageFileIds=" + imageFileIds +
            ", shopAddress=" + shopAddress +
            ", businessTime=" + businessTime +
            '}';
    }
}
