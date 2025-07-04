package com.tutorlink.student.dto;

/**
 * Command to apply for a class.
 */
public record ApplyClassCommand(Long studentId, Long classId) {
    public ApplyClassCommand {
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }
        if (classId == null) {
            throw new IllegalArgumentException("Class ID cannot be null");
        }
    }
}