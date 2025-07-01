package com.tutorlink.infra.teacher;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TeacherEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeachingClassEntity> teachingClasses = new ArrayList<>();

    protected TeacherEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public TeacherEntity(String name) {
        this.name = name;
    }

    public void addClass(TeachingClassEntity teachingClassEntity) {
        if (this.teachingClasses == null) {
            this.teachingClasses = new ArrayList<>();
        }
        this.teachingClasses.add(teachingClassEntity);
        teachingClassEntity.addTeacher(this);
    }
}
