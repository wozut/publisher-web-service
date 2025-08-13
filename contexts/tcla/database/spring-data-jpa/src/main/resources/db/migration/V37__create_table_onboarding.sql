create schema if not exists analytics;

create table analytics.onboarding
(
    user_has_seen_demo boolean      not null,
    id                 uuid         not null,
    owner_id           varchar(255) not null,
    primary key (id)
);
