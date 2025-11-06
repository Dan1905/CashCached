package com.bt.customer.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Name Entity Tests")
class NameTest {

    @Test
    @DisplayName("Should get full name with middle name")
    void shouldGetFullNameWithMiddleName() {
        Name name = Name.builder()
                .firstName("John")
                .middleName("Michael")
                .lastName("Doe")
                .build();

        assertEquals("John Michael Doe", name.getFullName());
    }

    @Test
    @DisplayName("Should get full name without middle name")
    void shouldGetFullNameWithoutMiddleName() {
        Name name = Name.builder()
                .firstName("John")
                .lastName("Doe")
                .build();

        assertEquals("John Doe", name.getFullName());
    }

    @Test
    @DisplayName("Should get full name with blank middle name")
    void shouldGetFullNameWithBlankMiddleName() {
        Name name = Name.builder()
                .firstName("John")
                .middleName("   ")
                .lastName("Doe")
                .build();

        assertEquals("John Doe", name.getFullName());
    }
}
