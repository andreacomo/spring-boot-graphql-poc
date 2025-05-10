CREATE TABLE public.authors (
	id uuid NOT NULL,
	first_name varchar(255) NULL,
	last_name varchar(255) NULL,
	CONSTRAINT authors_pkey PRIMARY KEY (id)
);

CREATE TABLE public.books (
	id uuid NOT NULL,
	"name" varchar(255) NULL,
	page_count int4 NULL,
	author_id uuid NULL,
	CONSTRAINT books_pkey PRIMARY KEY (id),
	CONSTRAINT fkfjixh2vym2cvfj3ufxj91jem7 FOREIGN KEY (author_id) REFERENCES public.authors(id)
);

CREATE TABLE public.orders (
	id uuid NOT NULL,
	creation_date timestamptz(6) NULL,
	CONSTRAINT orders_pkey PRIMARY KEY (id)
);

CREATE TABLE public.order_details (
	id uuid NOT NULL,
	price float4 NULL,
	quantity int4 NULL,
	book_id uuid NULL,
	order_id uuid NULL,
	CONSTRAINT order_details_pkey PRIMARY KEY (id),
	CONSTRAINT fkjqe04yonp6a52rhbf2y0m03qw FOREIGN KEY (book_id) REFERENCES public.books(id),
	CONSTRAINT fkjyu2qbqt8gnvno9oe9j2s2ldk FOREIGN KEY (order_id) REFERENCES public.orders(id)
);

--- data
INSERT INTO public.authors
(id, first_name, last_name)
VALUES('450eec82-f79f-4226-a200-70f934b740f0'::uuid, 'JK', 'Rowling');

INSERT INTO public.books
(id, "name", page_count, author_id)
VALUES('450eec82-f79f-4226-a200-70f934b740f0'::uuid, 'Harry Potter', NULL, '450eec82-f79f-4226-a200-70f934b740f0'::uuid);
INSERT INTO public.books
(id, "name", page_count, author_id)
VALUES('bed7070c-ca34-4ee6-a1ac-45b68fc0cf57'::uuid, 'Animali fantastici', NULL, '450eec82-f79f-4226-a200-70f934b740f0'::uuid);

INSERT INTO public.orders
(id, creation_date)
VALUES('34742b19-68a0-4044-a3c4-4ea845289246'::uuid, '2025-05-10 20:42:10.320');
INSERT INTO public.orders
(id, creation_date)
VALUES('109e1e4c-6fd4-4588-bc7e-a15f86413f8b'::uuid, '2025-05-10 23:08:09.647');

INSERT INTO public.order_details
(id, price, quantity, book_id, order_id)
VALUES('58261688-2732-4fff-b71e-2cd9afec0e15'::uuid, 10.0, 1, '450eec82-f79f-4226-a200-70f934b740f0'::uuid, '34742b19-68a0-4044-a3c4-4ea845289246'::uuid);
INSERT INTO public.order_details
(id, price, quantity, book_id, order_id)
VALUES('cc2ce438-7d33-4698-a751-0b42b7e3dc48'::uuid, 8.0, 2, 'bed7070c-ca34-4ee6-a1ac-45b68fc0cf57'::uuid, '34742b19-68a0-4044-a3c4-4ea845289246'::uuid);
INSERT INTO public.order_details
(id, price, quantity, book_id, order_id)
VALUES('725eb1d2-de16-4fad-be2b-d12748513a72'::uuid, 1.5, 1, '450eec82-f79f-4226-a200-70f934b740f0'::uuid, '109e1e4c-6fd4-4588-bc7e-a15f86413f8b'::uuid);
INSERT INTO public.order_details
(id, price, quantity, book_id, order_id)
VALUES('beab87cd-e553-4220-b30e-a0a1b5efd0f9'::uuid, 1.5, 1, '450eec82-f79f-4226-a200-70f934b740f0'::uuid, '27089bed-3aff-42bb-8a48-128a5955a007'::uuid);
INSERT INTO public.order_details
(id, price, quantity, book_id, order_id)
VALUES('8baed88e-2499-407d-ad22-a9e73c351b97'::uuid, 1.5, 1, '450eec82-f79f-4226-a200-70f934b740f0'::uuid, 'a1ad8ce6-267e-4045-9e76-0a1bc19c9d5f'::uuid);