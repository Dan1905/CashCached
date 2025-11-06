package com.bt.customer.controller;

import com.bt.customer.dto.AuthResponse;
import com.bt.customer.dto.LoginRequest;
import com.bt.customer.dto.NameDTO;
import com.bt.customer.dto.MobileNumberDTO;
import com.bt.customer.dto.AddressDTO;
import com.bt.customer.dto.RegisterRequest;
import com.bt.customer.entity.User;
import com.bt.customer.exception.InvalidCredentialsException;
import com.bt.customer.exception.UserAlreadyExistsException;
import com.bt.customer.service.AuthService;
import com.bt.customer.service.MagicLinkService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController Tests")
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private AuthService authService;

        @MockitoBean
        private MagicLinkService magicLinkService;

        @MockitoBean
        private RedisSessionService redisSessionService;

        @MockitoBean
        private RedisTemplate<String, String> redisTemplate;

        private RegisterRequest registerRequest;
        private LoginRequest loginRequest;
        private AuthResponse authResponse;

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
                                .country("Kuwait")
                                .build();

                registerRequest = RegisterRequest.builder()
                                .password("password123")
                                .name(nameDTO)
                                .email("test@example.com")
                                .mobileNumber(mobileDTO)
                                .address(addressDTO)
                                .role(User.Role.CUSTOMER)
                                .build();

                loginRequest = LoginRequest.builder()
                                .email("test@example.com")
                                .password("password123")
                                .build();

                authResponse = AuthResponse.builder()
                                .token("sample.jwt.token")
                                .tokenType("Bearer")
                                .email("test@example.com")
                                .role("CUSTOMER")
                                .message("Success")
                                .build();
        }

        @Test
        @DisplayName("Should register user successfully with valid data")
        void shouldRegisterUserSuccessfully() throws Exception {
                when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.token").value("sample.jwt.token"))
                                .andExpect(jsonPath("$.email").value("test@example.com"))
                                .andExpect(jsonPath("$.role").value("CUSTOMER"));

                verify(authService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        @DisplayName("Should return conflict when username already exists")
        void shouldReturnConflictWhenUsernameExists() throws Exception {
                when(authService.register(any(RegisterRequest.class)))
                                .thenThrow(new UserAlreadyExistsException("Username already exists"));

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.message").value("Username already exists"));
        }

        @Test
        @DisplayName("Should return bad request for invalid registration data")
        void shouldReturnBadRequestForInvalidData() throws Exception {
                registerRequest.setEmail("invalid-email");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should login user successfully with valid credentials")
        void shouldLoginUserSuccessfully() throws Exception {
                when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").value("sample.jwt.token"))
                                .andExpect(jsonPath("$.email").value("test@example.com"));

                verify(authService, times(1)).login(any(LoginRequest.class));
        }

        @Test
        @DisplayName("Should return unauthorized for invalid credentials")
        void shouldReturnUnauthorizedForInvalidCredentials() throws Exception {
                when(authService.login(any(LoginRequest.class)))
                                .thenThrow(new InvalidCredentialsException("Invalid credentials"));

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Invalid username or password"));
        }

        @Test
        @DisplayName("Should return bad request when login fields are blank")
        void shouldReturnBadRequestWhenLoginFieldsAreBlank() throws Exception {
                loginRequest.setEmail("");
                loginRequest.setPassword("");

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isBadRequest());
        }
}
