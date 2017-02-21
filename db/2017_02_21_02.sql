CREATE TYPE contractor_status AS ENUM ('active', 'inactive');

ALTER TABLE contractor
      ADD COLUMN IF NOT EXISTS status contractor_status NOT NULL default 'active';
