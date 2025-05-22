package com.example.Account.controllers;

import com.example.Account.constants.AccountsConstants;
import com.example.Account.dto.CustomerDetailsDto;
import com.example.Account.dto.CustomerDto;
import com.example.Account.dto.ErrorResponseDto;
import com.example.Account.dto.ResponseDto;
import com.example.Account.service.IAccountsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Crud Rest APIs for accounts in MyBank",
        description = "Crud REST APIs in MyBank to CREATE, UPDATE, FETCH, DELETE accounts details"
)
@RestController
@RequestMapping(path = "/api",produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class AccountsController {

    private IAccountsService iAccountsService;

    @Operation(
            summary = "Create Account",
            description = "REST API to create new customer & Account"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Http Status Created"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAccount(@Valid @RequestBody CustomerDto customerDto){

        iAccountsService.createAccount(customerDto);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(AccountsConstants.STATUS_201, AccountsConstants.MESSAGE_201));
    }

    @Operation(
            summary = "Fetch Account Details",
            description = "REST API to fetch Customer & Account details based on Mobile Number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP status Ok"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/fetch")
    public  ResponseEntity<CustomerDetailsDto> fetchAccountDetails(@RequestParam
                                                                       @Pattern(regexp = "(^$|[0-9]{10})", message = "Account number must be 10 digits ")
                                                                       String mobileNumber){
        CustomerDetailsDto customerDetailsDto =iAccountsService.fetchAccount(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(customerDetailsDto);
    }

    @Operation(
            summary = "Update Account Details",
            description = "REST API to update Customer & Account details"
    )
    @ApiResponses({
            @ApiResponse(
                   responseCode = "200",
                    description = "HTTP status Ok"
            ),
            @ApiResponse(
                   responseCode = "500",
                    description = "HTTP status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "Expectation Failed"
            )
    }
    )
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateAccountDetails(@Valid @RequestBody CustomerDetailsDto customerDetailsDto){
        boolean isUpdated = iAccountsService.updateAccount(customerDetailsDto);
        if(isUpdated){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        }else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(AccountsConstants.STATUS_417, AccountsConstants.MESSAGE_417_UPDATE));
        }
    }


    @Operation(
            summary = "Delete Account",
            description = "REST API to Delete Customer & Account details based on Mobile Number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP status Ok"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "417",
                    description = "Expectation Failed"
            )
    }
    )
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteAccount(@RequestParam
                                                         @Pattern(regexp = "(^$|[0-9]{10})", message = "Account number must be 10 digits ")
                                                         String mobileNumber){
        boolean isDeleted = iAccountsService.deleteAccount(mobileNumber);
        if(isDeleted){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        }else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(AccountsConstants.STATUS_417, AccountsConstants.MESSAGE_417_DELETE));
        }
    }
}
