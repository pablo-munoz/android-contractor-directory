-- In this change script we add the portrait column
-- which will contain an url of an image of the contractor.

ALTER TABLE contractor
      ADD COLUMN IF NOT EXISTS portrait TEXT;
