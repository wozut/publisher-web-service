create function tcla_table_survey_column_maximum_amount_to_be_collected_default(OUT result integer)
as '
    select count(*) as result
    from tcla.questionnaire, tcla.assessment, tcla.team_member
    where tcla.questionnaire.assessment_id = tcla.assessment.id AND tcla.team_member.team_id = tcla.assessment.team_id
    limit 1
'
language sql;

alter table tcla.questionnaire
    add column maximum_amount_to_be_collected integer not null
        default tcla_table_survey_column_maximum_amount_to_be_collected_default();

alter table tcla.questionnaire
    alter column maximum_amount_to_be_collected DROP DEFAULT;

drop function tcla_table_survey_column_maximum_amount_to_be_collected_default;

alter table tcla.questionnaire
    add column minimum_rate_required float(53) not null default 100.0;
