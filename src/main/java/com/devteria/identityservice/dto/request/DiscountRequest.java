package com.devteria.identityservice.dto.request;

import java.time.LocalDate;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountRequest {
    String voucherCode;
    int discountPercentage;
    LocalDate expiryDate;
}
