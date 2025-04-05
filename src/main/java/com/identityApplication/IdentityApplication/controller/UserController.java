package com.identityApplication.IdentityApplication.controller;
// I switched to sub1 branch
// That's amazing to use git in the real project

import com.identityApplication.IdentityApplication.dto.request.APIResponse;
import com.identityApplication.IdentityApplication.dto.request.UserCreationRequest;
import com.identityApplication.IdentityApplication.dto.request.UserUpdateRequest;
import com.identityApplication.IdentityApplication.dto.response.UserResponse;
import com.identityApplication.IdentityApplication.entity.User;
import com.identityApplication.IdentityApplication.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users") // because we will use the api /users frequently so we
// declare it here so we don't need to declare it multiple times below anymore.
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {

    //@Autowired
    UserService userService;

    //@PostMapping("/users") // each endpoint (I mean this api) will receive
    // data from user. In order to map data to object, we use @RequestBody

    @PostMapping // -> because we already had @RequestMapping("/users")
    public APIResponse <User> createUser(@RequestBody @Valid UserCreationRequest request){
        // annotation @Valid indicates the framework that the object of type UserCreationRequest
        // has to be validated based on the rule that has been set in UserCreationRequest
        APIResponse<User> apiResponse = new APIResponse<>();
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }

    @GetMapping // get all users
    APIResponse<List<UserResponse>> getUsers() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return APIResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

//    @GetMapping("/{userId}")
//    // in order to map the param "userId" to a variable
//    // we use @PathVariable
//    public UserResponse getUser(@PathVariable("userId") String userId){
//        return userService.getUser(userId);
//    }

    // in order to map the param "userId" to a variable
    // we use @PathVariable
    @GetMapping("/{userId}")
    APIResponse<UserResponse> getUser(@PathVariable("userId") String userId){
        return APIResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @GetMapping("/myinfo")
    APIResponse<UserResponse> getMyInfo(){
        return APIResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }


    @PutMapping("/{userId}")
    APIResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return APIResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    APIResponse<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return APIResponse.<String>builder().result("User has been deleted").build();
    }
}
