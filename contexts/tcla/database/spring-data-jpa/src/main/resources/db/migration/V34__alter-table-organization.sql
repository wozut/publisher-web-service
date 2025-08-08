UPDATE tcla.organization
SET "name"='ACME'
WHERE "name" is null;

UPDATE tcla.organization
SET industry='Software Engineering'
WHERE industry is null;

UPDATE tcla.organization
SET "size"='1-10 employees'
WHERE "size" is null;

alter table tcla.organization
alter column "name" set not null,
alter column industry set not null,
alter column "size" set not null;
