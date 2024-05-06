package com.ssafy.double_bean.attraction.dto;

import com.ssafy.double_bean.attraction.model.entity.AttractionEntity;
import com.ssafy.double_bean.attraction.util.AreaCode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;

@Schema(description = "관광지 데이터 응답 DTO")
public class AttractionResponseDto {
    @Schema(description = "고유 식별자")
    int id;
    @Schema(description = "장소 이름")
    String title;
    @Schema(description = "주소")
    String address;
    @Schema(description = "지역명")
    String area;
    @Schema(description = "분류")
    String category;
    @Schema(description = "관광지 유형")
    String type;
    @Schema(description = "이미지 url 1")
    URI firstImageUrl;
    @Schema(description = "이미지 url 2")
    URI secondImageUrl;
    @Schema(description = "위도")
    double latitude;
    @Schema(description = "경도")
    double longitude;
    @Schema(description = "시/군/구 정보")
    String sigungu;
    @Schema(description = "연락처")
    String telephoneNumber;

    public AttractionResponseDto(
            int id,
            String title,
            String address,
            String area,
            String category,
            String type,
            URI firstImageUrl,
            URI secondImageUrl,
            double latitude,
            double longitude,
            String sigungu,
            String telephoneNumber) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.area = area;
        this.category = category;
        this.type = type;
        this.firstImageUrl = firstImageUrl;
        this.secondImageUrl = secondImageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sigungu = sigungu;
        this.telephoneNumber = telephoneNumber;
    }

    public AttractionResponseDto() {
    }

    public AttractionResponseDto(AttractionEntity entity) {
        this(entity.getId(), entity.getTitle(), entity.getAddress() + entity.getDetailAddress(),
                AreaCode.getByCode(entity.getAreaCode()).getName(), entity.getCategory3(), entity.getContentTypeId(),
                entity.getFirstImageUrl(), entity.getSecondImageUrl(), entity.getLatitude(), entity.getLongitude(),
                entity.getSigunguCode(), entity.getTelephoneNumber());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public URI getFirstImageUrl() {
        return firstImageUrl;
    }

    public void setFirstImageUrl(URI firstImageUrl) {
        this.firstImageUrl = firstImageUrl;
    }

    public URI getSecondImageUrl() {
        return secondImageUrl;
    }

    public void setSecondImageUrl(URI secondImageUrl) {
        this.secondImageUrl = secondImageUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getSigungu() {
        return sigungu;
    }

    public void setSigungu(String sigungu) {
        this.sigungu = sigungu;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }
}
