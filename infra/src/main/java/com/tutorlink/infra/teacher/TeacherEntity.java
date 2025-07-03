package com.tutorlink.infra.teacher;

import com.tutorlink.teacher.domain.ActiveStatus;
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

    @Enumerated(EnumType.STRING)
    private ActiveStatus activeStatus;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeachingClassEntity> teachingClasses = new ArrayList<>();

    protected TeacherEntity(Long id, String name, ActiveStatus activeStatus) {
        this.id = id;
        this.name = name;
        this.activeStatus = activeStatus != null ? activeStatus : ActiveStatus.ACTIVE;
    }

    protected TeacherEntity(Long id, String name) {
        this(id, name, ActiveStatus.ACTIVE);
    }

    public TeacherEntity(String name, ActiveStatus activeStatus) {
        this.name = name;
        this.activeStatus = activeStatus != null ? activeStatus : ActiveStatus.ACTIVE;
    }

    public TeacherEntity(String name) {
        this(name, ActiveStatus.ACTIVE);
    }

    public void addClass(TeachingClassEntity teachingClassEntity) {
        if (this.teachingClasses == null) {
            this.teachingClasses = new ArrayList<>();
        }
        this.teachingClasses.add(teachingClassEntity);
        teachingClassEntity.addTeacher(this);
    }
}
