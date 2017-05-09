CREATE TABLE IF NOT EXISTS favorites (
       id              UUID PRIMARY KEY default uuid_generate_v4(),
       account_id      UUID NOT NULL REFERENCES account(id),
       contractor_id   UUID NOT NULL REFERENCES contractor(id),
       date_created    TIMESTAMP default now(),
       unique (account_id, contractor_id)
);


ALTER TABLE contractor ADD COLUMN account_id UUID NOT NULL REFERENCES account(id);
