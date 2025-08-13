create table tcla.team
(
    id        uuid         not null,
    time_zone varchar(255) not null,
    owner_id  varchar(255) not null,
    primary key (id)
);
