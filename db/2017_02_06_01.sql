CREATE TABLE IF NOT EXISTS contractor (
       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
       first_name TEXT NOT NULL,
       middle_name TEXT,
       last_names TEXT NOT NULL,
       phone TEXT NOT NULL,
       email TEXT,
       website TEXT,
       description TEXT,
       date_created DATE NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS contractor_category_map (
       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
       contractor_category_id UUID NOT NULL references contractor_category (id),
       contractor_id UUID NOT NULL references contractor (id),
       date_created DATE NOT NULL DEFAULT now()
);
