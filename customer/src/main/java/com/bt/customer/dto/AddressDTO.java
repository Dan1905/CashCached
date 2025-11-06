package com.bt.customer.dto;

import com.bt.customer.entity.Address;
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
@Schema(description = "Address information")
public class AddressDTO {

    @NotBlank(message = "Address line 1 is required")
    @Size(max = 100, message = "Address line 1 cannot exceed 100 characters")
    @Schema(description = "Address line 1", example = "Apartment 4B")
    private String line1;

    @Size(max = 100, message = "Address line 2 cannot exceed 100 characters")
    @Schema(description = "Address line 2", example = "Building A")
    private String line2;

    @NotBlank(message = "Street name is required")
    @Size(max = 100, message = "Street name cannot exceed 100 characters")
    @Schema(description = "Street name", example = "Main Street")
    private String street;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City cannot exceed 50 characters")
    @Schema(description = "City", example = "Kuwait City")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 50, message = "State cannot exceed 50 characters")
    @Schema(description = "State", example = "Al Asimah")
    private String state;

    @NotBlank(message = "Pin code is required")
    @Size(max = 10, message = "Pin code cannot exceed 10 characters")
    @Pattern(regexp = "^[0-9]{4,10}$", message = "Pin code must be 4-10 digits")
    @Schema(description = "Pin code", example = "12345")
    private String pinCode;

    @NotBlank(message = "Country is required")
    @Size(max = 50, message = "Country cannot exceed 50 characters")
    @Schema(description = "Country", example = "Kuwait")
    private String country;

    public static AddressDTO fromEntity(Address address) {
        if (address == null) {
            return null;
        }
        return AddressDTO.builder()
                .line1(address.getLine1())
                .line2(address.getLine2())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .pinCode(address.getPinCode())
                .country(address.getCountry())
                .build();
    }

    public Address toEntity() {
        return Address.builder()
                .line1(line1)
                .line2(line2)
                .street(street)
                .city(city)
                .state(state)
                .pinCode(pinCode)
                .country(country)
                .build();
    }
}
