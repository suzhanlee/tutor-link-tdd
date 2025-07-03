package com.tutorlink.teacher.dto;

/**
 * A request for a page of data.
 */
public record PageRequest(
        int page,
        int size
) {
    /**
     * Creates a new PageRequest with the given page and size.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return a new PageRequest
     */
    public static PageRequest of(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Page size must not be less than one");
        }
        return new PageRequest(page, size);
    }

    /**
     * Creates a new PageRequest with the first page and the given size.
     *
     * @param size the page size
     * @return a new PageRequest
     */
    public static PageRequest ofSize(int size) {
        return of(0, size);
    }
}