package com.tutorlink.application.service;

import com.tutorlink.teacher.domain.Teacher;
import com.tutorlink.teacher.domain.repository.TeacherRepository;
import com.tutorlink.teacher.dto.CreateTeacherCommand;
import com.tutorlink.teacher.dto.RegisterTeacherResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    @DisplayName("")
    void registerTeacher() {
        // given
        when(teacherRepository.save(Mockito.any(Teacher.class))).thenReturn(new Teacher(1L, "suchan"));

        // when
        RegisterTeacherResult result = teacherService.registerTeacher(new CreateTeacherCommand("suchan"));

        // then
        verify(teacherRepository).save(Mockito.any(Teacher.class));
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }
}