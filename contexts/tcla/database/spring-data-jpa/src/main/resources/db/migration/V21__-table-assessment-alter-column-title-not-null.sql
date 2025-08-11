update tcla.assessment set title = 'Assessment' where title is null;

alter table tcla.assessment
    alter column title set not null;
