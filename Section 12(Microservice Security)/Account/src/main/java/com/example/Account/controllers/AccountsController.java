package com.example.Account.controllers;

import com.example.Account.constants.AccountsConstants;
import com.example.Account.dto.*;
import com.example.Account.service.IAccountsService;
import com.example.Account.service.ITransactionService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Crud Rest APIs for accounts in MyBank",
        description = "Crud REST APIs in MyBank to CREATE, UPDATE, FETCH, DELETE accounts details and perform money operations"
)
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
public class AccountsController {

    private final IAccountsService accountsService;
    private final ITransactionService transactionService;
    private final Environment environment;
    private AccountsContactInfoDto accountsContactInfoDto;


    @Value("${build.version:1.0.0}")
    private String buildVersion;


    private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

    public AccountsController(
            IAccountsService accountsService,
            ITransactionService transactionService,
            Environment environment
//            AccountsContactInfoDto accountsContactInfoDto
    ) {
        this.accountsService = accountsService;
        this.transactionService = transactionService;
        this.environment = environment;
//        this.accountsContactInfoDto = accountsContactInfoDto;
    }

    // 1. Create Account
    @Operation(summary = "Create Account", description = "REST API to create new customer & Account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Http Status Created"),
            @ApiResponse(responseCode = "500", description = "HTTP status Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAccount(@Valid @RequestBody CustomerDto customerDto) {
        accountsService.createAccount(customerDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto(AccountsConstants.STATUS_201, AccountsConstants.MESSAGE_201));
    }

    // 2. Add Money
    @Operation(summary = "Add Money", description = "REST API to deposit/top-up money into account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Money added successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/add-money")
    public ResponseEntity<ResponseDto> addMoney(@RequestBody @Valid AddMoneyRequestDto request) {
        accountsService.addMoney(request);
        return ResponseEntity.ok(new ResponseDto("200", "Money added successfully"));
    }

    // 3. Transfer Money
    @Operation(summary = "Transfer Money", description = "REST API to transfer money to another account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer completed"),
            @ApiResponse(responseCode = "404", description = "Sender/Recipient account not found"),
            @ApiResponse(responseCode = "422", description = "Insufficient funds")
    })
    @PostMapping("/transfer")
    public ResponseEntity<ResponseDto> transferMoney(@RequestBody @Valid TransferMoneyRequestDto request) {
        accountsService.transferMoney(request);
        return ResponseEntity.ok(new ResponseDto("200", "Transfer completed"));
    }

    // 4. Account Balance Inquiry
    @Operation(summary = "Account Balance Inquiry", description = "REST API to check current account balance")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Balance fetched"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/balance")
    public ResponseEntity<AccountBalanceDto> getAccountBalance(
            @RequestParam @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits") String mobileNumber) {
        AccountBalanceDto balanceDto = accountsService.getBalance(mobileNumber);
        return ResponseEntity.ok(balanceDto);
    }

    // 5. Mini Statement / Transaction History
    @Operation(summary = "Transaction History / Mini Statement", description = "REST API to view recent transactions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions fetched"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/mini-statement")
    public ResponseEntity<List<TransactionDto>> getMiniStatement(
            @RequestParam @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits") String mobileNumber,
            @RequestParam(defaultValue = "10") int limit) {
        List<TransactionDto> transactions = transactionService.getMiniStatement(mobileNumber, limit);
        return ResponseEntity.ok(transactions);
    }

    // 6. Freeze/Unfreeze Account
    @Operation(summary = "Freeze/Unfreeze Account", description = "REST API to freeze or unfreeze an account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operation successful")
    })
    @PutMapping("/freeze-unfreeze")
    public ResponseEntity<ResponseDto> freezeUnfreeze(@RequestBody @Valid FreezeUnfreezeRequestDto request) {
        accountsService.freezeOrUnfreeze(request);
        return ResponseEntity.ok(new ResponseDto("200", "Account status updated"));
    }

    // 7. Change Account Type
    @Operation(summary = "Change Account Type", description = "REST API to change the type of an account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Type changed")
    })
    @PutMapping("/change-type")
    public ResponseEntity<ResponseDto> changeAccountType(@RequestBody @Valid ChangeAccountTypeRequestDto request) {
        accountsService.changeAccountType(request);
        return ResponseEntity.ok(new ResponseDto("200", "Account type updated"));
    }

    // 8. Fetch Full Account Details (existing)
    @Operation(summary = "Fetch Account Details", description = "REST API to fetch Customer & Account details by Mobile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP status Ok"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/fetch")
    public ResponseEntity<CustomerDetailsDto> fetchAccountDetails(
            @RequestParam @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits") String mobileNumber) {
        CustomerDetailsDto details = accountsService.fetchAccount(mobileNumber);
        return ResponseEntity.ok(details);
    }

    // 9. Update Account Details (existing)
    @Operation(summary = "Update Account Details", description = "REST API to update Customer & Account details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP status Ok"),
            @ApiResponse(responseCode = "417", description = "Expectation Failed")
    })
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateAccountDetails(@Valid @RequestBody CustomerDetailsDto customerDetailsDto) {
        boolean isUpdated = accountsService.updateAccount(customerDetailsDto);
        if(isUpdated){
            return ResponseEntity.ok(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
        }else {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(AccountsConstants.STATUS_417, AccountsConstants.MESSAGE_417_UPDATE));
        }
    }

    // 10. Delete Account (existing)
    @Operation(summary = "Delete Account", description = "REST API to Delete Customer & Account by Mobile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP status Ok"),
            @ApiResponse(responseCode = "417", description = "Expectation Failed")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteAccount(@RequestParam
                                                     @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits ")
                                                     String mobileNumber){
        boolean isDeleted = accountsService.deleteAccount(mobileNumber);
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


    @Operation(
            summary = "Get Application's Build Information",
            description = "Get Build Information that is deployed into account microservice"
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
    @Retry(name = "getBuildInfo", fallbackMethod = "getBuildVersionFallback")
    @GetMapping("/build-info")
    public ResponseEntity<String> getBuildVersion(){
        logger.debug("getBuildInfo() Method Invoked ");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(buildVersion);
    }
    public ResponseEntity<String> getBuildVersionFallback(Throwable throwable){
        logger.debug("getBuildInfoFallback() Method Invoked ");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Its a Fallback response: 0.9");
    }

    @Operation(
            summary = "Get java version Information",
            description = "Get java version Information that is Used into account microservice"
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
    @RateLimiter(name = "getJavaVersion", fallbackMethod = "getJavaVersionFallback")
    @GetMapping("/java-version")
    public ResponseEntity<String> getJavaVersion(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(environment.getProperty("JAVA_HOME"));
    }

    public ResponseEntity<String> getJavaVersionFallback(Throwable throwable){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Oyeeee Thoda slow Java Version is 17");
    }

    @Operation(
            summary = "Get Contect Information",
            description = "Contact Information that can be reached out in case of any issues"
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
    @GetMapping("/contact-info")
    public ResponseEntity<AccountsContactInfoDto> getContectInfo(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountsContactInfoDto);
    }
}
