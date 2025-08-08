alter table tcla.team
add column organization_id uuid,
add constraint fk_tcla_organization foreign key (organization_id) references tcla.organization;
