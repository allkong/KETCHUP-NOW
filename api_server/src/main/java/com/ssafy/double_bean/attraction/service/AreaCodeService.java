package com.ssafy.double_bean.attraction.service;

import java.util.List;

public interface AreaCodeService {
    List<String> getSubAreaNames(int areaCode);

    List<String> getSubAreaNames(String areaName);
}
