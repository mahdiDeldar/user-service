create table user_service.users
(
    id         varchar(255) not null constraint users_pkey primary key,
    nickname   varchar(255),
    first_name varchar(255),
    last_name  varchar(255),
    image_url  varchar(255),
    enabled    boolean      not null
);


create table user_service.local_users
(

    id                      varchar(255) not null
        constraint local_users_pkey primary key
        constraint fkraod9uwqw1q51rq7j0m9y6968 references user_service.users,
    email                   varchar(255) not null unique,
    password                varchar(255) not null,
    phone                   varchar(255) not null unique,
    email_verification_code varchar(255) not null,
    email_verified          boolean      not null
);



create table user_service.oauth_users
(

    id            varchar(255) not null
        constraint oauth_users_pkey primary key
        constraint fkhtfhqmfmxx65lu3son0ws8y5a references user_service.users,

    login         varchar(255) not null,
    provider      varchar(255) not null,
    provider_id   varchar(255) not null,
    provider_name varchar(255),
    otp_token     varchar(255),
    attempt       integer      not null,
    attributes    text
);



create table user_service.verification_codes
(
    id           varchar(255) not null constraint verification_codes_pkey primary key,
    phone        varchar(255) not null,
    code         varchar(255) not null,
    created_date timestamp    not null,
    attempt      integer      not null,
    expired      boolean      not null
);
