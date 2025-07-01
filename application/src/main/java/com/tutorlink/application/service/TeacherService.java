package com.tutorlink.application.service;

import com.tutorlink.teacher.domain.Teacher;
import com.tutorlink.teacher.domain.repository.TeacherRepository;
import com.tutorlink.teacher.dto.CreateTeacherCommand;
import com.tutorlink.teacher.dto.RegisterTeacherResult;

public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public RegisterTeacherResult registerTeacher(CreateTeacherCommand command) {
        Teacher teacher = new Teacher(0L, command.name());
        Teacher savedTeacher = teacherRepository.save(teacher);
        return new RegisterTeacherResult(savedTeacher.id());
    }
}
