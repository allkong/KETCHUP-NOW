package com.ssafy.double_bean.attraction.util;

import java.util.Arrays;
import java.util.List;

public enum AreaCode {
    SEOUL("서울", 1),
    INCHEON("인천", 2),
    DAEJEON("대전", 3),
    DAEGU("대구", 4),
    GWANGJU("광주", 5),
    BUSAN("부산", 6),
    ULSAN("울산", 7),
    SEJONG("세종", 8),
    GYEONGGI("경기", 31),
    GANGWON("강원", 32),
    CHUNGBUK("충북", 33),
    CHUNGNAM("충남", 34),
    GYEONGBUK("경북", 35),
    GYEONGNAM("경남", 36),
    JEONBUK("전북", 37),
    JEONNAM("전남", 38),
    JEJU("제주", 39);

    private final String name;
    private final int code;

    AreaCode(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static AreaCode getByCode(int code) {
        return getAllAreaCodes().stream().filter(areaCode -> areaCode.code == code)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown area code."));
    }

    public static List<AreaCode> getAllAreaCodes() {
        return Arrays.asList(AreaCode.values());
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
