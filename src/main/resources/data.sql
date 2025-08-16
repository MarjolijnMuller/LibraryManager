DELETE FROM user_roles;
DELETE FROM fines;
DELETE FROM loans;
DELETE FROM book_copies;
DELETE FROM books;
DELETE FROM users;
DELETE FROM roles;
DELETE FROM fine_configurations;
DELETE FROM profile;
DELETE FROM invoices;

ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;
ALTER TABLE books ALTER COLUMN book_id RESTART WITH 1;
ALTER TABLE book_copies ALTER COLUMN book_copy_id RESTART WITH 1;
ALTER TABLE loans ALTER COLUMN loan_id RESTART WITH 1;
ALTER TABLE fines ALTER COLUMN fine_id RESTART WITH 1;
ALTER TABLE invoices ALTER COLUMN invoice_id RESTART WITH 1;

INSERT INTO roles (rolename)
VALUES ('ROLE_MEMBER'),
       ('ROLE_LIBRARIAN'),
       ('ROLE_ADMIN');

INSERT INTO users (username, password)
VALUES
    ('admin', '$2a$10$HSkg99GUXUUlwOavURKyfu2xuPSgVRWTUolnChcD2ZROuemIqkyQS'),
    ('emilylib', '$2a$10$lk.TulcClA9jGcVdAYaSb.7A8JFryBpPDDLo1wvgnFX/NkvHEG8I6'),
    ('davidlib', '$2a$10$snzesRJ3cmueopRbgZleJuWWSHHShm.K3ToPe7v0y2bdjz0KSTqZC'),
    ('sarahlib', '$2a$10$JngqkUPrtfyOCA9RjB5lz.XnQzNXLpMF8IsYK54.NNkyy.LdwYgni'),
    ('chrislib', '$2a$10$ValRdN3RfiKzXyHIsQpPdOWCM/w4cxEx1VtQqPI.j9kmUpjdrHHiW'),
    ('lisalib', '$2a$10$VCFDUwsI8TB0KLzyeM3/Ue3BbfU2ba/zDR95WAziMGK9cP02hf2mW'),
    ('adamember', '$2a$10$8m8CxL1FtBwS5/xgKhJ/yuPuRlTEi5kh0ZIwl6tzYdTnH0nGSs/Xy'),
    ('ellenmember', '$2a$10$4AMC/j78Lt5sjvgmr2rwwOMBWoEsOkgPuwC0m6xMb8I3gYC6/6XEK'),
    ('michaelmember', '$2a$10$NJqM3e0ODFf4yb5trVjXnOA/NZFpFD4auRqCiNJWuGhbVhNJH4m.G'),
    ('oliviamember', '$2a$10$IHxBjC5AZns1KIebWBJ7ZuNfJzMN54QDU9234mPbaGp.iJDzS3h8O'),
    ('petermember', '$2a$10$N5P5ocYfr8zQauzczB3k5OTRrV7tpwPVL/52kQhI/GUTJJxaCPhZi');

INSERT INTO books (title, author_first_name, author_last_name, isbn, publisher, category)
VALUES ('The Hitchhiker''s Guide to the Galaxy', 'Douglas', 'Adams', '9780345391803', 'Del Rey', 'FICTION'),
       ('The Name of the Wind', 'Patrick', 'Rothfuss', '9780756404741', 'DAW Books', 'FICTION'),
       ('Thinking, Fast and Slow', 'Daniel', 'Kahneman', '9780374533557', 'Farrar, Straus and Giroux', 'NON_FICTION'),
       ('The Gruffalo', 'Julia', 'Donaldson', '9780333905581', 'Macmillan Children''s Books', 'CHILDRENS_BOOK'),
       ('Where the Crawdads Sing', 'Delia', 'Owens', '9780735219090', 'G.P. Putnam''s Sons', 'ADULTS_BOOK'),
       ('Cosmos', 'Carl', 'Sagan', '9780345539434', 'Ballantine Books', 'SCIENCE'),
       ('A People''s History of the United States', 'Howard', 'Zinn', '9780060528379', 'Harper Perennial', 'HISTORY');

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

INSERT INTO user_roles (user_id, rolename)
SELECT u.user_id, r.rolename
FROM users u, (VALUES ('admin', 'ROLE_ADMIN'),
                      ('emilylib', 'ROLE_LIBRARIAN'),
                      ('davidlib', 'ROLE_LIBRARIAN'),
                      ('sarahlib', 'ROLE_LIBRARIAN'),
                      ('chrislib', 'ROLE_LIBRARIAN'),
                      ('lisalib', 'ROLE_LIBRARIAN'),
                      ('adamember', 'ROLE_MEMBER'),
                      ('ellenmember', 'ROLE_MEMBER'),
                      ('michaelmember', 'ROLE_MEMBER'),
                      ('oliviamember', 'ROLE_MEMBER'),
                      ('petermember', 'ROLE_MEMBER')
) as r(username, rolename)
WHERE u.username = r.username;

INSERT INTO profile (user_id, first_name, last_name, street, house_number, postal_code, city, email, phone, profile_picture_file)
SELECT user_id, 'Admin', 'User', 'Adminstraat', '1', '0000AA', 'Amsterdam', 'admin@example.com', '06-00000000', 'http://example.com/images/admin.jpg' FROM users WHERE username = 'admin'
UNION ALL
SELECT user_id, 'Emily', 'Clark', 'Bibliotheeklaan', '10', '1234AB', 'Amsterdam', 'emily.clark@example.com', '06-12345678', 'http://example.com/images/emily.jpg' FROM users WHERE username = 'emilylib'
UNION ALL
SELECT user_id, 'David', 'Jones', 'Leesplein', '5', '5678CD', 'Utrecht', 'david.jones@example.com', '06-87654321', 'http://example.com/images/david.jpg' FROM users WHERE username = 'davidlib'
UNION ALL
SELECT user_id, 'Sarah', 'Miller', 'Boekensteeg', '22', '9012EF', 'Rotterdam', 'sarah.miller@example.com', '06-11223344', 'http://example.com/images/sarah.jpg' FROM users WHERE username = 'sarahlib'
UNION ALL
SELECT user_id, 'Chris', 'Wilson', 'Kennislaan', '7', '3456GH', 'Den Haag', 'chris.wilson@example.com', '06-55443322', 'http://example.com/images/chris.jpg' FROM users WHERE username = 'chrislib'
UNION ALL
SELECT user_id, 'Lisa', 'Moore', 'Vertelhof', '1', '7890IJ', 'Eindhoven', 'lisa.moore@example.com', '06-99887766', 'http://example.com/images/lisa.jpg' FROM users WHERE username = 'lisalib'
UNION ALL
SELECT user_id, 'Adam', 'Brown', 'Parklaan', '12', '1000AB', 'Amsterdam', 'adam.brown@example.com', '0611223344', 'http://example.com/images/adam.jpg' FROM users WHERE username = 'adamember'
UNION ALL
SELECT user_id, 'Ellen', 'Davis', 'Kerkstraat', '5A', '2000CD', 'Utrecht', 'ellen.davis@example.com', '0622334455', 'http://example.com/images/ellen.jpg' FROM users WHERE username = 'ellenmember'
UNION ALL
SELECT user_id, 'Michael', 'White', 'Nieuwstraat', '30', '3000EF', 'Rotterdam', 'michael.white@example.com', '0633445566', 'http://example.com/images/michael.jpg' FROM users WHERE username = 'michaelmember'
UNION ALL
SELECT user_id, 'Olivia', 'Green', 'Dorpsweg', '8', '4000GH', 'Den Haag', 'olivia.green@example.com', '0644556677', 'http://example.com/images/olivia.jpg' FROM users WHERE username = 'oliviamember'
UNION ALL
SELECT user_id, 'Peter', 'Black', 'Molenpad', '15B', '5000IJ', 'Eindhoven', 'peter.black@example.com', '0655667788', 'http://example.com/images/peter.jpg' FROM users WHERE username = 'petermember';

INSERT INTO loans (loan_date, return_date, actual_return_date, is_returned, book_copy_id, user_id)
SELECT '2025-06-01'::date, '2025-06-15'::date, '2025-06-20'::date, TRUE, 1, user_id FROM users WHERE username = 'adamember';
INSERT INTO loans (loan_date, return_date, actual_return_date, is_returned, book_copy_id, user_id)
SELECT '2025-07-05'::date, '2025-07-19'::date, '2025-07-27'::date, TRUE, 5, user_id FROM users WHERE username = 'michaelmember';
INSERT INTO loans (loan_date, return_date, actual_return_date, is_returned, book_copy_id, user_id)
SELECT '2025-07-17'::date, '2025-07-25'::date, '2025-07-27'::date, TRUE, 4, user_id FROM users WHERE username = 'ellenmember';
INSERT INTO loans (loan_date, return_date, actual_return_date, is_returned, book_copy_id, user_id)
SELECT '2025-07-01'::date, '2025-07-15'::date, '2025-07-25'::date, TRUE, 3, user_id FROM users WHERE username = 'adamember';

INSERT INTO fines (fine_amount, fine_date, is_paid, is_ready_for_invoice, loan_id, invoice_id)
SELECT 2.50, '2025-06-20'::date, FALSE, TRUE, loan_id, NULL FROM loans WHERE user_id = (SELECT user_id FROM users WHERE username = 'adamember') AND actual_return_date = '2025-06-20';
INSERT INTO fines (fine_amount, fine_date, is_paid, is_ready_for_invoice, loan_id, invoice_id)
SELECT 4.00, '2025-07-27'::date, FALSE, TRUE, loan_id, NULL FROM loans WHERE user_id = (SELECT user_id FROM users WHERE username = 'michaelmember') AND actual_return_date = '2025-07-27';
INSERT INTO fines (fine_amount, fine_date, is_paid, is_ready_for_invoice, loan_id, invoice_id)
SELECT 1.00, '2025-07-27'::date, FALSE, TRUE, loan_id, NULL FROM loans WHERE user_id = (SELECT user_id FROM users WHERE username = 'ellenmember') AND actual_return_date = '2025-07-27';
INSERT INTO fines (fine_amount, fine_date, is_paid, is_ready_for_invoice, loan_id, invoice_id)
SELECT 5.00, '2025-07-25'::date, FALSE, TRUE, loan_id, NULL FROM loans WHERE user_id = (SELECT user_id FROM users WHERE username = 'adamember') AND actual_return_date = '2025-07-25';

INSERT INTO loans (loan_date, return_date, actual_return_date, is_returned, book_copy_id, user_id)
SELECT '2025-06-01'::date, '2025-06-15'::date, '2025-06-14'::date, TRUE, 6, user_id FROM users WHERE username = 'michaelmember';
INSERT INTO loans (loan_date, return_date, actual_return_date, is_returned, book_copy_id, user_id)
SELECT '2025-07-01'::date, '2025-07-15'::date, '2025-07-15'::date, TRUE, 2, user_id FROM users WHERE username = 'oliviamember';
INSERT INTO loans (loan_date, return_date, actual_return_date, is_returned, book_copy_id, user_id)
SELECT '2025-07-05'::date, '2025-07-19'::date, '2025-07-18'::date, TRUE, 8, user_id FROM users WHERE username = 'petermember';

INSERT INTO loans (loan_date, return_date, actual_return_date, is_returned, book_copy_id, user_id)
SELECT '2025-08-10'::date, '2025-08-24'::date, NULL, FALSE, 3, user_id FROM users WHERE username = 'ellenmember';
INSERT INTO loans (loan_date, return_date, actual_return_date, is_returned, book_copy_id, user_id)
SELECT '2025-08-12'::date, '2025-08-26'::date, NULL, FALSE, 9, user_id FROM users WHERE username = 'oliviamember';
INSERT INTO loans (loan_date, return_date, actual_return_date, is_returned, book_copy_id, user_id)
SELECT '2025-08-14'::date, '2025-08-28'::date, NULL, FALSE, 11, user_id FROM users WHERE username = 'petermember';
INSERT INTO loans (loan_date, return_date, actual_return_date, is_returned, book_copy_id, user_id)
SELECT '2025-07-15'::date, '2025-07-29'::date, NULL, FALSE, 12, user_id FROM users WHERE username = 'petermember';

INSERT INTO fine_configurations (id, daily_fine, max_fine_amount, last_updated_by, last_updated_at)
VALUES (1, 0.50, 20.00, 'system', CURRENT_TIMESTAMP);