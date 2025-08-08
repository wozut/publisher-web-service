alter table tcla.account_message
    add column extra_data varchar(255) not null default '{}';

alter table tcla.team_member_message
    add column extra_data varchar(255) not null default '{}';
