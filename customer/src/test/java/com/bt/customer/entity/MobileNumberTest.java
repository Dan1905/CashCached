package com.bt.customer.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MobileNumber Entity Tests")
class MobileNumberTest {

    @Test
    @DisplayName("Should get full mobile number")
    void shouldGetFullMobileNumber() {
        MobileNumber mobileNumber = MobileNumber.builder()
                .countryCode("+965")
                .number("12345678")
                .build();

        assertEquals("+96512345678", mobileNumber.getFullNumber());
    }

    @Test
    @DisplayName("Should create mobile number with all fields")
    void shouldCreateMobileNumberWithAllFields() {
        MobileNumber mobileNumber = MobileNumber.builder()
                .id(1L)
                .countryCode("+1")
                .number("2025551234")
                .build();

        assertNotNull(mobileNumber);
        assertEquals(1L, mobileNumber.getId());
        assertEquals("+1", mobileNumber.getCountryCode());
        assertEquals("2025551234", mobileNumber.getNumber());
    }
}
