package com.ssafy.double_bean.common.model.repository;

import com.ssafy.double_bean.common.dto.ListRequestDto;
import com.ssafy.double_bean.common.dto.ListResponseDto;

import java.util.List;

public class RepositoryUtil {
    public static final String PAGING_QUERY = "LIMIT ${listRequest.size+2} OFFSET ${listRequest.size*(listRequest.page-1)}";

    private RepositoryUtil() {
    }

    public static ListResponseDto getRequestedPage(ListRequestDto listRequestDto, List retrieveResult) {
        boolean hasPreviousPage = listRequestDto.getPage() > 1 && retrieveResult.size() >= 1 + listRequestDto.getSize();
        boolean hasNextPage = retrieveResult.size() >= 2 + listRequestDto.getSize();

        List requestedContents = retrieveResult.subList(hasPreviousPage ? 1 : 0,
                Math.max(0,
                        Math.min(retrieveResult.size() - 1, hasNextPage ? listRequestDto.getSize() * listRequestDto.getPage() : retrieveResult.size() - 1))
        );
        return new ListResponseDto(listRequestDto.getPage(), listRequestDto.getSize(), hasNextPage, hasPreviousPage, requestedContents);
    }
}
