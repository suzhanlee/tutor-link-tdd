package com.tutorlink.teacher.domain;

import java.time.LocalDateTime;

public record TeachingClass(
        Long id,
        Long teacherId,
        String title,
        String description,
        int price,
        LocalDateTime registeredAt,
        LocalDateTime recruitmentStartAt,
        LocalDateTime recruitmentEndAt
) {
    /**
     * Returns the current recruitment status of the teaching class.
     * 
     * @return RecruitmentStatus - RECRUITING, CLOSED, or NOT_YET_OPENED
     */
    public RecruitmentStatus getRecruitmentStatus() {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(recruitmentStartAt)) {
            return RecruitmentStatus.NOT_YET_OPENED;
        } else if (now.isAfter(recruitmentEndAt)) {
            return RecruitmentStatus.CLOSED;
        } else {
            return RecruitmentStatus.RECRUITING;
        }
    }
}
