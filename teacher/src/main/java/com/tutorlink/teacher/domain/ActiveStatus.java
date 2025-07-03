package com.tutorlink.teacher.domain;

import lombok.Getter;

@Getter
public enum ActiveStatus {
    ACTIVE("활성"),
    INACTIVE("비활성");

    private final String description;

    ActiveStatus(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }
}