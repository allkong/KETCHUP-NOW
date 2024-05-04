package com.ssafy.double_bean.attraction.model.entity;

import java.net.URI;

public class AttractionEntity {
    private int id;
    // 장소 이름
    private String title;
    // 주소
    private String address;
    // 상세 주소
    private String detailAddress;
    // 지역 코드
    private int areaCode;
    // 분류 1
    private String category1;
    // 분류 2
    private String category2;
    // 분류 3
    private String category3;
    // 관광지 유형 id
    private String contentTypeId;
    // 이미지 url 1
    private URI firstImageUrl;
    // 이미지 url 2
    private URI secondImageUrl;
    // 위도
    private float latitude;
    // 경도
    private float longitude;
    // 시-군-구 코드
    private String sigunguCode;
    // 연락처
    private String telephoneNumber;

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

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public int getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(int areaCode) {
        this.areaCode = areaCode;
    }

    public String getCategory1() {
        return category1;
    }

    public void setCategory1(String category1) {
        this.category1 = category1;
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }

    public String getCategory3() {
        return category3;
    }

    public void setCategory3(String category3) {
        this.category3 = category3;
    }

    public String getContentTypeId() {
        return contentTypeId;
    }

    public void setContentTypeId(String contentTypeId) {
        this.contentTypeId = contentTypeId;
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

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getSigunguCode() {
        return sigunguCode;
    }

    public void setSigunguCode(String sigunguCode) {
        this.sigunguCode = sigunguCode;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    @Override
    public String toString() {
        return "AttractionEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", detailAddress='" + detailAddress + '\'' +
                ", areaCode=" + areaCode +
                ", category1='" + category1 + '\'' +
                ", category2='" + category2 + '\'' +
                ", category3='" + category3 + '\'' +
                ", contentTypeId='" + contentTypeId + '\'' +
                ", firstImageUrl=" + firstImageUrl +
                ", secondImageUrl=" + secondImageUrl +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", sigunguCode='" + sigunguCode + '\'' +
                ", telephoneNumber='" + telephoneNumber + '\'' +
                '}';
    }
}
