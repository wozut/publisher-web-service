create table tcla.action
(
    is_archived                     boolean      not null,
    assessment_id                   uuid         not null,
    id                              uuid         not null,
    challenges                      varchar(10000),
    context                         varchar(10000),
    description                     varchar(10000) not null,
    goals                           varchar(10000),
    target_questions_to_think_about text not null,
    target_tcl_drivers              text not null,
    title                           varchar(1024) not null,
    primary key (id),
    constraint fk_tcla_assessment foreign key (assessment_id) references tcla.assessment
);
