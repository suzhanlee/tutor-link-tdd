package com.tutorlink.teacher.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TeachingClassTest {

    @Test
    @DisplayName("수업이 모집 중인지 확인할 수 있다 - 모집 중")
    void isRecruiting_WhenCurrentTimeIsBetweenRecruitmentStartAndEndTime_ReturnsRecruiting() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime recruitmentStartAt = now.minusDays(1);
        LocalDateTime recruitmentEndAt = now.plusDays(1);
        
        TeachingClass teachingClass = new TeachingClass(
                1L,
                1L,
                "Java Programming",
                "Learn Java Programming",
                10000,
                now.minusDays(2),
                recruitmentStartAt,
                recruitmentEndAt
        );

        // When
        RecruitmentStatus status = teachingClass.getRecruitmentStatus();

        // Then
        assertThat(status).isEqualTo(RecruitmentStatus.RECRUITING);
    }

    @Test
    @DisplayName("수업이 모집 중인지 확인할 수 있다 - 모집 종료")
    void isRecruiting_WhenCurrentTimeIsAfterRecruitmentEndTime_ReturnsClosed() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime recruitmentStartAt = now.minusDays(2);
        LocalDateTime recruitmentEndAt = now.minusDays(1);
        
        TeachingClass teachingClass = new TeachingClass(
                1L,
                1L,
                "Java Programming",
                "Learn Java Programming",
                10000,
                now.minusDays(3),
                recruitmentStartAt,
                recruitmentEndAt
        );

        // When
        RecruitmentStatus status = teachingClass.getRecruitmentStatus();

        // Then
        assertThat(status).isEqualTo(RecruitmentStatus.CLOSED);
    }

    @Test
    @DisplayName("수업이 모집 중인지 확인할 수 있다 - 아직 열리지 않음")
    void isRecruiting_WhenCurrentTimeIsBeforeRecruitmentStartTime_ReturnsNotYetOpened() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime recruitmentStartAt = now.plusDays(1);
        LocalDateTime recruitmentEndAt = now.plusDays(2);
        
        TeachingClass teachingClass = new TeachingClass(
                1L,
                1L,
                "Java Programming",
                "Learn Java Programming",
                10000,
                now.minusDays(3),
                recruitmentStartAt,
                recruitmentEndAt
        );

        // When
        RecruitmentStatus status = teachingClass.getRecruitmentStatus();

        // Then
        assertThat(status).isEqualTo(RecruitmentStatus.NOT_YET_OPENED);
    }
}