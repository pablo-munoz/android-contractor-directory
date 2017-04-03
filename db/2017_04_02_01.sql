CREATE TABLE contractor_comment (
      id UUID NOT NULL default uuid_generate_v4(),
      account_id UUID NOT NULL REFERENCES account(id),
      contractor_id UUID NOT NULL REFERENCES contractor(id),
      content TEXT NOT NULL,
      parent UUID default NULL,
      date_created DATE NOT NULL default now()
);


ALTER TABLE contractor_comment ALTER COLUMN date_created TYPE TIMESTAMP;
