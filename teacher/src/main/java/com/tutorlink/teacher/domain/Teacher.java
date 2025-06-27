package com.tutorlink.teacher.domain;

import java.util.Objects;

public record Teacher(Long id, String name) {
    public Teacher {
        if (Objects.isNull(id)) {
            id = 0L;
        }
        Objects.requireNonNull(name, "선생님의 이름이 비어있을 수 없습니다.");
    }
}