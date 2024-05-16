package com.ssafy.double_bean.attraction.service;

import com.ssafy.double_bean.attraction.constants.SubAreaNames;
import com.ssafy.double_bean.attraction.util.AreaCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AreaCodeServiceImpl implements AreaCodeService {
    private static final String SUB_AREA_API_URL = "https://apis.data.go.kr/B551011/KorService1/areaCode1?serviceKey={serviceKey}&areaCode={areaCode}&numOfRows=10000&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=json";
    @Value("${openapi.service-key}")
    private String openApiServiceKey;

    @Override
    public List<String> getSubAreaNames(int areaCode) {
        String[] arrayResult = SubAreaNames.getSubAreNamesByAreaCode(areaCode);
        List<String> result = new ArrayList<>();
        Collections.addAll(result, arrayResult);
        return result;
    }

    @Override
    public List<String> getSubAreaNames(String areaName) {
        return getSubAreaNames(AreaCode.getByName(areaName).getCode());
    }
}
