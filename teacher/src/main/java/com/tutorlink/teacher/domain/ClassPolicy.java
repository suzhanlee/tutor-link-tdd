package com.tutorlink.teacher.domain;

import com.tutorlink.teacher.dto.RegisterClassCommand;
import java.time.LocalTime;

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

        // 등록 시간 검증 (오전 6시 ~ 10시 사이)
        int hour = registerClassCommand.registeredAt().getHour();
        if (hour < 6 || hour >= 10) {
            throw new IllegalArgumentException("클래스 등록은 오전 6시부터 10시 사이에만 가능합니다.");
        }
    }
}
