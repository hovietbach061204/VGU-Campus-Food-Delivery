package com.devteria.identityservice.dto.request;

import java.util.Objects;

import com.devteria.identityservice.size.Size;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FoodItemOrderRequest {
    String name;
    int quantity;
    String description;
    Size size;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodItemOrderRequest that = (FoodItemOrderRequest) o;
        return Objects.equals(name, that.name) && size == that.size; // Important: compare Size too
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, size); // Combine name + size
    }
}
