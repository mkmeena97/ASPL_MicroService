package com.example.Account.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddMoneyRequestDto {

    @NotNull(message = "Mobile number is required")
    @Pattern(regexp = "[0-9]{10}", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount should be positive")
    private BigDecimal amount;

    // Optional description or reference field if UI/API wants it
    // private String description;
}

