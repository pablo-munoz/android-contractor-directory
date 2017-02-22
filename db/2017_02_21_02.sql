CREATE TYPE contractor_status AS ENUM ('active', 'inactive');

ALTER TABLE contractor
      ADD COLUMN IF NOT EXISTS status contractor_status NOT NULL default 'active';

CREATE EXTENSION pgcrypto;

CREATE TABLE IF NOT EXISTS account (
       id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
       email        TEXT NOT NULL UNIQUE,
       password     TEXT NOT NULL, 
       date_created DATE NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX ON account ((lower(email)));
