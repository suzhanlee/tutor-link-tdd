package com.tutorlink.application.service;

import com.tutorlink.teacher.domain.Teacher;
import com.tutorlink.teacher.domain.repository.TeacherRepository;
import com.tutorlink.teacher.dto.CreateTeacherCommand;
import com.tutorlink.teacher.dto.RegisterTeacherResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @InjectMocks
    private TeacherService teacherService;

    @Mock
    private TeacherRepository teacherRepository;

    @Test
    @DisplayName("선생님 정보로 선생님을 등록할 수 있다.")
    void registerTeacher() {
        // given
        when(teacherRepository.save(Mockito.any(Teacher.class))).thenReturn(new Teacher(1L, "suchan", null, null));

        // when
        RegisterTeacherResult result = teacherService.registerTeacher(new CreateTeacherCommand("suchan"));

        // then
        ArgumentCaptor<Teacher> teacherArgumentCaptor = ArgumentCaptor.forClass(Teacher.class);
        verify(teacherRepository).save(teacherArgumentCaptor.capture());
        Teacher teacher = teacherArgumentCaptor.getValue();
        assertThat(teacher.name()).isEqualTo("suchan");

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }
}