package com.tutorlink.teacher.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Teacher(Long id, String name, List<TeachingClass> teachingClasses, ActiveStatus activeStatus) {
    public Teacher {
        if (Objects.isNull(id)) {
            id = 0L;
        }
        Objects.requireNonNull(name, "선생님의 이름이 비어있을 수 없습니다.");
        if (Objects.isNull(activeStatus)) {
            activeStatus = ActiveStatus.ACTIVE;
        }
        if (Objects.isNull(teachingClasses)) {
            teachingClasses = new ArrayList<>();
        }
    }

    public Teacher registerClass(TeachingClass teachingClass) {
        Objects.requireNonNull(teachingClass, "클래스를 등록할 수 없습니다.");
        if (teachingClasses.size() >= 10) {
            throw new IllegalStateException("클래스는 10개 이상 등록할 수 없습니다.");
        }
        List<TeachingClass> newTeachingClasses = new ArrayList<>(teachingClasses);
        newTeachingClasses.add(teachingClass);
        return new Teacher(id, name, newTeachingClasses, activeStatus);
    }

}
