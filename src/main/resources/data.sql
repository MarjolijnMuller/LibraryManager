-- Insert roles
INSERT INTO roles (rolename)
VALUES ('ROLE_MEMBER'),
       ('ROLE_LIBRARIAN');

-- Insert books
INSERT INTO books (title, author_first_name, author_last_name, isbn, publisher, category)
VALUES ('The Hitchhiker''s Guide to the Galaxy', 'Douglas', 'Adams', '9780345391803', 'Del Rey', 'FICTION'),
       ('The Name of the Wind', 'Patrick', 'Rothfuss', '9780756404741', 'DAW Books', 'FICTION'),
       ('Thinking, Fast and Slow', 'Daniel', 'Kahneman', '9780374533557', 'Farrar, Straus and Giroux', 'NON_FICTION'),
       ('The Gruffalo', 'Julia', 'Donaldson', '9780333905581', 'Macmillan Children''s Books', 'CHILDRENS_BOOK'),
       ('Where the Crawdads Sing', 'Delia', 'Owens', '9780735219090', 'G.P. Putnam''s Sons', 'ADULTS_BOOK'),
       ('Cosmos', 'Carl', 'Sagan', '9780345539434', 'Ballantine Books', 'SCIENCE'),
       ('A People''s History of the United States', 'Howard', 'Zinn', '9780060528379', 'Harper Perennial', 'HISTORY');

-- Insert book copies
INSERT INTO book_copies (book_id, follow_number, status)
VALUES (1, 1, 'AVAILABLE'),
       (1, 2, 'ON_LOAN'),
       (2, 1, 'AVAILABLE'),
       (3, 1, 'DAMAGED'),
       (3, 2, 'AVAILABLE'),
       (3, 3, 'ON_LOAN'),
       (3, 4, 'AVAILABLE'),
       (3, 5, 'AVAILABLE'),
       (4, 1, 'AVAILABLE'),
       (5, 1, 'MISSING'),
       (6, 1, 'AVAILABLE'),
       (7, 1, 'IN_REPAIR');

-- Insert users
INSERT INTO users (user_id, username, password)
VALUES (101, 'emilylib', 'passwordEmily'),
       (102, 'davidlib', 'passwordDavid'),
       (103, 'sarahlib', 'passwordSarah'),
       (104, 'chrislib', 'passwordChris'),
       (105, 'lisalib', 'passwordLisa'),
       (201, 'adamember', 'passwordAdam'),
       (202, 'ellenmember', 'passwordEllen'),
       (203, 'mikeymember', 'passwordMikey'),
       (204, 'oliviarmember', 'passwordOlivia'),
       (205, 'petermember', 'passwordPeter');

-- Insert users_information
INSERT INTO users_information (user_information_id, user_id, first_name, last_name, street, house_number, postal_code, city, email, phone, profile_picture_url)
VALUES (1, 101, 'Emily', 'Clark', 'Bibliotheeklaan', '10', '1234AB', 'Amsterdam', 'emily.clark@example.com', '06-12345678', 'http://example.com/images/emily.jpg'),
       (2, 102, 'David', 'Jones', 'Leesplein', '5', '5678CD', 'Utrecht', 'david.jones@example.com', '06-87654321', 'http://example.com/images/david.jpg'),
       (3, 103, 'Sarah', 'Miller', 'Boekensteeg', '22', '9012EF', 'Rotterdam', 'sarah.miller@example.com', '06-11223344', 'http://example.com/images/sarah.jpg'),
       (4, 104, 'Chris', 'Wilson', 'Kennislaan', '7', '3456GH', 'Den Haag', 'chris.wilson@example.com', '06-55443322', 'http://example.com/images/chris.jpg'),
       (5, 105, 'Lisa', 'Moore', 'Vertelhof', '1', '7890IJ', 'Eindhoven', 'lisa.moore@example.com', '06-99887766', 'http://example.com/images/lisa.jpg'),
       (6, 201, 'Adam', 'Brown', 'Parklaan', '12', '1000AB', 'Amsterdam', 'adam.brown@example.com', '0611223344', 'http://example.com/images/adam.jpg'),
       (7, 202, 'Ellen', 'Davis', 'Kerkstraat', '5A', '2000CD', 'Utrecht', 'ellen.davis@example.com', '0622334455', 'http://example.com/images/ellen.jpg'),
       (8, 203, 'Michael', 'White', 'Nieuwstraat', '30', '3000EF', 'Rotterdam', 'michael.white@example.com', '0633445566', 'http://example.com/images/michael.jpg'),
       (9, 204, 'Olivia', 'Green', 'Dorpsweg', '8', '4000GH', 'Den Haag', 'olivia.green@example.com', '0644556677', 'http://example.com/images/olivia.jpg'),
       (10, 205, 'Peter', 'Black', 'Molenpad', '15B', '5000IJ', 'Eindhoven', 'peter.black@example.com', '0655667788', 'http://example.com/images/peter.jpg');

SELECT setval('users_information_user_information_id_seq', (SELECT MAX(user_information_id) FROM users_information));

-- Insert user_roles
INSERT INTO user_roles (user_id, rolename)
VALUES (101, 'ROLE_LIBRARIAN'),
       (102, 'ROLE_LIBRARIAN'),
       (103, 'ROLE_LIBRARIAN'),
       (104, 'ROLE_LIBRARIAN'),
       (105, 'ROLE_LIBRARIAN'),
       (201, 'ROLE_MEMBER'),
       (202, 'ROLE_MEMBER'),
       (203, 'ROLE_MEMBER'),
       (204, 'ROLE_MEMBER'),
       (205, 'ROLE_MEMBER');

-- Insert invoices
INSERT INTO invoices (invoice_date, invoice_period, invoice_amount, payment_status)
VALUES ('2024-01-15', 'Januari 2024', 25.50, 'PAID'),
       ('2024-02-10', 'Februari 2024', 15.00, 'PENDING'),
       ('2024-03-05', 'Maart 2024', 30.75, 'OVERDUE'),
       ('2024-04-20', 'April 2024', 10.00, 'PAID'),
       ('2024-05-25', 'Mei 2024', 45.99, 'PENDING'),
       ('2024-06-12', 'Juni 2024', 5.25, 'OVERDUE');

-- Insert loans
INSERT INTO loans (loan_date, return_date, is_returned, book_copy_id, user_id)
VALUES
    -- Bestaande leningen
    ('2025-06-20', '2025-07-04', TRUE, 1, 201),
    ('2025-06-20', '2025-07-04', TRUE, 7, 201),
    ('2025-07-01', '2025-07-15', FALSE, 3, 202), -- Openstaand, niet achterstallig (inleverdatum 15 juli 2025)
    ('2025-07-05', '2025-07-19', FALSE, 5, 203), -- Openstaand, niet achterstallig (inleverdatum 19 juli 2025)
    ('2025-05-10', '2025-05-24', TRUE, 9, 204),
    ('2025-07-08', '2025-07-22', FALSE, 11, 205), -- Openstaand, niet achterstallig (inleverdatum 22 juli 2025)

    -- NIEUWE OPENSTAANDE LENINGEN (Outstanding - inleverdatum in de toekomst)
    ('2025-07-18', '2025-08-01', FALSE, 2, 201), -- Boekcopy 2, Gebruiker 201, inleverdatum in de toekomst
    ('2025-07-17', '2025-07-25', FALSE, 4, 202), -- Boekcopy 4 (DAMAGED, maar wordt uitgeleend), Gebruiker 202, inleverdatum in de toekomst

    -- NIEUWE ACHTERSTALLIGE LENINGEN (Overdue - inleverdatum in het verleden)
    ('2025-06-01', '2025-06-15', FALSE, 6, 203), -- Boekcopy 6, Gebruiker 203, inleverdatum 15 juni 2025 (VERSTREKEN)
    ('2025-06-25', '2025-07-10', FALSE, 10, 204); -- Boekcopy 10 (MISSING, maar wordt uitgeleend), Gebruiker 204, inleverdatum 10 juli 2025 (VERSTREKEN)

-- Insert fines
INSERT INTO fines (fine_amount, fine_date, is_paid, loan_id, invoice_id)
VALUES (2.50, '2025-07-05', FALSE, 1, 1),
       (10.00, '2025-07-01', TRUE, 1, 1),
       (7.75, '2025-07-07', FALSE, 1, 4),
       (3.00, '2025-07-06', FALSE, 4, 3),
       (1.25, '2025-07-08', FALSE, 5, 5);