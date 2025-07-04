package com.tutorlink.student.domain.repository;

import com.tutorlink.student.domain.Student;

import java.util.Optional;

public interface StudentRepository {
    Student save(Student student);

    Optional<Student> findById(Long id);

    Optional<Student> findByEmail(String email);
}