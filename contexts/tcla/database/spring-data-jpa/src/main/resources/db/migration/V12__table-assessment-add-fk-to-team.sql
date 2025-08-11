INSERT INTO tcla.team
(id, time_zone, owner_id, name)
VALUES ('e817cc31-5492-41e9-997d-6cb5088bbebe', 'Africa/Abidjan', 'sadnksdmn','Manually set');

UPDATE tcla.assessment
SET team_id = 'e817cc31-5492-41e9-997d-6cb5088bbebe' WHERE team_id is NULL;

ALTER TABLE tcla.assessment ALTER COLUMN team_id SET NOT NULL;

ALTER TABLE tcla.assessment ADD CONSTRAINT fk_tcla_team FOREIGN KEY (team_id) REFERENCES tcla.team;
