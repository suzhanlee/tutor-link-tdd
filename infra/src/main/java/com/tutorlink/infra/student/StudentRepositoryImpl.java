package com.tutorlink.infra.student;

import com.tutorlink.student.domain.Student;
import com.tutorlink.student.domain.repository.StudentRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class StudentRepositoryImpl implements StudentRepository {

    private final JpaStudentRepository jpaStudentRepository;

    public StudentRepositoryImpl(JpaStudentRepository jpaStudentRepository) {
        this.jpaStudentRepository = jpaStudentRepository;
    }

    @Override
    public Student save(Student student) {
        StudentEntity entity = StudentMapper.toEntity(student);
        StudentEntity savedEntity = jpaStudentRepository.save(entity);
        return StudentMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Student> findById(Long id) {
        return jpaStudentRepository.findById(id)
                .map(StudentMapper::toDomain);
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        return jpaStudentRepository.findByEmail(email)
                .map(StudentMapper::toDomain);
    }
}