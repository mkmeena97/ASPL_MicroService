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
public class FreezeUnfreezeRequestDto {

    @NotNull(message = "Mobile number is required")
    @Pattern(regexp = "[0-9]{10}", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @NotNull(message = "Action is required")
    private Action action; // Enum indicating whether to FREEZE or UNFREEZE

    // Optionally, you can add a reason/comment
    // private String reason;

    public enum Action {
        FREEZE,
        UNFREEZE
    }
}

