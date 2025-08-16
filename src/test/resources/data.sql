-- data.sql

-- Verwijder bestaande data. De volgorde is belangrijk vanwege foreign keys.
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

-- Zorg ervoor dat de ID's vastliggen voor voorspelbare testen door de teller te resetten.
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;
ALTER TABLE books ALTER COLUMN book_id RESTART WITH 1;
ALTER TABLE book_copies ALTER COLUMN book_copy_id RESTART WITH 1;
ALTER TABLE loans ALTER COLUMN loan_id RESTART WITH 1;
ALTER TABLE fines ALTER COLUMN fine_id RESTART WITH 1;
ALTER TABLE invoices ALTER COLUMN invoice_id RESTART WITH 1;

-- Insert roles.
INSERT INTO roles (rolename) VALUES ('ROLE_MEMBER');
INSERT INTO roles (rolename) VALUES ('ROLE_ADMIN');

-- Insert users with specific IDs.
INSERT INTO users (user_id, username, password) VALUES (1, 'testuser_member', '{noop}password');
INSERT INTO users (user_id, username, password) VALUES (2, 'testuser_admin', '{noop}password');

-- Koppel users aan hun rollen.
INSERT INTO user_roles (user_id, rolename) VALUES (1, 'ROLE_MEMBER');
INSERT INTO user_roles (user_id, rolename) VALUES (2, 'ROLE_ADMIN');

-- Insert fine configuration with a specific ID.
INSERT INTO fine_configurations (id, daily_fine, max_fine_amount) VALUES (1, 0.50, 10.00);

-- Insert books with specific IDs.
INSERT INTO books (book_id, title, author_first_name, author_last_name, isbn, publisher, category) VALUES (1, 'The Test Book', 'A.', 'Tester', '1234567890', 'Test Publisher', 'FICTION');
INSERT INTO books (book_id, title, author_first_name, author_last_name, isbn, publisher, category) VALUES (2, 'Another Test Book', 'B.', 'Tester', '0987654321', 'Another Publisher', 'NON_FICTION');

-- Insert book copies with different statuses and specific IDs.
INSERT INTO book_copies (book_copy_id, book_id, follow_number, status) VALUES (1, 1, 1, 'AVAILABLE');
INSERT INTO book_copies (book_copy_id, book_id, follow_number, status) VALUES (2, 1, 2, 'ON_LOAN');
INSERT INTO book_copies (book_copy_id, book_id, follow_number, status) VALUES (3, 2, 1, 'ON_LOAN');

-- Insert test loans with specific IDs for the 'ON_LOAN' copies.
INSERT INTO loans (loan_date, return_date, actual_return_date, is_returned, user_id, book_copy_id)
VALUES (CURRENT_DATE(), CURRENT_DATE() + 14, NULL, false, 1, 2),
       (CURRENT_DATE(), CURRENT_DATE() + 14, NULL, false, 1, 3);