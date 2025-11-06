package com.bt.customer.service;

import com.bt.customer.dto.UpdateProfileRequest;
import com.bt.customer.dto.UserProfileResponse;
import com.bt.customer.entity.User;
import com.bt.customer.exception.UserNotFoundException;
import com.bt.customer.repository.UserRepository;
import com.bt.customer.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Cacheable(value = "userProfile", key = "#root.methodName + ':' + T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()")
    public UserProfileResponse getCurrentUserProfile() {
        User user = getCurrentUser();
        return UserProfileResponse.fromUser(user);
    }

    @Cacheable(value = "allCustomers", key = "'all'")
    public List<UserProfileResponse> getAllCustomers() {
        return userRepository.findAll().stream()
                .map(UserProfileResponse::fromUser)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = { "userProfile", "customerById", "allCustomers" }, allEntries = true)
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();

        if (request.getName() != null) {
            if (user.getName() == null) {
                user.setName(request.getName().toEntity());
            } else {
                if (request.getName().getFirstName() != null && !request.getName().getFirstName().isBlank()) {
                    user.getName().setFirstName(request.getName().getFirstName());
                }
                if (request.getName().getMiddleName() != null) {
                    user.getName().setMiddleName(request.getName().getMiddleName());
                }
                if (request.getName().getLastName() != null && !request.getName().getLastName().isBlank()) {
                    user.getName().setLastName(request.getName().getLastName());
                }
            }
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!user.getEmail().equals(request.getEmail()) &&
                    userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getMobileNumber() != null) {
            if (user.getMobileNumber() == null) {
                user.setMobileNumber(request.getMobileNumber().toEntity());
            } else {
                if (request.getMobileNumber().getCountryCode() != null && !request.getMobileNumber().getCountryCode().isBlank()) {
                    user.getMobileNumber().setCountryCode(request.getMobileNumber().getCountryCode());
                }
                if (request.getMobileNumber().getNumber() != null && !request.getMobileNumber().getNumber().isBlank()) {
                    user.getMobileNumber().setNumber(request.getMobileNumber().getNumber());
                }
            }
        }

        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }

        if (request.getAddress() != null) {
            if (user.getAddress() == null) {
                user.setAddress(request.getAddress().toEntity());
            } else {
                if (request.getAddress().getLine1() != null && !request.getAddress().getLine1().isBlank()) {
                    user.getAddress().setLine1(request.getAddress().getLine1());
                }
                if (request.getAddress().getLine2() != null) {
                    user.getAddress().setLine2(request.getAddress().getLine2());
                }
                if (request.getAddress().getStreet() != null && !request.getAddress().getStreet().isBlank()) {
                    user.getAddress().setStreet(request.getAddress().getStreet());
                }
                if (request.getAddress().getCity() != null && !request.getAddress().getCity().isBlank()) {
                    user.getAddress().setCity(request.getAddress().getCity());
                }
                if (request.getAddress().getState() != null && !request.getAddress().getState().isBlank()) {
                    user.getAddress().setState(request.getAddress().getState());
                }
                if (request.getAddress().getPinCode() != null && !request.getAddress().getPinCode().isBlank()) {
                    user.getAddress().setPinCode(request.getAddress().getPinCode());
                }
            }
        }

        if (request.getAadhaarNumber() != null && !request.getAadhaarNumber().isBlank()) {
            user.setAadhaarNumber(request.getAadhaarNumber());
        }

        if (request.getPanNumber() != null && !request.getPanNumber().isBlank()) {
            user.setPanNumber(request.getPanNumber());
        }

        if (request.getPreferredCurrency() != null) {
            String code = request.getPreferredCurrency().trim().toUpperCase();
            if (!code.isEmpty()) {
                user.setPreferredCurrency(code);
            }
        }

        User updatedUser = userRepository.save(user);
        return UserProfileResponse.fromUser(updatedUser);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));
    }

    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return userRepository.findByEmail(userPrincipal.getUsername())
                .map(user -> user.getRole().name())
                .orElse("UNKNOWN");
    }

    @Cacheable(value = "customerById", key = "#id")
    public UserProfileResponse getCustomerById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Customer with ID " + id + " not found"));
        return UserProfileResponse.fromUser(user);
    }

    public void enableTwoFactorForCurrentUser() {
        User user = getCurrentUser();
        user.setTwoFactorEnabled(true);
        userRepository.save(user);
        log.info("Two-factor authentication enabled for user: {}", user.getEmail());
    }

    public void disableTwoFactorForCurrentUser() {
        User user = getCurrentUser();
        user.setTwoFactorEnabled(false);
        userRepository.save(user);
        log.info("Two-factor authentication disabled for user: {}", user.getEmail());
    }

    public boolean isTwoFactorEnabledForCurrentUser() {
        User user = getCurrentUser();
        return Boolean.TRUE.equals(user.getTwoFactorEnabled());
    }

    public boolean isTwoFactorEnabled(String email) {
        return userRepository.findByEmail(email)
                .map(user -> Boolean.TRUE.equals(user.getTwoFactorEnabled()))
                .orElse(false);
    }

    @CacheEvict(value = "userProfile", allEntries = true)
    public void changePassword(String currentPassword, String newPassword) {
        User user = getCurrentUser();
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
