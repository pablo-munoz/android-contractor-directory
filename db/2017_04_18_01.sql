-- The address field is to be added to the contractor table.
-- It's value can be NULL
ALTER TABLE contractor ADD COLUMN address TEXT NULL;

DROP VIEW contractor_summary;

CREATE VIEW contractor_summary AS
SELECT contractor.*, COALESCE(AVG(contractor_rating.rating), 5) as avg_rating
FROM contractor
LEFT JOIN contractor_rating on contractor.id = contractor_rating.contractor_id
GROUP BY contractor.id;
