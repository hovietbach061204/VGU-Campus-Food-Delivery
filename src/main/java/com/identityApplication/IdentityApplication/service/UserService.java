package com.identityApplication.IdentityApplication.service;


import com.identityApplication.IdentityApplication.dto.request.UserCreationRequest;
import com.identityApplication.IdentityApplication.dto.request.UserUpdateRequest;
import com.identityApplication.IdentityApplication.dto.response.UserResponse;
import com.identityApplication.IdentityApplication.entity.User;
import com.identityApplication.IdentityApplication.enums.Role;
import com.identityApplication.IdentityApplication.exception.AppException;
import com.identityApplication.IdentityApplication.exception.ErrorCode;
import com.identityApplication.IdentityApplication.mapper.UserMapper;
import com.identityApplication.IdentityApplication.repository.RoleRepository;
import com.identityApplication.IdentityApplication.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
// this annotation RequiredArgsConstructor will create constructors for variables
// that you define final
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    // before
//    @Autowired
//    // declare a Bean, in this case is Autowired to connect to UserRepository
//    // However Autowired is not recommended to inject Bean into our class.
//    private UserRepository userRepository;
//
//    @Autowired
//    private UserMapper userMapper;

    // after
    // final private by default
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    public UserResponse createUser(UserCreationRequest request){
        log.info("Service: Create User");
        if(userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED); // if the new user is created with
        // the name that already exists in database, it will cause this error


        // Mapping request to user using Map struct
        User user = userMapper.toUser(request);
        // PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10); // as the number get bigger
        // the code will be harder to break, but if the number gets too large, the system will be slow to generate this code
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
//        user.setRoles(roles);

        // without Map struct
        //User user = new User();
        //user.setUsername(request.getUsername());
        //user.setPassword(request.getPassword());
        //user.setFirstname(request.getFirstname());
        //user.setLastname(request.getLastname());
        //user.setDob(request.getDob());

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        return userMapper.toUserResponse(user);
    }





    @PreAuthorize("hasRole('ADMIN')")
    //@PreAuthorize("hasAuthority('APPROVE_POST')") -> Authorize the user who has the role APPROVE_POST is allowed to access
    public List<UserResponse> getUsers() {
        log.info("In method get Users");
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }


    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(String id){ // find user by their id, if not found, show error message
        log.info("In method get Users by Id");
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getMyInfo() {
        log.info("Get My Info activated");
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }


}
