create table tcla.respondent
(
    id             uuid         not null,
    name           varchar(255) not null,
    email          varchar(255) not null,
    assessment_id  uuid         not null,
    creation_at    timestamp(6) without time zone not null,
    last_update_at timestamp(6) without time zone not null,
    primary key (id),
    constraint fk_tcla_assessment foreign key (assessment_id) references tcla.assessment
);
