CREATE TABLE IF NOT EXISTS contractor_rating (
      account_id UUID NOT NULL REFERENCES account(id),
      contractor_id UUID NOT NULL REFERENCES contractor(id),
      rating FLOAT NOT NULL,
      PRIMARY KEY (account_id, contractor_id)
);
