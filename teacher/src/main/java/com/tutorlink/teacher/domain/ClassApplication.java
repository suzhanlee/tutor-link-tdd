package com.tutorlink.teacher.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ClassApplication represents a student's application to a teaching class.
 * It is a snapshot of the class information at the time of application and
 * is responsible for checking the student's application result.
 * The class application and student are composed with ID references.
 */
public record ClassApplication(
        Long id,
        Long studentId,
        Long teachingClassId,
        String classTitle,
        String classDescription,
        int classPrice,
        LocalDateTime classRegisteredAt,
        LocalDateTime classRecruitmentStartAt,
        LocalDateTime classRecruitmentEndAt,
        LocalDateTime appliedAt
) {
    public ClassApplication {
        Objects.requireNonNull(studentId, "학생 ID는 필수입니다.");
        Objects.requireNonNull(teachingClassId, "수업 ID는 필수입니다.");
        Objects.requireNonNull(classTitle, "수업 제목은 필수입니다.");
        Objects.requireNonNull(classDescription, "수업 설명은 필수입니다.");
        Objects.requireNonNull(classRegisteredAt, "수업 등록 시간은 필수입니다.");
        Objects.requireNonNull(classRecruitmentStartAt, "수업 모집 시작 시간은 필수입니다.");
        Objects.requireNonNull(classRecruitmentEndAt, "수업 모집 종료 시간은 필수입니다.");
        Objects.requireNonNull(appliedAt, "신청 시간은 필수입니다.");
        
        if (classPrice < 0) {
            throw new IllegalArgumentException("수업 가격은 0 이상이어야 합니다.");
        }
    }
    
    /**
     * Creates a ClassApplication from a TeachingClass and a student ID.
     * This factory method ensures that the ClassApplication is a snapshot of the class information.
     */
    public static ClassApplication createFromTeachingClass(TeachingClass teachingClass, Long studentId) {
        return new ClassApplication(
                null,
                studentId,
                teachingClass.id(),
                teachingClass.title(),
                teachingClass.description(),
                teachingClass.price(),
                teachingClass.registeredAt(),
                teachingClass.recruitmentStartAt(),
                teachingClass.recruitmentEndAt(),
                LocalDateTime.now()
        );
    }
    
    /**
     * Checks if the application is valid based on the recruitment status at the time of application.
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        
        // Application is valid if it was made during the recruitment period
        return !now.isBefore(classRecruitmentStartAt) && !now.isAfter(classRecruitmentEndAt);
    }
}