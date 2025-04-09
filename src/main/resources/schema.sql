-- we don't know how to generate root <with-no-name> (class Root) :(

comment on database postgres is 'default administrative connection database';

create table "UserRole"
(
    role_id   integer     not null
        primary key,
    role_name varchar(50) not null
        unique
);

alter table "UserRole"
    owner to postgres;

create table "OrderStatus"
(
    status_id   integer     not null
        primary key,
    status_name varchar(50) not null
        unique
);

alter table "OrderStatus"
    owner to postgres;

create table "Users"
(
    user_id         serial
        primary key,
    username        varchar(50)                            not null
        unique,
    password_hash   varchar(255)                           not null,
    email           varchar(100)                           not null
        unique,
    phone_number    varchar(10),
    role_id         integer                                not null
        references "UserRole"
            on update cascade on delete restrict,
    profile_picture bytea,
    created_at      timestamp with time zone default now() not null,
    updated_at      timestamp with time zone default now() not null
);

alter table "Users"
    owner to postgres;

create table "Discount"
(
    discount_id         integer     not null
        primary key,
    voucher_code        varchar(50) not null
        unique,
    discount_percentage integer     not null,
    expiry_date         date        not null
);

alter table "Discount"
    owner to postgres;

create table "Eatery"
(
    eatery_id      integer      not null
        primary key,
    name           varchar(100) not null,
    location       varchar(255) not null,
    contact_number varchar(15)
);

alter table "Eatery"
    owner to postgres;

create table "FoodItem"
(
    food_item_id integer        not null
        primary key,
    name         varchar(100)   not null,
    description  text,
    price        numeric(10, 2) not null,
    image_url    varchar(255),
    eatery_id    integer        not null
        references "Eatery"
            on update cascade on delete set null
);

alter table "FoodItem"
    owner to postgres;

create table "Orders"
(
    order_id        integer        not null
        primary key,
    purchaser_id    integer        not null
        references "Users"
            on update cascade on delete set null,
    delivery_man_id integer
                                   references "Users"
                                       on update cascade on delete set null,
    status_id       integer        not null
        references "OrderStatus"
            on update cascade on delete restrict,
    total_price     numeric(10, 2) not null,
    discount_id     integer        not null
        references "Discount"
            on update cascade on delete set null,
    created_at      timestamp with time zone default now(),
    updated_at      timestamp with time zone default now()
);

alter table "Orders"
    owner to postgres;

create table "OrderDetails"
(
    order_detail_id integer        not null
        primary key,
    order_id        integer        not null
        references "Orders"
            on update cascade on delete cascade,
    food_item_id    integer        not null
        references "FoodItem"
            on update cascade on delete set null,
    quantity        integer        not null,
    price           numeric(10, 2) not null
);

alter table "OrderDetails"
    owner to postgres;

create table "Chat"
(
    chat_id   integer                                not null
        primary key,
    order_id  integer                                not null
        references "Orders"
            on update cascade on delete cascade,
    user_id   integer                                not null
        references "Users"
            on update cascade on delete set null,
    message   text                                   not null,
    timestamp timestamp with time zone default now() not null
);

alter table "Chat"
    owner to postgres;

create table "DeliveryTracking"
(
    tracking_id      integer      not null
        primary key,
    order_id         integer      not null
        references "Orders"
            on update cascade on delete cascade,
    current_location varchar(255) not null,
    estimated_time   varchar(50),
    status_update    timestamp with time zone default now()
);

alter table "DeliveryTracking"
    owner to postgres;

create table "SystemLog"
(
    log_id            integer                                not null
        primary key,
    event_description text                                   not null,
    timestamp         timestamp with time zone default now() not null,
    user_id           integer
                                                             references "Users"
                                                                 on update cascade on delete set null
);

alter table "SystemLog"
    owner to postgres;

create table userrole
(
    role_id   serial
        primary key,
    role_name varchar(50) not null
        unique
);

alter table userrole
    owner to postgres;

create table orderstatus
(
    status_id   serial
        primary key,
    status_name varchar(50) not null
        unique
);

alter table orderstatus
    owner to postgres;

create table users
(
    user_id         serial
        primary key,
    username        varchar(50)                            not null
        unique,
    password_hash   varchar(255)                           not null,
    email           varchar(100)                           not null
        unique,
    phone_number    varchar(10),
    profile_picture bytea,
    created_at      timestamp with time zone default now() not null,
    updated_at      timestamp with time zone default now() not null
);

alter table users
    owner to postgres;

create table discount
(
    discount_id         serial
        primary key,
    voucher_code        varchar(50) not null
        unique,
    discount_percentage integer     not null,
    expiry_date         date        not null
);

alter table discount
    owner to postgres;

create table eatery
(
    eatery_id      serial
        primary key,
    name           varchar(100) not null,
    location       varchar(255) not null,
    contact_number varchar(15)
);

alter table eatery
    owner to postgres;

create table fooditem
(
    food_item_id serial
        primary key,
    name         varchar(100)                not null,
    description  text,
    price        numeric(10, 2) default 0.00 not null,
    image_url    varchar(255),
    eatery_id    integer                     not null
        references eatery
            on update cascade on delete set null
);

alter table fooditem
    owner to postgres;

create table orders
(
    order_id        serial
        primary key,
    purchaser_id    integer        not null
        references users
            on update cascade on delete set null,
    delivery_man_id integer
                                   references users
                                       on update cascade on delete set null,
    status_id       integer        not null
        references orderstatus
            on update cascade on delete restrict,
    total_price     numeric(10, 2) not null,
    discount_id     integer
                                   references discount
                                       on update cascade on delete set null,
    created_at      timestamp with time zone default now(),
    updated_at      timestamp with time zone default now()
);

alter table orders
    owner to postgres;

create table orderdetails
(
    order_detail_id serial
        primary key,
    order_id        integer                     not null
        references orders
            on update cascade on delete cascade,
    food_item_id    integer                     not null
        references fooditem
            on update cascade on delete set null,
    quantity        integer                     not null,
    price           numeric(10, 2) default 0.00 not null
);

alter table orderdetails
    owner to postgres;

create table chat
(
    chat_id   serial
        primary key,
    order_id  integer                                not null
        references orders
            on update cascade on delete cascade,
    user_id   integer                                not null
        references users
            on update cascade on delete set null,
    message   text                                   not null,
    timestamp timestamp with time zone default now() not null
);

alter table chat
    owner to postgres;

create table deliverytracking
(
    tracking_id       serial
        primary key,
    order_id          integer      not null
        references orders
            on update cascade on delete cascade,
    current_location  varchar(255) not null,
    estimated_time    varchar(50),
    delivery_location varchar(255) not null,
    status_update     timestamp with time zone default now()
);

alter table deliverytracking
    owner to postgres;

create table systemlog
(
    log_id            serial
        primary key,
    event_description text                                   not null,
    timestamp         timestamp with time zone default now() not null,
    user_id           integer
                                                             references users
                                                                 on update cascade on delete set null
);

alter table systemlog
    owner to postgres;

create table userroles
(
    user_id     integer not null
        references users
            on update cascade on delete cascade,
    role_id     integer not null
        references userrole
            on update cascade on delete cascade,
    assigned_at timestamp with time zone default now(),
    primary key (user_id, role_id)
);

alter table userroles
    owner to postgres;

create table deliveryrequest
(
    request_id      serial
        primary key,
    order_id        integer     not null
        references orders
            on update cascade on delete cascade,
    delivery_man_id integer     not null
        references users
            on update cascade on delete cascade,
    status          varchar(25) not null
        constraint deliveryrequest_status_check
            check ((status)::text = ANY
                   ((ARRAY ['pending'::character varying, 'accepted'::character varying, 'declined'::character varying])::text[])),
    requested_at    timestamp with time zone default now()
);

alter table deliveryrequest
    owner to postgres;

create table orderstatuslogs
(
    log_id     serial
        primary key,
    order_id   integer not null
        references orders
            on update cascade on delete cascade,
    status_id  integer not null
        references orderstatus
            on update cascade on delete cascade,
    changed_at timestamp with time zone default now()
);

alter table orderstatuslogs
    owner to postgres;

create table notification
(
    notification_id serial
        primary key,
    user_id         integer      not null
        references users
            on update cascade on delete cascade,
    title           varchar(100) not null,
    content         text         not null,
    is_read         boolean                  default false,
    sent_at         timestamp with time zone default now()
);

alter table notification
    owner to postgres;

