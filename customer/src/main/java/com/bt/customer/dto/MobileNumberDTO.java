package com.bt.customer.dto;

import com.bt.customer.entity.MobileNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Mobile number information")
public class MobileNumberDTO {

    @NotBlank(message = "Country code is required")
    @Size(max = 5, message = "Country code cannot exceed 5 characters")
    @Pattern(regexp = "^\\+[0-9]{1,4}$", message = "Country code must start with + and contain 1-4 digits")
    @Schema(description = "Country code", example = "+965")
    private String countryCode;

    @NotBlank(message = "Mobile number is required")
    @Size(max = 15, message = "Mobile number cannot exceed 15 characters")
    @Pattern(regexp = "^[0-9]{7,15}$", message = "Mobile number must contain 7-15 digits")
    @Schema(description = "Mobile number", example = "12345678")
    private String number;

    public static MobileNumberDTO fromEntity(MobileNumber mobileNumber) {
        if (mobileNumber == null) {
            return null;
        }
        return MobileNumberDTO.builder()
                .countryCode(mobileNumber.getCountryCode())
                .number(mobileNumber.getNumber())
                .build();
    }

    public MobileNumber toEntity() {
        return MobileNumber.builder()
                .countryCode(countryCode)
                .number(number)
                .build();
    }
}
