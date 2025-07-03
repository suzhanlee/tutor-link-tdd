package com.tutorlink.teacher.dto;

/**
 * Condition object for searching teaching classes
 */
public record ClassSearchCondition(
        String titleKeyword,
        SortType sortType
) {
    /**
     * Creates a new ClassSearchCondition with default values
     * @return a new ClassSearchCondition with null titleKeyword and null sortType
     */
    public static ClassSearchCondition empty() {
        return new ClassSearchCondition(null, null);
    }
    
    /**
     * Creates a new ClassSearchCondition with the given titleKeyword and null sortType
     * @param titleKeyword the keyword to search for in class titles
     * @return a new ClassSearchCondition with the given titleKeyword and null sortType
     */
    public static ClassSearchCondition withTitleKeyword(String titleKeyword) {
        return new ClassSearchCondition(titleKeyword, null);
    }
    
    /**
     * Creates a new ClassSearchCondition with the given sortType and null titleKeyword
     * @param sortType the type of sorting to apply
     * @return a new ClassSearchCondition with the given sortType and null titleKeyword
     */
    public static ClassSearchCondition withSortType(SortType sortType) {
        return new ClassSearchCondition(null, sortType);
    }
    
    /**
     * Creates a new ClassSearchCondition with the given titleKeyword and sortType
     * @param titleKeyword the keyword to search for in class titles
     * @param sortType the type of sorting to apply
     * @return a new ClassSearchCondition with the given titleKeyword and sortType
     */
    public static ClassSearchCondition of(String titleKeyword, SortType sortType) {
        return new ClassSearchCondition(titleKeyword, sortType);
    }
}