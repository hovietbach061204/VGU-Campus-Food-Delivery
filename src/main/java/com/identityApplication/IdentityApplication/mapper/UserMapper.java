package com.identityApplication.IdentityApplication.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.identityApplication.IdentityApplication.dto.request.UserCreationRequest;
import com.identityApplication.IdentityApplication.dto.request.UserUpdateRequest;
import com.identityApplication.IdentityApplication.dto.response.UserResponse;
import com.identityApplication.IdentityApplication.entity.User;

// @Mapper(componentModel = "spring") // we notify the map struct that we generate this map
// in spring
// public interface UserMapper {
//    User toUser(UserCreationRequest request);
//    //this will receive request with type UserCreationRequest
//    // and will return to class User.
//
//    //some features of Map struct we can use:
//
//    //map firstname and lastname. Set firstname = lastname
//    //@Mapping(source = "firstname", target = "lastname")
//    // or
//    //@Mapping(target = "lastname", ignore = true)
//
//    UserResponse toUserResponse(User user);
//    // we define we will map the data from UserUpdateRequest to object User
//    @Mapping(target = "roles", ignore = true)
//    void updateUser(@MappingTarget User user, UserUpdateRequest request);
// }

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
