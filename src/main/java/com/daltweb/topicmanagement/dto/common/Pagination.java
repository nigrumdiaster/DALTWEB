package com.daltweb.topicmanagement.dto.common;

import com.daltweb.topicmanagement.exception.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record Pagination(int page, int size, long totalElements, int totalPages, boolean hasNext) {
    public static final int DEFAULT_SIZE = 10;
    public static final int MAX_SIZE = 200;

    public static Pagination from(Page<?> page) {
        return new Pagination(page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.hasNext());
    }

    public static Pageable request(int page, int limit) {
        if (page < 0 || (limit != -1 && (limit < 1 || limit > MAX_SIZE))) {
            throw new BadRequestException("page phải từ 0 trở lên; limit phải là -1 hoặc trong khoảng 1–200!");
        }
        // Every page has a unique, deterministic order, including equal timestamps/names.
        if (limit == -1) {
            if (page != 0) throw new BadRequestException("limit=-1 chỉ dùng với page=0 hoặc không truyền page!");
            return Pageable.unpaged(Sort.by("id").ascending());
        }
        return PageRequest.of(page, limit, Sort.by("id").ascending());
    }
}
