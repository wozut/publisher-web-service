alter table tcla.team_member_message
add column respondent_id uuid,
add constraint fk_tcla_respondent foreign key (respondent_id) references tcla.respondent;
