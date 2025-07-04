package com.tutorlink.teacher.domain;

/**
 * Represents the recruitment status of a teaching class.
 */
public enum RecruitmentStatus {
    /**
     * The class is currently recruiting students.
     */
    RECRUITING,
    
    /**
     * The recruitment period for the class has ended.
     */
    CLOSED,
    
    /**
     * The recruitment period for the class has not yet started.
     */
    NOT_YET_OPENED
}