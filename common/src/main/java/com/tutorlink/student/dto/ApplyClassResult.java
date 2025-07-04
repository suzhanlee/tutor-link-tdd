package com.tutorlink.student.dto;

import java.time.LocalDateTime;

/**
 * Result of applying for a class.
 */
public record ApplyClassResult(
    Long applicationId,
    Long studentId,
    Long classId,
    String classTitle,
    String classDescription,
    int classPrice,
    LocalDateTime appliedAt
) {
    public ApplyClassResult {
        if (applicationId == null) {
            throw new IllegalArgumentException("Application ID cannot be null");
        }
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }
        if (classId == null) {
            throw new IllegalArgumentException("Class ID cannot be null");
        }
        if (classTitle == null || classTitle.isBlank()) {
            throw new IllegalArgumentException("Class title cannot be null or blank");
        }
        if (classDescription == null || classDescription.isBlank()) {
            throw new IllegalArgumentException("Class description cannot be null or blank");
        }
        if (classPrice < 0) {
            throw new IllegalArgumentException("Class price cannot be negative");
        }
        if (appliedAt == null) {
            throw new IllegalArgumentException("Applied at cannot be null");
        }
    }
}