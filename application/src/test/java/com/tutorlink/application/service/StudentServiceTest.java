package com.tutorlink.application.service;

import com.tutorlink.student.dto.ApplyClassCommand;
import com.tutorlink.student.dto.ApplyClassResult;
import com.tutorlink.teacher.domain.ClassApplication;
import com.tutorlink.teacher.domain.TeachingClass;
import com.tutorlink.teacher.domain.repository.ClassApplicationRepository;
import com.tutorlink.teacher.domain.repository.TeachingClassRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private TeachingClassRepository teachingClassRepository;

    @Mock
    private ClassApplicationRepository classApplicationRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("학생이 클래스를 신청하면 클래스 신청 정보가 저장되고 결과가 반환된다")
    void applyClass_success() {
        // given
        Long studentId = 1L;
        Long classId = 100L;
        ApplyClassCommand command = new ApplyClassCommand(studentId, classId);

        LocalDateTime now = LocalDateTime.now();
        TeachingClass teachingClass = new TeachingClass(
                classId,
                2L,
                "Java 프로그래밍 기초",
                "자바 프로그래밍의 기초를 배웁니다.",
                50000,
                now.minusDays(10),
                now.minusDays(5),
                now.plusDays(5)
        );

        ClassApplication classApplication = ClassApplication.createFromTeachingClass(teachingClass, studentId);
        ClassApplication savedClassApplication = new ClassApplication(
                1L,
                studentId,
                classId,
                teachingClass.title(),
                teachingClass.description(),
                teachingClass.price(),
                teachingClass.registeredAt(),
                teachingClass.recruitmentStartAt(),
                teachingClass.recruitmentEndAt(),
                now
        );

        when(teachingClassRepository.findById(classId)).thenReturn(Optional.of(teachingClass));
        when(classApplicationRepository.save(any(ClassApplication.class))).thenReturn(savedClassApplication);

        // when
        ApplyClassResult result = studentService.applyClass(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.applicationId()).isEqualTo(savedClassApplication.id());
        assertThat(result.studentId()).isEqualTo(studentId);
        assertThat(result.classId()).isEqualTo(classId);
        assertThat(result.classTitle()).isEqualTo(teachingClass.title());
        assertThat(result.classDescription()).isEqualTo(teachingClass.description());
        assertThat(result.classPrice()).isEqualTo(teachingClass.price());
        assertThat(result.appliedAt()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 클래스를 신청하면 예외가 발생한다")
    void applyClass_classNotFound() {
        // given
        Long studentId = 1L;
        Long classId = 999L;
        ApplyClassCommand command = new ApplyClassCommand(studentId, classId);

        when(teachingClassRepository.findById(classId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studentService.applyClass(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 클래스");
    }
}