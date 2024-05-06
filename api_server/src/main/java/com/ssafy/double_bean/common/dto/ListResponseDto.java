package com.ssafy.double_bean.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "목록 조회 DTO")
public class ListResponseDto<E> {
    @Schema(description = "조회를 요청한 페이지 번호")
    private int page;
    @Schema(description = "조회된 객체의 개수")
    private int size;
    @Schema(description = "다음 페이지의 존재 여부")
    private boolean hasNext;
    @Schema(description = "이전 페이지의 존재 여부")
    private boolean hasPrev;
    @Schema(description = "조회한 데이터 목록")
    private List<E> data;

    public ListResponseDto(ListResponseDto responseDto, List<E> data) {
        this(responseDto.getPage(), responseDto.getSize(), responseDto.hasNext, responseDto.hasPrev, data);
    }

    public ListResponseDto(int page, int size, boolean hasNext, boolean hasPrev, List<E> data) {
        this.page = page;
        this.size = size;
        this.hasNext = hasNext;
        this.hasPrev = hasPrev;
        this.data = data;
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

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrev() {
        return hasPrev;
    }

    public void setHasPrev(boolean hasPrev) {
        this.hasPrev = hasPrev;
    }

    public List<E> getData() {
        return data;
    }

    public void setData(List<E> data) {
        this.data = data;
    }
}
