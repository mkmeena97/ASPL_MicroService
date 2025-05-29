package com.example.Account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(
        name = "Customer Details",
        description = "Schema to hold Customer and Account related Details"
)
public class CustomerDetailsDto {

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

    @Schema(
            description = "Account Number Of MyBank",
            example = "5246789654"
    )
    @NotNull(message = "Account Number can not be null")
    @Digits(integer = 10, fraction = 0, message = "Account number must be exactly 10 digits")
    private Long accountNumber;

    @Schema(
            description = "Account Type Of MyBank",
            example = "Savings"
    )
    @NotEmpty(message = "Account type can not be null or empty")
    private String accountType;

    @Schema(
            description = "Branch Address Of MyBank",
            example = "32 Chhattarpur, New Delhi"
    )
    @NotEmpty(message = "Branch address can not be null or empty")
    private String branchAddress;
}
