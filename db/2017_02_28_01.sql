--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.1
-- Dumped by pg_dump version 9.6.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


--
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


--
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;


--
-- Name: EXTENSION "uuid-ossp"; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


SET search_path = public, pg_catalog;

--
-- Name: contractor_status; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE contractor_status AS ENUM (
    'active',
    'inactive'
);


ALTER TYPE contractor_status OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: account; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE account (
    id uuid DEFAULT uuid_generate_v4() NOT NULL,
    email text NOT NULL,
    password text NOT NULL,
    date_created date DEFAULT now() NOT NULL
);


ALTER TABLE account OWNER TO postgres;

--
-- Name: contractor; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE contractor (
    id uuid DEFAULT uuid_generate_v4() NOT NULL,
    first_name text NOT NULL,
    middle_name text,
    last_names text NOT NULL,
    phone text NOT NULL,
    email text,
    website text,
    description text,
    date_created date DEFAULT now() NOT NULL,
    portrait text,
    status contractor_status DEFAULT 'active'::contractor_status NOT NULL
);


ALTER TABLE contractor OWNER TO postgres;

--
-- Name: contractor_category; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE contractor_category (
    id uuid DEFAULT uuid_generate_v4() NOT NULL,
    name text NOT NULL,
    short_name text NOT NULL,
    img text NOT NULL,
    date_created date DEFAULT now() NOT NULL
);


ALTER TABLE contractor_category OWNER TO postgres;

--
-- Name: contractor_category_map; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE contractor_category_map (
    id uuid DEFAULT uuid_generate_v4() NOT NULL,
    contractor_category_id uuid NOT NULL,
    contractor_id uuid NOT NULL,
    date_created date DEFAULT now() NOT NULL
);


ALTER TABLE contractor_category_map OWNER TO postgres;

--
-- Data for Name: account; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY account (id, email, password, date_created) FROM stdin;
a221a051-6387-4ef0-87f0-0ebe4ed859be	pablosdesk@gmail.com	$2a$08$1GvlX70z50BDiKnhokmuHeIV1AC7K9B80OJTFTVo08D/kCteCiaGS	2017-02-22
c707d9a0-3734-4d60-8c9c-ec80a10bcaec	a01222422@itesm.mx	$2a$08$FfmmyHdTvEp8atUQJuYK5.t0at7IxFTGJgJPwpc9bw9bn1IZ5lWxO	2017-02-27
\.


--
-- Data for Name: contractor; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY contractor (id, first_name, middle_name, last_names, phone, email, website, description, date_created, portrait, status) FROM stdin;
401c04fb-0609-4250-9596-beb37eecc040	Francisco	Javier	Tejero Silva	333-111-222-33	fjt@gmail.com	\N	\N	2017-02-28	\N	active
8c702c98-3174-42a9-a17d-ab0686cc6d45	Agustín		Artola Monteiro	333-444-555-66	aam@gmail.com	\N	\N	2017-02-28	\N	active
7128ec6b-4410-4333-918b-c8fc338db114	Salvador		Cruzado Montalbán	333-777-88-99	scm@gmail.com	\N	\N	2017-02-28	\N	active
58eeb2cb-50a4-4e93-89d1-697fa947444c	María	Victoria	Quevedo Pavón	333-123-456-78	marivi@gmail.com	\N	\N	2017-02-28	\N	active
2135eebb-4485-42e1-9da2-e272b49ac0d9	Pablo		Muñoz Haro	333-987-654-32	pablosdesk@gmail.com	\N	\N	2017-02-28	\N	active
3e72e99b-65c7-40ac-86d5-f0c567e5b22a	David		Mercado Magaña	333-999-888-77	maga@gmail.com	\N	\N	2017-02-28	\N	active
\.


--
-- Data for Name: contractor_category; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY contractor_category (id, name, short_name, img, date_created) FROM stdin;
966f6a1b-1f83-4f16-9d58-cfd08c06b1f4	Electricistas	elec	http://www.electricistasalcala.com/IMAGENES/electricistas-en-alcala.jpg	2017-02-07
9d1dae3f-9576-4469-8327-ca6d08a3d5f4	Contadores	acc	http://elpoderdelosnumeros.org/wp-content/uploads/2014/05/25-de-Mayo-D%C3%ADa-del-Contador.jpg	2017-02-14
b74fb86c-b10c-4333-81e4-730f7124816d	Pintores	paint	https://aos.iacpublishinglabs.com/question/aq/700px-394px/painters-wear-white-clothing_be53e73e4b01a054.jpg?domain=cx.aos.ask.com	2017-02-21
18f22833-496b-4347-a1c3-d7f84a7906d3	Albañiles	alb	https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTy9Bo6aSoF6OXnBgYYRc_BkFZJA-fReO_jhPDoMwcI0XHVzwVIeA	2017-02-20
\.


--
-- Data for Name: contractor_category_map; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY contractor_category_map (id, contractor_category_id, contractor_id, date_created) FROM stdin;
83a8ee5b-9d90-41f1-af8b-9c8c37af3d2d	966f6a1b-1f83-4f16-9d58-cfd08c06b1f4	401c04fb-0609-4250-9596-beb37eecc040	2017-02-28
cf19d02b-204a-43b6-ac50-859fbe6992c4	966f6a1b-1f83-4f16-9d58-cfd08c06b1f4	8c702c98-3174-42a9-a17d-ab0686cc6d45	2017-02-28
5ab1c436-58c6-40e3-8d64-eb28fbb03729	966f6a1b-1f83-4f16-9d58-cfd08c06b1f4	7128ec6b-4410-4333-918b-c8fc338db114	2017-02-28
bfa9716b-d38c-4198-8701-a675ae921535	9d1dae3f-9576-4469-8327-ca6d08a3d5f4	58eeb2cb-50a4-4e93-89d1-697fa947444c	2017-02-28
5d92d60e-1c31-40d1-8540-c0e389fa5741	9d1dae3f-9576-4469-8327-ca6d08a3d5f4	2135eebb-4485-42e1-9da2-e272b49ac0d9	2017-02-28
19490402-ced0-476c-b1a4-6db34376d28d	b74fb86c-b10c-4333-81e4-730f7124816d	3e72e99b-65c7-40ac-86d5-f0c567e5b22a	2017-02-28
\.


--
-- Name: account account_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY account
    ADD CONSTRAINT account_email_key UNIQUE (email);


--
-- Name: account account_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY account
    ADD CONSTRAINT account_pkey PRIMARY KEY (id);


--
-- Name: contractor_category_map contractor_category_map_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY contractor_category_map
    ADD CONSTRAINT contractor_category_map_pkey PRIMARY KEY (id);


--
-- Name: contractor_category contractor_category_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY contractor_category
    ADD CONSTRAINT contractor_category_name_key UNIQUE (name);


--
-- Name: contractor_category contractor_category_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY contractor_category
    ADD CONSTRAINT contractor_category_pkey PRIMARY KEY (id);


--
-- Name: contractor_category contractor_category_short_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY contractor_category
    ADD CONSTRAINT contractor_category_short_name_key UNIQUE (short_name);


--
-- Name: contractor contractor_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY contractor
    ADD CONSTRAINT contractor_pkey PRIMARY KEY (id);


--
-- Name: account_lower_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX account_lower_idx ON account USING btree (lower(email));


--
-- Name: account_lower_idx1; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX account_lower_idx1 ON account USING btree (lower(email));


--
-- Name: account_lower_idx2; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX account_lower_idx2 ON account USING btree (lower(email));


--
-- Name: account_lower_idx3; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX account_lower_idx3 ON account USING btree (lower(email));


--
-- Name: account_lower_idx4; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX account_lower_idx4 ON account USING btree (lower(email));


--
-- Name: contractor_category_map contractor_category_map_contractor_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY contractor_category_map
    ADD CONSTRAINT contractor_category_map_contractor_category_id_fkey FOREIGN KEY (contractor_category_id) REFERENCES contractor_category(id);


--
-- Name: contractor_category_map contractor_category_map_contractor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY contractor_category_map
    ADD CONSTRAINT contractor_category_map_contractor_id_fkey FOREIGN KEY (contractor_id) REFERENCES contractor(id);


--
-- PostgreSQL database dump complete
--

