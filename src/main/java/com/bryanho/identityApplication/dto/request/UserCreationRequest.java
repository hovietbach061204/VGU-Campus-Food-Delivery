package com.bryanho.identityApplication.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

import com.bryanho.identityApplication.validator.DobConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

// @Getter // lombok will automatically generate the public getter for variables
// @Setter
@Data // lombok will automatically generate the public getter, setter, construct, ToString, HarshCode,... for variables
@NoArgsConstructor // create constructor with no required parameters
@AllArgsConstructor // create constructor requires all below variables as parameters
@Builder // create a builder class. Easier to create instance.
@FieldDefaults(level = AccessLevel.PRIVATE) // if we don't set the field for variable, private will be default
public class UserCreationRequest {
    // we don't need to paste id here because it will be automatically created
    // once we insert new user to table in database
    @Size(min = 4, message = "USERNAME_INVALID") // we need to paste the String here
    // The String "USERNAME_INVALID" is considered as a key
    // because in this @Size annotation requires constant, we can't do this:
    // ErrorCode.USERNAME_INVALID.getMessage() -> it does not accept this
    private String username;

    @Size(min = 6, message = "PASSWORD_INVALID") // indicates that the length of password when user creates new account
    // has to be min equal to 8. message will pop up when the user violates this rule.
    String password;

    String firstname;
    String lastname;
    // some more annotations: @NotNull, @NotBlank,...
    // @Email -> check for the input in the right format of an email.
    // we can also do a custom annotation for validation. For ex: the registered user has to be at least 18 years old.

    @DobConstraint(min = 18, message = "INVALID_DOB")
    LocalDate dob;
}
