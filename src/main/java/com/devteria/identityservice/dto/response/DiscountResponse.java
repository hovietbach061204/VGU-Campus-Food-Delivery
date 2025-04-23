package com.devteria.identityservice.dto.response;

import java.time.LocalDate;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountResponse {
    String voucherCode;
    int discountPercentage;
    LocalDate expiryDate;
}
