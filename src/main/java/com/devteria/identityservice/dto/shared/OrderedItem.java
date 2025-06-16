package com.devteria.identityservice.dto.shared;

import java.math.BigDecimal;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"name", "size"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderedItem {
    String name;
    String description;
    String size;
    int quantity;
    BigDecimal price;
}
