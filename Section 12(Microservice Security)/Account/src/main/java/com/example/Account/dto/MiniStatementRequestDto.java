package com.example.Account.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MiniStatementRequestDto {

    @NotNull(message = "Mobile number is required")
    @Pattern(regexp = "[0-9]{10}", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @Min(value = 1, message = "Limit must be at least 1")
    private int limit = 10; // Default value, returns last 10 transactions

    // You may optionally add date range or filter fields if needed in the future
}

