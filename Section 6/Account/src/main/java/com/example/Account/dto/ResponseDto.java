package com.example.Account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(
        name = "Success Response",
        description = "Schema to hold Successful response status"
)
public class ResponseDto {

    @Schema(
            description = "status code in the response"
    )
    private String statusCode;

    @Schema(
            description = "status message in the response"
    )
    private String statusMsg;

}
