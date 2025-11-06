package com.bt.customer.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Address Entity Tests")
class AddressTest {

    @Test
    @DisplayName("Should create address with all fields")
    void shouldCreateAddressWithAllFields() {
        Address address = Address.builder()
                .id(1L)
                .line1("Apartment 4B")
                .line2("Building A")
                .street("Main Street")
                .city("Kuwait City")
                .state("Al Asimah")
                .pinCode("12345")
                .build();

        assertNotNull(address);
        assertEquals(1L, address.getId());
        assertEquals("Apartment 4B", address.getLine1());
        assertEquals("Building A", address.getLine2());
        assertEquals("Main Street", address.getStreet());
        assertEquals("Kuwait City", address.getCity());
        assertEquals("Al Asimah", address.getState());
        assertEquals("12345", address.getPinCode());
    }

    @Test
    @DisplayName("Should create address without line2")
    void shouldCreateAddressWithoutLine2() {
        Address address = Address.builder()
                .line1("123 Main St")
                .street("Main Street")
                .city("Kuwait City")
                .state("Al Asimah")
                .pinCode("54321")
                .build();

        assertNotNull(address);
        assertEquals("123 Main St", address.getLine1());
        assertNull(address.getLine2());
        assertEquals("Main Street", address.getStreet());
    }
}
