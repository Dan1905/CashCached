package com.bt.customer.controller;

import com.bt.customer.dto.NameDTO;
import com.bt.customer.dto.MobileNumberDTO;
import com.bt.customer.dto.AddressDTO;
import com.bt.customer.dto.UpdateProfileRequest;
import com.bt.customer.dto.UserProfileResponse;
import com.bt.customer.entity.Name;
import com.bt.customer.entity.MobileNumber;
import com.bt.customer.entity.Address;
import com.bt.customer.entity.User;
import com.bt.customer.service.AuthService;
import com.bt.customer.service.CustomerService;
import com.bt.customer.service.RedisSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CustomerController Tests")
class CustomerControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private CustomerService customerService;

        @MockitoBean
        private AuthService authService;

        @MockitoBean
        private RedisSessionService redisSessionService;

        @MockitoBean
        private RedisTemplate<String, String> redisTemplate;

        private UserProfileResponse profileResponse;
        private UpdateProfileRequest updateRequest;
        private User mockUser;

        @BeforeEach
        void setUp() {
                NameDTO nameDTO = NameDTO.builder()
                                .firstName("Test")
                                .lastName("User")
                                .build();

                MobileNumberDTO mobileDTO = MobileNumberDTO.builder()
                                .countryCode("+965")
                                .number("12345678")
                                .build();

                AddressDTO addressDTO = AddressDTO.builder()
                                .line1("Apartment 4B")
                                .street("Main Street")
                                .city("Kuwait City")
                                .state("Al Asimah")
                                .pinCode("12345")
                                .build();

                profileResponse = UserProfileResponse.builder()
                                .id(1L)
                                .name(nameDTO)
                                .email("test@example.com")
                                .mobileNumber(mobileDTO)
                                .address(addressDTO)
                                .role("CUSTOMER")
                                .active(true)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                NameDTO updatedNameDTO = NameDTO.builder()
                                .firstName("Updated")
                                .lastName("Name")
                                .build();

                MobileNumberDTO updatedMobileDTO = MobileNumberDTO.builder()
                                .countryCode("+965")
                                .number("98765432")
                                .build();

                updateRequest = UpdateProfileRequest.builder()
                                .name(updatedNameDTO)
                                .email("updated@example.com")
                                .mobileNumber(updatedMobileDTO)
                                .build();

                Name name = Name.builder()
                                .firstName("Test")
                                .lastName("User")
                                .build();

                mockUser = User.builder()
                                .id(1L)
                                .name(name)
                                .email("test@example.com")
                                .role(User.Role.CUSTOMER)
                                .build();
        }

        @Test
        @WithMockUser(username = "testuser", roles = { "CUSTOMER" })
        @DisplayName("Should get current user profile successfully")
        void shouldGetCurrentUserProfile() throws Exception {
                when(customerService.getCurrentUserProfile()).thenReturn(profileResponse);

                mockMvc.perform(get("/api/customer/profile")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("test@example.com"))
                                .andExpect(jsonPath("$.role").value("CUSTOMER"));

                verify(customerService, times(1)).getCurrentUserProfile();
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should get all customers when user is ADMIN")
        void shouldGetAllCustomersForAdmin() throws Exception {
                NameDTO name2 = NameDTO.builder()
                                .firstName("Customer")
                                .lastName("Two")
                                .build();

                List<UserProfileResponse> customers = Arrays.asList(
                                profileResponse,
                                UserProfileResponse.builder()
                                                .id(2L)
                                                .name(name2)
                                                .email("customer2@example.com")
                                                .role("CUSTOMER")
                                                .build());

                when(customerService.getAllCustomers()).thenReturn(customers);

                mockMvc.perform(get("/api/customer/all")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].email").value("test@example.com"))
                                .andExpect(jsonPath("$[1].email").value("customer2@example.com"));

                verify(customerService, times(1)).getAllCustomers();
        }

        @Test
        @WithMockUser(username = "bankofficer", roles = { "BANKOFFICER" })
        @DisplayName("Should get all customers when user is BANKOFFICER")
        void shouldGetAllCustomersForBankOfficer() throws Exception {
                List<UserProfileResponse> customers = Arrays.asList(profileResponse);
                when(customerService.getAllCustomers()).thenReturn(customers);

                mockMvc.perform(get("/api/customer/all")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray());

                verify(customerService, times(1)).getAllCustomers();
        }

        @Test
        @WithMockUser(username = "testuser", roles = { "CUSTOMER" })
        @DisplayName("Should update profile successfully")
        void shouldUpdateProfileSuccessfully() throws Exception {
                NameDTO updatedNameDTO = NameDTO.builder()
                                .firstName("Updated")
                                .lastName("Name")
                                .build();

                MobileNumberDTO updatedMobileDTO = MobileNumberDTO.builder()
                                .countryCode("+965")
                                .number("98765432")
                                .build();

                UserProfileResponse updatedResponse = UserProfileResponse.builder()
                                .id(1L)
                                .name(updatedNameDTO)
                                .email("updated@example.com")
                                .mobileNumber(updatedMobileDTO)
                                .role("CUSTOMER")
                                .build();

                when(customerService.updateProfile(any(UpdateProfileRequest.class))).thenReturn(updatedResponse);

                mockMvc.perform(put("/api/customer/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name.firstName").value("Updated"))
                                .andExpect(jsonPath("$.email").value("updated@example.com"));

                verify(customerService, times(1)).updateProfile(any(UpdateProfileRequest.class));
        }

        @Test
        @WithMockUser(username = "testuser", roles = { "CUSTOMER" })
        @DisplayName("Should return bad request for invalid email in update")
        void shouldReturnBadRequestForInvalidEmailUpdate() throws Exception {
                updateRequest.setEmail("invalid-email");

                mockMvc.perform(put("/api/customer/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "testuser", roles = { "CUSTOMER" })
        @DisplayName("Should get service status successfully")
        void shouldGetServiceStatus() throws Exception {
                when(customerService.getCurrentUserRole()).thenReturn("CUSTOMER");
                when(customerService.getCurrentUser()).thenReturn(mockUser);

                mockMvc.perform(get("/api/customer/status")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("OPERATIONAL"))
                                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                                .andExpect(jsonPath("$.email").value("test@example.com"));

                verify(customerService, times(1)).getCurrentUserRole();
                verify(customerService, times(1)).getCurrentUser();
        }
}
