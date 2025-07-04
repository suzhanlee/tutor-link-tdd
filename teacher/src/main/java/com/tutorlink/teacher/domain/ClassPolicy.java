package com.tutorlink.teacher.domain;

import com.tutorlink.teacher.dto.RegisterClassCommand;
import java.time.LocalDateTime;

public class ClassPolicy {
    public void validate(Teacher teacher, RegisterClassCommand registerClassCommand) {
        validateTeacher(teacher);
        validateTotalClassCnt(teacher);
        validateClassTitle(registerClassCommand.title());
        validateClassRegistrationTime(registerClassCommand);
        validateClassDates(registerClassCommand);
    }

    private void validateTeacher(Teacher teacher) {
        // Teacher 존재 여부 및 활성 상태 확인
        if (teacher == null) {
            throw new IllegalArgumentException("선생님이 존재하지 않습니다.");
        }
        if (!teacher.activeStatus().isActive()) {
            throw new IllegalStateException("비활성화된 선생님은 클래스를 등록할 수 없습니다.");
        }
    }

    private void validateTotalClassCnt(Teacher teacher) {
        if (teacher.teachingClasses().size() > 10) {
            throw new IllegalStateException("최대 클래스 개수를 초과했습니다.");
        }
    }

    private void validateClassTitle(String title) {
        if (title.length() < 10) {
            throw new IllegalArgumentException("클래스 제목은 10자 이상이어야 합니다.");
        }
        if (title.length() >= 100) {
            throw new IllegalArgumentException("클래스 제목은 100자 미만이어야 합니다.");
        }
    }

    private void validateClassRegistrationTime(RegisterClassCommand registerClassCommand) {
        // 등록 시간 검증 (오전 6시 ~ 10시 사이)
        int hour = registerClassCommand.registeredAt().getHour();
        if (hour < 6 || hour >= 10) {
            throw new IllegalArgumentException("클래스 등록은 오전 6시부터 10시 사이에만 가능합니다.");
        }
    }

    private void validateClassDates(RegisterClassCommand registerClassCommand) {
        // 시작일이 종료일 이전인지 검증
        LocalDateTime startDate = registerClassCommand.recruitmentStartAt();
        LocalDateTime endDate = registerClassCommand.recruitmentEndAt();

        if (!startDate.isBefore(endDate)) {
            throw new IllegalArgumentException("클래스의 시작일은 종료일 이전이어야 합니다.");
        }
    }
}
