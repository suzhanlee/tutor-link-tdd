package com.tutorlink.teacher.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTeacherCommand(@NotBlank String name) {

}
