package com.devteria.identityservice.configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.devteria.identityservice.constant.PredefinedRole;
import com.devteria.identityservice.entity.*;
import com.devteria.identityservice.repository.*;
import com.devteria.identityservice.status.Status;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @NonFinal
    static final String ADMIN_USER_NAME = "admin";

    @NonFinal
    static final Status OFFLINE = Status.OFFLINE;

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @NonFinal
    static final String DEFAULT_PASSWORD = "12345678";

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(
            UserRepository userRepository,
            RoleRepository roleRepository,
            EateryRepository eateryRepository,
            FoodItemRepository foodItemRepository,
            PermissionRepository permissionRepository) {
        log.info("Initializing application.....");
        return args -> {
            // Create admin user logic (already present)
            if (userRepository.findByUsername(ADMIN_USER_NAME).isEmpty()) {
                roleRepository.save(Role.builder()
                        .name(PredefinedRole.USER_ROLE)
                        .description("User role")
                        .build());

                Permission order_food = Permission.builder()
                        .name("ORDER FOOD")
                        .description("Book the order")
                        .build();

                Permission accept_order = Permission.builder()
                        .name("ACCEPT ORDER")
                        .description("Accept the order")
                        .build();

                Permission cancel_order = Permission.builder()
                        .name("CANCEL ORDER")
                        .description("Cancel the order")
                        .build();

                Permission pay = Permission.builder()
                        .name("PAY")
                        .description("Pay the order")
                        .build();

                Permission comment_feedback = Permission.builder()
                        .name("COMMENT & FEEDBACK")
                        .description("Comment and feedback about the order")
                        .build();

                permissionRepository.saveAll(Set.of(order_food, accept_order, cancel_order, pay, comment_feedback));

                Role adminRole = roleRepository.save(Role.builder()
                        .name(PredefinedRole.ADMIN_ROLE)
                        .description("Admin role")
                        .build());

                Role purchaserRole = Role.builder()
                        .name("PURCHASER")
                        .description("The man books the order")
                        .build();

                purchaserRole.setPermissions(Set.of(order_food, cancel_order, pay, comment_feedback));

                Role deliveryManRole = Role.builder()
                        .name("DELIVERY MAN")
                        .description("The man delivers the order")
                        .build();

                deliveryManRole.setPermissions(Set.of(accept_order));

                var roles = Set.of(purchaserRole, deliveryManRole, adminRole);
                roles.forEach(roleRepository::save);

                var adminRoles = new HashSet<Role>();
                adminRoles.add(adminRole);

                User admin = User.builder()
                        .username(ADMIN_USER_NAME)
                        .firstName(ADMIN_USER_NAME)
                        .lastName(ADMIN_USER_NAME)
                        .dob(LocalDate.parse("2000-01-01"))
                        .status(OFFLINE)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .roles(adminRoles)
                        .build();

                userRepository.save(admin);
                log.warn("admin user has been created with default password: admin, please change it");
            }

            // Create default eateries with food items
            if (eateryRepository.count() == 0) {
                FoodItem pho = FoodItem.builder()
                        .name("PHO")
                        .price(new BigDecimal("40000"))
                        .description("Pho bo tai nam")
                        .imageUrl("pho_image")
                        .build();

                FoodItem bun_cha = FoodItem.builder()
                        .name("BUN CHA")
                        .price(new BigDecimal("35000"))
                        .description("Bun cha Ha Noi")
                        .imageUrl("bun_cha_image")
                        .build();

                FoodItem banh_cuon = FoodItem.builder()
                        .name("BANH CUON")
                        .price(new BigDecimal("30000"))
                        .description("Banh cuon nhan thit")
                        .imageUrl("banh_cuon_image")
                        .build();

                FoodItem xoi = FoodItem.builder()
                        .name("XOI")
                        .price(new BigDecimal("25000"))
                        .description("Xoi man")
                        .imageUrl("xoi_image")
                        .build();

                FoodItem banh_mi = FoodItem.builder()
                        .name("BANH MI")
                        .price(new BigDecimal("20000"))
                        .description("Banh mi thit nguoi")
                        .imageUrl("banh_mi_image")
                        .build();

                FoodItem cafe_sua = FoodItem.builder()
                        .name("CAFE SUA")
                        .price(new BigDecimal("15000"))
                        .description("Cafe sua da")
                        .imageUrl("cafe_sua_image")
                        .build();

                FoodItem hu_tieu = FoodItem.builder()
                        .name("HU TIEU")
                        .price(new BigDecimal("40000"))
                        .description("Hu tieu Nam Vang")
                        .imageUrl("hu_tieu_image")
                        .build();

                FoodItem tra_sua = FoodItem.builder()
                        .name("TRA SUA")
                        .price(new BigDecimal("25000"))
                        .description("Tra sua tran chau")
                        .imageUrl("tra_sua_image")
                        .build();

                FoodItem sinh_to = FoodItem.builder()
                        .name("SINH TO")
                        .price(new BigDecimal("25000"))
                        .description("Tra sua tran chau")
                        .imageUrl("tra_sua_image")
                        .build();

                FoodItem com_ga = FoodItem.builder()
                        .name("COM GA")
                        .price(new BigDecimal("30000"))
                        .description("Com dui ga chien")
                        .imageUrl("com_ga_image")
                        .build();

                FoodItem bun_ca = FoodItem.builder()
                        .name("BUN CA")
                        .price(new BigDecimal("30000"))
                        .description("Bun ca Quy Nhon")
                        .imageUrl("bun_ca_image")
                        .build();

                FoodItem rau_ma = FoodItem.builder()
                        .name("RAU MA SUA DUA")
                        .price(new BigDecimal("25000"))
                        .description("Rau ma sua dua")
                        .imageUrl("Rau_ma_image")
                        .build();

                // Save FoodItems to the database
                foodItemRepository.saveAll(Set.of(
                        pho, bun_cha, banh_cuon, banh_mi, xoi, sinh_to, bun_ca, cafe_sua, tra_sua, hu_tieu, com_ga,
                        rau_ma));

                Eatery abo = Eatery.builder()
                        .name("ABO")
                        .location("123 D9")
                        .contactNumber("0932036012")
                        .build();
                abo.setFoodItems(Set.of(pho, bun_cha, sinh_to, tra_sua, banh_mi, bun_ca));

                Eatery lam_phat = Eatery.builder()
                        .name("LAM PHAT")
                        .location("345 D9")
                        .contactNumber("0396822085")
                        .build();
                lam_phat.setFoodItems(Set.of(pho, bun_cha, xoi, hu_tieu, banh_cuon, com_ga));

                Eatery mai_giang = Eatery.builder()
                        .name("MAI GIANG")
                        .location("678 D9")
                        .contactNumber("0906631822")
                        .build();
                mai_giang.setFoodItems(Set.of(cafe_sua, sinh_to, xoi, hu_tieu, tra_sua));

                Eatery co_ngoc = Eatery.builder()
                        .name("CO NGOC")
                        .location("246 D9")
                        .contactNumber("0909440377")
                        .build();
                co_ngoc.setFoodItems(Set.of(sinh_to, tra_sua, rau_ma));

                var eateries = Set.of(abo, lam_phat, mai_giang, co_ngoc);
                eateries.forEach(eateryRepository::save);

                // Repeat for other eateries
                log.info("Default eateries and food items have been created.");
            }

            log.info("Application initialization completed .....");
        };
    }
}
