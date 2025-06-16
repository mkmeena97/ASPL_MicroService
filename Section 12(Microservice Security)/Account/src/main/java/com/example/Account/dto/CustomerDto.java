package com.example.Account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(
        name = "Customer",
        description = "Schema to Hold Customer related Information"
)
public class CustomerDto {

    @Schema(
            description = "Name of the Customer",
            example = "Mahendra Kumar"
    )
    @NotEmpty(message = "Name can not be null or Empty")
    @Size(min = 5, max = 30, message = "The length of the customer name should be between 5 to 30")
    private String name;

    @Schema(
            description = "Email Address of the Customer",
            example = "mahendra.m@audilitycssolutions.com"
    )
    @NotEmpty(message = "Email address can not be null or empty")
    @Email(message = "Email address should be a valid value")
    private String email;

    @Schema(
            description = "Mobile Number of the Customer",
            example = "7665770360"
    )
    @NotEmpty(message = "Mobile Number can not be null or empty")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits ")
    private String mobileNumber;
}
