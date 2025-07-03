package com.tutorlink.teacher.dto;

/**
 * Condition object for searching teaching classes
 */
public record ClassSearchCondition(
        String titleKeyword,
        SortType sortType,
        PageRequest pageRequest
) {
    /**
     * Creates a new ClassSearchCondition with default values
     *
     * @return a new ClassSearchCondition with null titleKeyword, null sortType, and null pageRequest
     */
    public static ClassSearchCondition empty() {
        return new ClassSearchCondition(null, null, null);
    }

    /**
     * Creates a new ClassSearchCondition with the given titleKeyword and null sortType and pageRequest
     *
     * @param titleKeyword the keyword to search for in class titles
     * @return a new ClassSearchCondition with the given titleKeyword and null sortType and pageRequest
     */
    public static ClassSearchCondition withTitleKeyword(String titleKeyword) {
        return new ClassSearchCondition(titleKeyword, null, null);
    }

    /**
     * Creates a new ClassSearchCondition with the given sortType and null titleKeyword and pageRequest
     *
     * @param sortType the type of sorting to apply
     * @return a new ClassSearchCondition with the given sortType and null titleKeyword and pageRequest
     */
    public static ClassSearchCondition withSortType(SortType sortType) {
        return new ClassSearchCondition(null, sortType, null);
    }

    /**
     * Creates a new ClassSearchCondition with the given pageRequest and null titleKeyword and sortType
     *
     * @param pageRequest the pagination request
     * @return a new ClassSearchCondition with the given pageRequest and null titleKeyword and sortType
     */
    public static ClassSearchCondition withPageRequest(PageRequest pageRequest) {
        return new ClassSearchCondition(null, null, pageRequest);
    }

    /**
     * Creates a new ClassSearchCondition with the given titleKeyword, sortType, and pageRequest
     *
     * @param titleKeyword the keyword to search for in class titles
     * @param sortType     the type of sorting to apply
     * @param pageRequest  the pagination request
     * @return a new ClassSearchCondition with the given titleKeyword, sortType, and pageRequest
     */
    public static ClassSearchCondition of(String titleKeyword, SortType sortType, PageRequest pageRequest) {
        return new ClassSearchCondition(titleKeyword, sortType, pageRequest);
    }

    /**
     * Creates a new ClassSearchCondition with the given titleKeyword and sortType and null pageRequest
     *
     * @param titleKeyword the keyword to search for in class titles
     * @param sortType     the type of sorting to apply
     * @return a new ClassSearchCondition with the given titleKeyword and sortType and null pageRequest
     */
    public static ClassSearchCondition of(String titleKeyword, SortType sortType) {
        return new ClassSearchCondition(titleKeyword, sortType, null);
    }
}
