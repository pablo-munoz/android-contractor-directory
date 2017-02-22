-- Extension to generate unique ids
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS contractor_category (
       id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
       name         TEXT NOT NULL UNIQUE, -- The name of the category e.g. plumbers
       short_name   TEXT NOT NULL UNIQUE, -- A short name about the category .e.g. "plmb"
       img          TEXT NOT NULL, -- An image that can be displayed in the ui about the category
       date_created DATE NOT NULL DEFAULT now()
);
