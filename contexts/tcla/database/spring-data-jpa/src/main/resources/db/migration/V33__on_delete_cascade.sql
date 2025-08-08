alter table tcla.account_message
drop constraint fk_tcla_questionnaire,
add constraint fk_tcla_survey foreign key (questionnaire_id) references tcla.survey on delete cascade;

alter table tcla."action"
drop constraint fk_tcla_assessment,
add constraint fk_tcla_assessment foreign key (assessment_id) references tcla.assessment on delete cascade;

alter table tcla.answer
drop constraint fk_tcla_questionnaire_filling,
add constraint fk_tcla_response foreign key (questionnaire_filling_id) references tcla.response on delete cascade;

alter table tcla.answer_option
drop constraint fk_tcla_multiple_choice_question,
add constraint fk_tcla_multiple_choice_question foreign key (multiple_choice_question_id) references tcla.multiple_choice_question on delete cascade;

alter table tcla.assessment
drop constraint fk_tcla_team,
add constraint fk_tcla_team foreign key (team_id) references tcla.team on delete cascade;

alter table tcla.multiple_choice_answer
drop constraint fk_tcla_answer,
add constraint fk_tcla_answer foreign key (id) references tcla.answer on delete cascade,
drop constraint fk_tcla_answer_option,
add constraint fk_tcla_answer_option foreign key (answer_option_id) references tcla.answer_option on delete cascade;

alter table tcla.multiple_choice_question
drop constraint fk_tcla_question,
add constraint fk_tcla_question foreign key (id) references tcla.question on delete cascade;

alter table tcla.question
drop constraint fk_tcla_questionnaire,
add constraint fk_tcla_survey foreign key (questionnaire_id) references tcla.survey on delete cascade;

alter table tcla.response
drop constraint fk_tcla_questionnaire,
add constraint fk_tcla_survey foreign key (questionnaire_id) references tcla.survey on delete cascade;

alter table tcla.survey
drop constraint fk_tcla_assessment,
add constraint fk_tcla_assessment foreign key (assessment_id) references tcla.assessment on delete cascade;

alter table tcla.tcl_driver_score
drop constraint fk_tcla_assessment,
add constraint fk_tcla_assessment foreign key (assessment_id) references tcla.assessment on delete cascade;

alter table tcla.team_member
drop constraint fk_tcla_team,
add constraint fk_tcla_team foreign key (team_id) references tcla.team on delete cascade;

alter table tcla.team_member_message
drop constraint fk_tcla_questionnaire,
add constraint fk_tcla_survey foreign key (questionnaire_id) references tcla.survey on delete cascade,
drop constraint fk_tcla_team_member,
add constraint fk_tcla_team_member foreign key (team_member_id) references tcla.team_member on delete cascade;
