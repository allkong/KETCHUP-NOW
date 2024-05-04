package com.ssafy.double_bean.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

@Schema(description = "List pagination 요청 DTO")
public class ListRequestDto {
    @Schema(description = "조회할 페이지", example = "1", defaultValue = "1")
    @Min(value = 1, message = "page must bigger than 1")
    private int page;
    @Schema(description = "한 페이지에서 조회할 객체의 수", example = "1", defaultValue = "50")
    @Min(value = 1, message = "size must bigger than 1")
    private int size;
    @Schema(description = "정렬 기준", example = "createdAt")
    private String orderKey;
    @Schema(description = "정렬 방향", example = "ASC", defaultValue = "ASC")
    private OrderDirection orderDirection;

    public ListRequestDto() {
        page = 1;
        size = 50;
        orderKey = null;
        orderDirection = OrderDirection.ASC;
    }

    public ListRequestDto(int page, int size, String orderKey, OrderDirection orderDirection) {
        this.page = page;
        this.size = size;
        this.orderKey = orderKey;
        this.orderDirection = orderDirection;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getOrderKey() {
        return orderKey;
    }

    public void setOrderKey(String orderKey) {
        this.orderKey = orderKey;
    }

    public OrderDirection getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(OrderDirection orderDirection) {
        this.orderDirection = orderDirection;
    }

    @Override
    public String toString() {
        return "ListRequestDto{" +
                "page=" + page +
                ", size=" + size +
                ", orderKey='" + orderKey + '\'' +
                ", orderDirection=" + orderDirection +
                '}';
    }

    public enum OrderDirection {
        ASC, DESC
    }
}
