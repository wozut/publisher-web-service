CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE OR REPLACE FUNCTION generate_uid(size INT) RETURNS TEXT AS $$
DECLARE
characters TEXT := 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  bytes BYTEA := gen_random_bytes(size);
  l INT := length(characters);
  i INT := 0;
output TEXT := '';
BEGIN
  WHILE i < size LOOP
    output := output || substr(characters, get_byte(bytes, i) % l + 1, 1);
    i := i + 1;
END LOOP;
  RETURN output;
END;
$$ LANGUAGE plpgsql VOLATILE;

alter table tcla.assessment
    add column results_shareable_token text unique not null default generate_uid(256);

alter table tcla.assessment
    alter column results_shareable_token drop default;

drop function generate_uid;

DROP EXTENSION pgcrypto;
