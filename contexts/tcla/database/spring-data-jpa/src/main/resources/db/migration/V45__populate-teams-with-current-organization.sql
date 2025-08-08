INSERT INTO tcla.organization
(id, "name", industry, "size", owner_id, creation_at, last_update_at, maximum_amount_of_teams)
VALUES('87e8301e-2c09-444c-aab7-c9a069aeca6b'::uuid, '//test organization', 'Aerospace', '1 - 50 employees', 'auth0|657a5b6d4031c0a6a4b985c0', '2024-01-31 12:01:28.461', '2024-01-31 12:01:28.461', 1)
ON CONFLICT DO NOTHING;

UPDATE tcla.team t
SET owner_id = 'auth0|657a5b6d4031c0a6a4b985c0'
WHERE t.id = 'e817cc31-5492-41e9-997d-6cb5088bbebe'::uuid;

UPDATE tcla.team t
SET organization_id = tcla.organization.id from tcla.organization
WHERE t.owner_id = tcla.organization.owner_id;

ALTER TABLE tcla.team
    ALTER COLUMN organization_id SET NOT NULL;
