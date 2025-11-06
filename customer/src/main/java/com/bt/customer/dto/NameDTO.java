package com.bt.customer.dto;

import com.bt.customer.entity.Name;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Name information")
public class NameDTO {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Schema(description = "First name", example = "John")
    private String firstName;

    @Size(max = 50, message = "Middle name cannot exceed 50 characters")
    @Schema(description = "Middle name", example = "Michael")
    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    public static NameDTO fromEntity(Name name) {
        if (name == null) {
            return null;
        }
        return NameDTO.builder()
                .firstName(name.getFirstName())
                .middleName(name.getMiddleName())
                .lastName(name.getLastName())
                .build();
    }

    public Name toEntity() {
        return Name.builder()
                .firstName(firstName)
                .middleName(middleName)
                .lastName(lastName)
                .build();
    }
}
