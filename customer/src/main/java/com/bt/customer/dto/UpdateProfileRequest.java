package com.bt.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for updating user profile")
public class UpdateProfileRequest {

    @Valid
    @Schema(description = "Updated name details")
    private NameDTO name;

    @Email(message = "Email must be valid")
    @Schema(description = "Updated email address", example = "john.updated@example.com")
    private String email;

    @Valid
    @Schema(description = "Updated mobile number details")
    private MobileNumberDTO mobileNumber;

    @Schema(description = "Date of birth", example = "1990-05-15")
    private LocalDate dateOfBirth;

    @Valid
    @Schema(description = "Updated address details")
    private AddressDTO address;

    @Size(min = 12, max = 12, message = "Aadhaar number must be exactly 12 digits")
    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar number must contain only digits")
    @Schema(description = "Aadhaar number (12 digits)", example = "123456789012")
    private String aadhaarNumber;

    @Size(min = 10, max = 10, message = "PAN number must be exactly 10 characters")
    @Pattern(regexp = "^[A-Z0-9]{10}$", message = "PAN number format is invalid")
    @Schema(description = "PAN number (10 characters)", example = "ABCDE1234F")
    private String panNumber;

    @Size(max = 10, message = "Currency code cannot exceed 10 characters")
    @Schema(description = "Preferred fiat currency for CashCached conversions", example = "USD")
    private String preferredCurrency;
}
