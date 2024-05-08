package com.ssafy.double_bean.attraction.service;

import com.ssafy.double_bean.attraction.util.AreaCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class AreaCodeServiceImpl implements AreaCodeService {
    private static final String SUB_AREA_API_URL = "https://apis.data.go.kr/B551011/KorService1/areaCode1?serviceKey={serviceKey}&areaCode={areaCode}&numOfRows=10000&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=json";
    @Value("${openapi.service-key}")
    private String openApiServiceKey;

    @Override
    public List<String> getSubAreaNames(int areaCode) {
        RestClient restClient = RestClient.create();

        ResponseEntity<String> response = restClient
                .get()
                .uri(SUB_AREA_API_URL, openApiServiceKey, areaCode)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class);

        JsonParser parser = new GsonJsonParser();
        List<Map<String, Object>> items = (List<Map<String, Object>>) ((Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) parser.parseMap(response.getBody()).get("response")).get("body")).get("items")).get("item");
        return items.stream().map(obj -> (String) obj.get("name")).toList();
    }

    @Override
    public List<String> getSubAreaNames(String areaName) {
        return getSubAreaNames(AreaCode.getByName(areaName).getCode());
    }
}
