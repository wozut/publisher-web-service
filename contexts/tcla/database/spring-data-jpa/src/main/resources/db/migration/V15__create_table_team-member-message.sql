create table tcla.team_member_message
(
    actually_sent_at        timestamp(6),
    scheduled_to_be_sent_at timestamp(6) not null,
    id                      uuid         not null,
    questionnaire_id        uuid         not null,
    team_member_id          uuid         not null,
    channel                 varchar(255) not null,
    type                    varchar(255) not null,
    primary key (id),
    constraint fk_tcla_questionnaire foreign key (questionnaire_id) references tcla.questionnaire,
    constraint fk_tcla_team_member foreign key (team_member_id) references tcla.team_member
);
