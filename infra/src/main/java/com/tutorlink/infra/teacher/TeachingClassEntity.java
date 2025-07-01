package com.tutorlink.infra.teacher;

import jakarta.persistence.*;

@Entity
public class TeachingClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private TeacherEntity teacher;
}
