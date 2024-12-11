package com.beautify_project.bp_app_api.entity;

import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest;
import com.beautify_project.bp_app_api.entity.embedded.Address;
import com.beautify_project.bp_app_api.entity.embedded.BusinessTime;
import com.beautify_project.bp_app_api.utils.UUIDGenerator;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
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
    private Integer likes = 0;

    @Column(name = "shop_registered")
    private Long registered;

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

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private final List<ShopFacility> shopFacilities = new ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    private final List<ShopOperation> shopOperations = new ArrayList<>();

    @Builder
    private Shop(final String name, final String contact, final String url,
        final String introduction, final String rate,
        final Integer likes, final Long registered, final Long updated, final Address shopAddress,
        final BusinessTime businessTime) {
        this.id = UUIDGenerator.generate();
        this.name = name;
        this.contact = contact;
        this.url = url;
        this.introduction = introduction;
        this.rate = rate;
        this.likes = likes;
        this.registered = registered;
        this.updated = updated;
        this.shopAddress = shopAddress;
        this.businessTime = businessTime;
    }



    private static Shop of(final ShopRegistrationRequest request, long registeredTime) {
        return Shop.builder()
            .name(request.name())
            .contact(request.contact())
            .url(request.url())
            .registered(registeredTime)
            .updated(registeredTime)
            .introduction(request.introduction())
            .shopAddress(
                Address.builder()
                    .dongCode(request.address().dongCode())
                    .siDoName(request.address().siDoName())
                    .siGoonGooName(request.address().siGoonGooName())
                    .eubMyunDongName(request.address().eubMyunDongName())
                    .roadNameCode(request.address().roadNameCode())
                    .roadName(request.address().roadName())
                    .underGround(request.address().underGround())
                    .roadMainNum(request.address().roadMainNum())
                    .roadSubNum(request.address().roadSubNum())
                    .siGoonGooBuildingName(request.address().siGoonGooBuildingName())
                    .zipCode(request.address().zipCode())
                    .apartComplex(request.address().apartComplex())
                    .eubMyunDongSerialNumber(request.address().eubMyunDongSerialNumber())
                    .latitude(request.address().latitude())
                    .longitude(request.address().longitude())
                    .build()
            )
            .businessTime(
                BusinessTime.builder()
                    .openTime(request.businessTime().openTime())
                    .closeTime(request.businessTime().closeTime())
                    .breakBeginTime(request.businessTime().breakBeginTime())
                    .breakEndTime(request.businessTime().breakEndTime())
                    .offDayOfWeek(request.businessTime().offDayOfWeek())
                    .build()
            )
            .build();
    }

    public static Shop createShop(ShopRegistrationRequest registrationRequest,
        List<Operation> operations, List<Facility> facilities, Long registeredTime) {
        Shop newShop = of(registrationRequest, registeredTime);

        addAllImageFileIds(newShop, registrationRequest.imageFileIds());
        addAllOperationsToShopOperations(operations, newShop, registeredTime);
        addAllFacilitiesToShopFacilities(facilities, newShop, registeredTime);
        return newShop;
    }

    private static void addAllImageFileIds(final Shop shop, final List<String> imageFileIdsParam) {
        imageFileIdsParam.forEach(imageFileId -> shop.getImageFileIds().add(imageFileId));
    }

    private static void addAllOperationsToShopOperations(final List<Operation> operations, final Shop shop,
        final long registeredTime) {
        if (operations == null || operations.isEmpty()) {
            return;
        }
        for (Operation operation : operations) {
            shop.addOperations(ShopOperation.of(shop, operation, registeredTime));
        }
    }

    private static void addAllFacilitiesToShopFacilities(final List<Facility> facilities, final Shop shop,
        final long registeredTime) {
        if (facilities == null || facilities.isEmpty()) {
            return;
        }
        for (Facility facility : facilities) {
            shop.addSupportFacilities(ShopFacility.of(shop, facility, registeredTime));
        }
    }

    public void addSupportFacilities(ShopFacility shopFacility) {
        this.shopFacilities.add(shopFacility);
    }


    public void addOperations(ShopOperation shopOperation) {
        this.shopOperations.add(shopOperation);
    }

    @Override
    public boolean isNew() {
        return getObjectCreated() == null;
    }
}
