package com.tutorlink.student.domain;

import java.util.Objects;

public record Student(Long id, String name, Email email, ActiveStatus activeStatus) {
    public Student {
        if (Objects.isNull(id)) {
            id = 0L;
        }
        Objects.requireNonNull(name, "학생의 이름이 비어있을 수 없습니다.");
        Objects.requireNonNull(email, "학생의 이메일이 비어있을 수 없습니다.");
        if (Objects.isNull(activeStatus)) {
            activeStatus = ActiveStatus.ACTIVE;
        }
    }

    public boolean isValid() {
        return activeStatus == ActiveStatus.ACTIVE;
    }
}