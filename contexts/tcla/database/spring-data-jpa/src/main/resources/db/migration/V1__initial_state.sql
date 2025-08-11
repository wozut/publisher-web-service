CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create schema if not exists tcla;

create table tcla.assessment
(
    id uuid not null,
    primary key (id)
);

create table tcla.questionnaire
(
    id                               uuid                           not null,
    external_questionnaire_id        varchar(255),
    external_questionnaire_is_public boolean                        not null,
    end_date                         timestamp(6) without time zone not null,
    start_date                       timestamp(6) without time zone not null,
    assessment_id                    uuid                           not null unique,
    primary key (id),
    constraint fk_tcla_assessment foreign key (assessment_id) references tcla.assessment
);

create table tcla.question
(
    id               uuid          not null,
    title            varchar(2048) not null,
    "order"          integer       not null,
    questionnaire_id uuid          not null,
    primary key (id),
    constraint fk_tcla_questionnaire foreign key (questionnaire_id) references tcla.questionnaire
);

create table tcla.multiple_choice_question
(
    id uuid not null,
    primary key (id),
    constraint fk_tcla_question foreign key (id) references tcla.question
);

create table tcla.answer_option
(
    id                          uuid         not null,
    "order"                     integer      not null,
    value                       varchar(255) not null,
    multiple_choice_question_id uuid         not null,
    primary key (id),
    constraint fk_tcla_multiple_choice_question foreign key (multiple_choice_question_id) references tcla.multiple_choice_question
);

create table tcla.questionnaire_filling
(
    id               uuid not null,
    questionnaire_id uuid not null,
    primary key (id),
    constraint fk_tcla_questionnaire foreign key (questionnaire_id) references tcla.questionnaire
);

create table tcla.answer
(
    id                       uuid not null,
    questionnaire_filling_id uuid not null,
    primary key (id),
    constraint fk_tcla_questionnaire_filling foreign key (questionnaire_filling_id) references tcla.questionnaire_filling
);

create table tcla.multiple_choice_answer
(
    id               uuid not null,
    answer_option_id uuid not null,
    primary key (id),
    constraint fk_tcla_answer_option foreign key (answer_option_id) references tcla.answer_option,
    constraint fk_tcla_answer foreign key (id) references tcla.answer
);

create table tcla.tcl_driver
(
    id        uuid         not null,
    name      varchar(255) not null,
    parent_id uuid,
    primary key (id),
    constraint fk_tcla_tcl_driver foreign key (parent_id) references tcla.tcl_driver
);

create table tcla.tcl_driver_score
(
    id                uuid      not null,
    value             float(53) not null,
    tcl_assessment_id uuid      not null,
    tcl_driver_id     uuid      not null,
    primary key (id),
    constraint fk_tcla_assessment foreign key (tcl_assessment_id) references tcla.assessment,
    constraint fk_tcla_tcl_driver foreign key (tcl_driver_id) references tcla.tcl_driver
);
