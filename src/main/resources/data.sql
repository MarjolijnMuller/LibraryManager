-- Insert roles
INSERT INTO roles (rolename)
VALUES ('ROLE_MEMBER'),
       ('ROLE_LIBRARIAN'),
       ('ROLE_ADMIN');

-- Insert users
INSERT INTO users (username, password)
VALUES
        --admin
       ('admin', '$2a$10$HSkg99GUXUUlwOavURKyfu2xuPSgVRWTUolnChcD2ZROuemIqkyQS'), -- Wachtwoord: admin123
       --librarians
       ('emilylib', '$2a$10$lk.TulcClA9jGcVdAYaSb.7A8JFryBpPDDLo1wvgnFX/NkvHEG8I6'), -- Wachtwoord: passwordEmily
       ('davidlib', '$2a$10$snzesRJ3cmueopRbgZleJuWWSHHShm.K3ToPe7v0y2bdjz0KSTqZC'), -- Wachtwoord: passwordDavid
       ('sarahlib', '$2a$10$JngqkUPrtfyOCA9RjB5lz.XnQzNXLpMF8IsYK54.NNkyy.LdwYgni'), -- Wachtwoord: passwordSarah
       ('chrislib', '$2a$10$ValRdN3RfiKzXyHIsQpPdOWCM/w4cxEx1VtQqPI.j9kmUpjdrHHiW'), -- Wachtwoord: passwordChris
       ('lisalib', '$2a$10$VCFDUwsI8TB0KLzyeM3/Ue3BbfU2ba/zDR95WAziMGK9cP02hf2mW'), -- Wachtwoord: passwordLisa
       --members
       ('adamember', '$2a$10$8m8CxL1FtBwS5/xgKhJ/yuPuRlTEi5kh0ZIwl6tzYdTnH0nGSs/Xy'), -- Wachtwoord: passwordAdam
       ('ellenmember', '$2a$10$4AMC/j78Lt5sjvgmr2rwwOMBWoEsOkgPuwC0m6xMb8I3gYC6/6XEK'), -- Wachtwoord: passwordEllen
       ('michaelmember', '$2a$10$NJqM3e0ODFf4yb5trVjXnOA/NZFpFD4auRqCiNJWuGhbVhNJH4m.G'), -- Wachtwoord: passwordMichael
       ('oliviamember', '$2a$10$IHxBjC5AZns1KIebWBJ7ZuNfJzMN54QDU9234mPbaGp.iJDzS3h8O'), -- Wachtwoord: passwordOlivia
       ('petermember', '$2a$10$N5P5ocYfr8zQauzczB3k5OTRrV7tpwPVL/52kQhI/GUTJJxaCPhZi'); -- Wachtwoord: passwordPeter

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

-- Insert user_roles using a single combined statement
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

-- Insert profile
INSERT INTO profile (user_id, first_name, last_name, street, house_number, postal_code, city, email, phone, profile_picture_url)
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

-- Insert invoices
INSERT INTO invoices (invoice_date, invoice_period, invoice_amount, payment_status, user_id)
SELECT '2024-01-15'::date, 'Januari 2024', 25.50, 'PAID', user_id FROM users WHERE username = 'adamember'
UNION ALL
SELECT '2024-02-10'::date, 'Februari 2024', 15.00, 'PENDING', user_id FROM users WHERE username = 'ellenmember'
UNION ALL
SELECT '2024-03-05'::date, 'Maart 2024', 30.75, 'OVERDUE', user_id FROM users WHERE username = 'michaelmember'
UNION ALL
SELECT '2024-04-20'::date, 'April 2024', 10.00, 'PAID', user_id FROM users WHERE username = 'oliviamember'
UNION ALL
SELECT '2024-05-25'::date, 'Mei 2024', 45.99, 'PENDING', user_id FROM users WHERE username = 'petermember'
UNION ALL
SELECT '2024-06-12'::date, 'Juni 2024', 5.25, 'OVERDUE', user_id FROM users WHERE username = 'adamember';

-- Insert loans
INSERT INTO loans (loan_date, return_date, actual_return_date, is_returned, book_copy_id, user_id)
SELECT '2025-06-20'::date, '2025-07-04'::date, '2025-07-04'::date, TRUE, 1, user_id FROM users WHERE username = 'adamember'
UNION ALL
SELECT '2025-06-20'::date, '2025-07-04'::date, NULL, TRUE, 7, user_id FROM users WHERE username = 'adamember'
UNION ALL
SELECT '2025-07-01'::date, '2025-07-15'::date, NULL, FALSE, 3, user_id FROM users WHERE username = 'ellenmember'
UNION ALL
SELECT '2025-07-05'::date, '2025-07-19'::date, NULL, FALSE, 5, user_id FROM users WHERE username = 'michaelmember'
UNION ALL
SELECT '2025-05-10'::date, '2025-05-24'::date, '2025-05-24'::date, TRUE, 9, user_id FROM users WHERE username = 'oliviamember'
UNION ALL
SELECT '2025-07-08'::date, '2025-07-22'::date, NULL, FALSE, 11, user_id FROM users WHERE username = 'petermember'
UNION ALL
SELECT '2025-07-18'::date, '2025-08-01'::date, NULL, FALSE, 2, user_id FROM users WHERE username = 'adamember'
UNION ALL
SELECT '2025-07-17'::date, '2025-07-25'::date, NULL, FALSE, 4, user_id FROM users WHERE username = 'ellenmember'
UNION ALL
SELECT '2025-06-01'::date, '2025-06-15'::date, NULL, FALSE, 6, user_id FROM users WHERE username = 'michaelmember'
UNION ALL
SELECT '2025-06-25'::date, '2025-07-10'::date, NULL, FALSE, 10, user_id FROM users WHERE username = 'oliviamember';

-- Insert fines
INSERT INTO fines (fine_amount, fine_date, is_paid, is_ready_for_invoice, loan_id, invoice_id)
VALUES (2.50, '2025-07-05'::date, FALSE, TRUE, 1, 1),
       (10.00, '2025-07-01'::date, TRUE, TRUE, 1, 1),
       (7.75, '2025-07-07'::date, FALSE, TRUE, 1, 4),
       (3.00, '2025-07-06'::date, FALSE, TRUE, 4, 3),
       (1.25, '2025-07-08'::date, FALSE, TRUE, 5, 5),
       (5.00, '2025-07-20'::date, FALSE, TRUE, 6, 6);

INSERT INTO fine_configurations (id, daily_fine, max_fine_amount, last_updated_by, last_updated_at)
VALUES (1, 0.50, 20.00, 'system', CURRENT_TIMESTAMP);