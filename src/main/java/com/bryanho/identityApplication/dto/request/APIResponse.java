package com.bryanho.identityApplication.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // this means when the field is null
// it will not be displayed in the response when under the json format.
// Normalize the responses
public class APIResponse<T> {
    @Builder.Default
    int code = 1000; // if the code == 1000, this api successes

    String message;
    T result; // T means the type is mutable, depends on each api.
}
