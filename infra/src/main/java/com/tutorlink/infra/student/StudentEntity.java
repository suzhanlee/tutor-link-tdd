package com.tutorlink.infra.student;

import com.tutorlink.student.domain.ActiveStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    @Enumerated(EnumType.STRING)
    private ActiveStatus activeStatus;

    protected StudentEntity(Long id, String name, String email, ActiveStatus activeStatus) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.activeStatus = activeStatus != null ? activeStatus : ActiveStatus.ACTIVE;
    }

    public StudentEntity(String name, String email, ActiveStatus activeStatus) {
        this.name = name;
        this.email = email;
        this.activeStatus = activeStatus != null ? activeStatus : ActiveStatus.ACTIVE;
    }

    public StudentEntity(String name, String email) {
        this(name, email, ActiveStatus.ACTIVE);
    }
}
