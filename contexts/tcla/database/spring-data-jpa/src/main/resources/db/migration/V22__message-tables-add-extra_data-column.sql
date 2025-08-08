alter table tcla.questionnaire_filling
    add column external_id text not null default 'undefined';
