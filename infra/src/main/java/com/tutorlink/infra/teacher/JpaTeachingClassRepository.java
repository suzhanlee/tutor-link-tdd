package com.tutorlink.infra.teacher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTeachingClassRepository extends JpaRepository<TeachingClassEntity, Long> {
}