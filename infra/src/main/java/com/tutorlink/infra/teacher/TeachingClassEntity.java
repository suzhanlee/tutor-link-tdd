package com.tutorlink.infra.teacher;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TeachingClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private int price;
    private LocalDateTime registeredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private TeacherEntity teacher;

    public TeachingClassEntity(String title, String description, int price, TeacherEntity teacher) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.teacher = teacher;
        this.registeredAt = LocalDateTime.now();
    }

    public TeachingClassEntity(String title, String description, int price, TeacherEntity teacher, LocalDateTime registeredAt) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.teacher = teacher;
        this.registeredAt = registeredAt;
    }

    public void addTeacher(TeacherEntity teacherEntity) {
        this.teacher = teacherEntity;
    }
}
