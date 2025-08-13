package com.example.Account.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeAccountTypeRequestDto {

    @NotNull(message = "Mobile number is required")
    @Pattern(regexp = "[0-9]{10}", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @NotNull(message = "New account type is required")
    private String newAccountType; // e.g. "SAVINGS", "CURRENT", etc.

    // Optionally, you may want to validate allowed values or use an enum.
}

