package com.tutorlink.teacher.domain;

import com.tutorlink.teacher.dto.RegisterClassCommand;

public class ClassPolicy {
    public void validate(Teacher teacher, RegisterClassCommand registerClassCommand) {
        if (teacher.teachingClasses().size() >= 8) {
            throw new IllegalStateException("최대 클래스 개수를 초과했습니다.");
        }
        String title = registerClassCommand.title();
        if (title.length() < 10) {
            throw new IllegalArgumentException("클래스 제목은 10자 이상이어야 합니다.");
        }
        if (title.length() >= 100) {
            throw new IllegalArgumentException("클래스 제목은 100자 미만이어야 합니다.");
        }
    }
}
