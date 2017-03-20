CREATE TABLE IF NOT EXISTS contractor_rating (
      account_id UUID NOT NULL REFERENCES account(id),
      contractor_id UUID NOT NULL REFERENCES contractor(id),
      rating FLOAT NOT NULL,
      PRIMARY KEY (account_id, contractor_id)
);


CREATE VIEW contractor_summary AS
SELECT contractor.*, COALESCE(AVG(contractor_rating.rating), 5) as avg_rating
FROM contractor LEFT JOIN contractor_rating on contractor.id = contractor_rating.contractor_id
GROUP BY contractor.id;
