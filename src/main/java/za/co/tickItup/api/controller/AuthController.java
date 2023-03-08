package za.co.tickItup.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.tickItup.api.entity.UserProfile;
import za.co.tickItup.api.request.LoginRequest;
import za.co.tickItup.api.response.ResponseHandler;
import za.co.tickItup.api.service.UserProfileService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserProfileService userProfileService;

    @Operation(summary = "New user", description = "", tags = {"auth"})
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = UserProfile.class)))})
    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody UserProfile userProfile) {
        userProfileService.createProfile(userProfile);
        return ResponseHandler.responseBuilder("Signed up successful ", HttpStatus.CREATED, null);
    }

    @Operation(summary = "login user", description = "", tags = {"auth"})
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = LoginRequest.class)))})
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {

        return ResponseHandler.responseBuilder(null, HttpStatus.OK, userProfileService.login(loginRequest));
    }

}
