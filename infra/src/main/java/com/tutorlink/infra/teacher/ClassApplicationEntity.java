package com.tutorlink.infra.teacher;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ClassApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teaching_class_id")
    private TeachingClassEntity teachingClass;

    private LocalDateTime appliedAt;

    public ClassApplicationEntity(Long studentId, TeachingClassEntity teachingClass, LocalDateTime appliedAt) {
        this.studentId = studentId;
        this.teachingClass = teachingClass;
        this.appliedAt = appliedAt;
    }

    protected ClassApplicationEntity(Long id, Long studentId, TeachingClassEntity teachingClass, LocalDateTime appliedAt) {
        this.id = id;
        this.studentId = studentId;
        this.teachingClass = teachingClass;
        this.appliedAt = appliedAt;
    }
}
