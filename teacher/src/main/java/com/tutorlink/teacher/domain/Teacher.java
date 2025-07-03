package com.tutorlink.teacher.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Teacher(Long id, String name, List<TeachingClass> teachingClasses) {
    public Teacher {
        if (Objects.isNull(id)) {
            id = 0L;
        }
        Objects.requireNonNull(name, "선생님의 이름이 비어있을 수 없습니다.");
    }

    public Teacher registerClass(TeachingClass teachingClass) {
        Objects.requireNonNull(teachingClass, "클래스를 등록할 수 없습니다.");
        if (teachingClasses.size() >= 8) {
            throw new IllegalStateException("클래스는 9개 이상 등록할 수 없습니다.");
        }
        List<TeachingClass> newTeachingClasses = new ArrayList<>(teachingClasses);
        newTeachingClasses.add(teachingClass);
        return new Teacher(id, name, newTeachingClasses);
    }

}