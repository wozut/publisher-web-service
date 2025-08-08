alter table tcla.assessment
    add column team_name varchar(255),
    add column team_time_zone varchar(255);

UPDATE tcla.assessment a
SET team_name      = tcla.team.name,
    team_time_zone = tcla.team.time_zone FROM tcla.team
where a.team_id = tcla.team.id;
