package com.tutorlink.teacher.dto;

import java.util.List;

/**
 * A generic pagination response containing a list of items and pagination metadata.
 *
 * @param <T> the type of items in the page
 */
public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {
    /**
     * Creates a new PageResponse with the given content and pagination metadata.
     *
     * @param content       the content of the page
     * @param pageNumber    the current page number (0-based)
     * @param pageSize      the size of the page
     * @param totalElements the total number of elements across all pages
     * @param totalPages    the total number of pages
     * @param <T>           the type of items in the page
     * @return a new PageResponse
     */
    public static <T> PageResponse<T> of(List<T> content, int pageNumber, int pageSize, long totalElements, int totalPages) {
        return new PageResponse<>(content, pageNumber, pageSize, totalElements, totalPages);
    }
}
