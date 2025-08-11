alter table tcla.team
    add column creation_at timestamp(6) without time zone not null default NOW(),
    add column last_update_at timestamp(6) without time zone not null default NOW();

alter table tcla.organization
    add column creation_at timestamp(6) without time zone not null default NOW(),
    add column last_update_at timestamp(6) without time zone not null default NOW();

alter table tcla.team_member
    add column creation_at timestamp(6) without time zone not null default NOW(),
    add column last_update_at timestamp(6) without time zone not null default NOW();

alter table tcla.assessment
    add column creation_at timestamp(6) without time zone not null default NOW(),
    add column last_update_at timestamp(6) without time zone not null default NOW();

alter table tcla.questionnaire
    add column creation_at timestamp(6) without time zone not null default NOW(),
    add column last_update_at timestamp(6) without time zone not null default NOW();

alter table tcla.action
    add column creation_at timestamp(6) without time zone not null default NOW(),
    add column last_update_at timestamp(6) without time zone not null default NOW();

alter table tcla.tcl_driver_score
    add column creation_at timestamp(6) without time zone not null default NOW(),
    add column last_update_at timestamp(6) without time zone not null default NOW();
