create table tcla.team_member
(
    id      uuid         not null,
    name    varchar(255) not null,
    email   varchar(255) not null,
    team_id uuid         not null,
    primary key (id),
    constraint fk_tcla_team foreign key (team_id) references tcla.team
);
