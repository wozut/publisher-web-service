create table tcla.organization
(
    id       uuid         not null,
    name     varchar(255),
    industry varchar(255),
    size     varchar(255),
    owner_id varchar(255) not null,
    primary key (id)
);
