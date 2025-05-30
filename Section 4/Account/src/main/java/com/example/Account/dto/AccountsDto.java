package com.example.Account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(
        name = "Account",
        description = "Schema to Hold Account related Information"
)
public class AccountsDto {

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
