package com.bt.customer.service;

import com.bt.customer.dto.NameDTO;
import com.bt.customer.dto.AddressDTO;
import com.bt.customer.dto.MobileNumberDTO;
import com.bt.customer.dto.UpdateProfileRequest;
import com.bt.customer.dto.UserProfileResponse;
import com.bt.customer.entity.Name;
import com.bt.customer.entity.Address;
import com.bt.customer.entity.MobileNumber;
import com.bt.customer.entity.User;
import com.bt.customer.exception.UserNotFoundException;
import com.bt.customer.repository.UserRepository;
import com.bt.customer.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Tests")
class CustomerServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private CustomerService customerService;

    private User user;
    private UserPrincipal userPrincipal;
    private Name name;
    private Address address;
    private MobileNumber mobileNumber;

    @BeforeEach
    void setUp() {
        name = Name.builder()
                .id(1L)
                .firstName("Test")
                .middleName("M")
                .lastName("User")
                .build();

        address = Address.builder()
                .id(1L)
                .line1("Apartment 4B")
                .line2("Building A")
                .street("Main Street")
                .city("Kuwait City")
                .state("Al Asimah")
                .pinCode("12345")
                .build();

        mobileNumber = MobileNumber.builder()
                .id(1L)
                .countryCode("+965")
                .number("12345678")
                .build();

        user = User.builder()
                .id(1L)
                .password("encoded-password")
                .name(name)
                .email("test@example.com")
                .mobileNumber(mobileNumber)
                .address(address)
                .role(User.Role.CUSTOMER)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userPrincipal = UserPrincipal.create(user);
    }

    @Test
    @DisplayName("Should get current user profile successfully")
    void shouldGetCurrentUserProfile() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserProfileResponse response = customerService.getCurrentUserProfile();

        assertNotNull(response);
        assertNotNull(response.getName());
        assertEquals("Test", response.getName().getFirstName());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("CUSTOMER", response.getRole());

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should throw exception when current user not found")
    void shouldThrowExceptionWhenCurrentUserNotFound() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> customerService.getCurrentUserProfile());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should get all customers successfully")
    void shouldGetAllCustomers() {
        Name name2 = Name.builder()
                .firstName("Customer")
                .lastName("Two")
                .build();

        User customer2 = User.builder()
                .id(2L)
                .name(name2)
                .email("customer2@example.com")
                .role(User.Role.CUSTOMER)
                .build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(user, customer2));

        List<UserProfileResponse> customers = customerService.getAllCustomers();

        assertNotNull(customers);
        assertEquals(2, customers.size());
        assertEquals("test@example.com", customers.get(0).getEmail());
        assertEquals("customer2@example.com", customers.get(1).getEmail());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update profile successfully")
    void shouldUpdateProfileSuccessfully() {
        NameDTO nameDTO = NameDTO.builder()
                .firstName("Updated")
                .lastName("Name")
                .build();

        MobileNumberDTO mobileDTO = MobileNumberDTO.builder()
                .countryCode("+965")
                .number("98765432")
                .build();

        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .name(nameDTO)
                .email("updated@example.com")
                .mobileNumber(mobileDTO)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserProfileResponse response = customerService.updateProfile(request);

        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update only provided fields in profile")
    void shouldUpdateOnlyProvidedFields() {
        NameDTO nameDTO = NameDTO.builder()
                .firstName("Updated")
                .lastName("Name")
                .build();

        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .name(nameDTO)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserProfileResponse response = customerService.updateProfile(request);

        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating to existing email")
    void shouldThrowExceptionForDuplicateEmail() {
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .email("existing@example.com")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> customerService.updateProfile(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should get current user role successfully")
    void shouldGetCurrentUserRole() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        String role = customerService.getCurrentUserRole();

        assertEquals("CUSTOMER", role);
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should return UNKNOWN when user not found for role check")
    void shouldReturnUnknownWhenUserNotFoundForRole() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        String role = customerService.getCurrentUserRole();

        assertEquals("UNKNOWN", role);
    }
}
