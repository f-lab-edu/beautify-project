package com.beautify_project.bp_app_api.entity;

import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest;
import com.beautify_project.bp_app_api.entity.embedded.Address;
import com.beautify_project.bp_app_api.entity.embedded.BusinessTime;
import com.beautify_project.bp_app_api.utils.UUIDGenerator;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "shop")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shop implements Persistable<String> {

    @Id
    @Column(name = "shop_id")
    private String id;

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

    @Column(name = "shop_registered_time")
    private Long registeredTime;

    @Column(name = "shop_updated")
    private Long updated;

    @Column(name = "shop_image_file_ids")
    private final List<String> imageFileIds = new ArrayList<>();

    @Transient
    private Long objectCreated;

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

    private Shop(final String id, final String name, final String contact, final String url,
        final String introduction, final String rate, final Long likes, final Long registeredTime,
        final Long updated, final Address shopAddress, final BusinessTime businessTime) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.url = url;
        this.introduction = introduction;
        this.rate = rate;
        this.likes = likes;
        this.registeredTime = registeredTime;
        this.updated = updated;
        this.shopAddress = shopAddress;
        this.businessTime = businessTime;
    }

    public static Shop from(final ShopRegistrationRequest registrationRequest) {
        return new Shop(
            UUIDGenerator.generate(),
            registrationRequest.name(),
            registrationRequest.contact(),
            registrationRequest.url(),
            registrationRequest.introduction(),
            "0.0", // rate
            0L, // likes
            System.currentTimeMillis(), // registeredTime
            System.currentTimeMillis(), // updated
            Address.builder()
                .dongCode(registrationRequest.address().dongCode())
                .siDoName(registrationRequest.address().siDoName())
                .siGoonGooName(registrationRequest.address().siGoonGooName())
                .eubMyunDongName(registrationRequest.address().eubMyunDongName())
                .roadNameCode(registrationRequest.address().roadNameCode())
                .roadName(registrationRequest.address().roadName())
                .underGround(registrationRequest.address().underGround())
                .roadMainNum(registrationRequest.address().roadMainNum())
                .roadSubNum(registrationRequest.address().roadSubNum())
                .siGoonGooBuildingName(registrationRequest.address().siGoonGooBuildingName())
                .zipCode(registrationRequest.address().zipCode())
                .apartComplex(registrationRequest.address().apartComplex())
                .eubMyunDongSerialNumber(registrationRequest.address().eubMyunDongSerialNumber())
                .latitude(registrationRequest.address().latitude())
                .longitude(registrationRequest.address().longitude())
                .build(),
            BusinessTime.builder()
                .openTime(registrationRequest.businessTime().openTime())
                .closeTime(registrationRequest.businessTime().closeTime())
                .breakBeginTime(registrationRequest.businessTime().breakBeginTime())
                .breakEndTime(registrationRequest.businessTime().breakEndTime())
                .offDayOfWeek(registrationRequest.businessTime().offDayOfWeek()).build()
        );
    }

    public void increaseLikeCount() {
        likes += 1;
    }

    public void decreaseLikeCount() {
        likes -= 1;
    }

    @Override
    public String toString() {
        return "Shop{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", contact='" + contact + '\'' +
            ", url='" + url + '\'' +
            ", introduction='" + introduction + '\'' +
            ", rate='" + rate + '\'' +
            ", likes=" + likes +
            ", registeredTime=" + registeredTime +
            ", updated=" + updated +
            ", shopAddress=" + shopAddress +
            ", businessTime=" + businessTime +
            '}';
    }

    @Override
    public boolean isNew() {
        return getObjectCreated() == null;
    }
}
