insert into books(title,
                  author_first_name,
                  author_last_name,
                  isbn,
                  publisher,
                  category)
values ('The Hitchhiker''s Guide to the Galaxy',
        'Douglas',
        'Adams',
        '9780345391803',
        'Del Rey',
        'FICTION'),
       ('The Name of the Wind',
        'Patrick',
        'Rothfuss',
        '9780756404741',
        'DAW Books',
        'FICTION'),
       ('Thinking, Fast and Slow',
        'Daniel',
        'Kahneman',
        '9780374533557',
        'Farrar, Straus and Giroux',
        'NON_FICTION'),
       ('The Gruffalo',
        'Julia',
        'Donaldson',
        '9780333905581',
        'Macmillan Children''s Books',
        'CHILDRENS_BOOK'),
       ('Where the Crawdads Sing',
        'Delia',
        'Owens',
        '9780735219090',
        'G.P. Putnam''s Sons',
        'ADULTS_BOOK'),
       ('Cosmos',
        'Carl',
        'Sagan',
        '9780345539434',
        'Ballantine Books',
        'SCIENCE'),
       ('A People''s History of the United States',
        'Howard',
        'Zinn',
        '9780060528379',
        'Harper Perennial',
        'HISTORY');

insert into book_copies(book_id, follow_number, status)
values (1, 1, 'AVAILABLE'),
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

INSERT INTO users (user_id, username, email, password, profile_picture_url)
VALUES (101, 'emilylib', 'emily.clark@example.com', '$2a$10$HASHED_PASSWORD_EMILY',
        'http://example.com/images/emily.jpg'),
       (102, 'davidlib', 'david.jones@example.com', '$2a$10$HASHED_PASSWORD_DAVID',
        'http://example.com/images/david.jpg'),
       (103, 'sarahlib', 'sarah.miller@example.com', '$2a$10$HASHED_PASSWORD_SARAH',
        'http://example.com/images/sarah.jpg'),
       (104, 'chrislib', 'chris.wilson@example.com', '$2a$10$HASHED_PASSWORD_CHRIS',
        'http://example.com/images/chris.jpg'),
       (105, 'lisalib', 'lisa.moore@example.com', '$2a$10$HASHED_PASSWORD_LISA', 'http://example.com/images/lisa.jpg');

INSERT INTO librarians (user_id, first_name, last_name)
VALUES (101, 'Emily', 'Clark'),
       (102, 'David', 'Jones'),
       (103, 'Sarah', 'Miller'),
       (104, 'Chris', 'Wilson'),
       (105, 'Lisa', 'Moore');

INSERT INTO users (user_id, username, email, password, profile_picture_url)
VALUES (201, 'adamember', 'adam.brown@example.com', '$2a$10$HASHED_PASSWORD_ADAM',
        'http://example.com/images/adam.jpg'),
       (202, 'ellenmember', 'ellen.davis@example.com', '$2a$10$HASHED_PASSWORD_ELLEN',
        'http://example.com/images/ellen.jpg'),
       (203, 'mikeymember', 'michael.white@example.com', '$2a$10$HASHED_PASSWORD_MIKEY',
        'http://example.com/images/michael.jpg'),
       (204, 'oliviarmember', 'olivia.green@example.com', '$2a$10$HASHED_PASSWORD_OLIVIA',
        'http://example.com/images/olivia.jpg'),
       (205, 'petermember', 'peter.black@example.com', '$2a$10$HASHED_PASSWORD_PETER',
        'http://example.com/images/peter.jpg');

INSERT INTO members (user_id, first_name, last_name, street, house_number, postal_code, city, phone)
VALUES (201, 'Adam', 'Brown', 'Parklaan', '12', '1000AB', 'Amsterdam', '0611223344'),
       (202, 'Ellen', 'Davis', 'Kerkstraat', '5A', '2000CD', 'Utrecht', '0622334455'),
       (203, 'Michael', 'White', 'Nieuwstraat', '30', '3000EF', 'Rotterdam', '0633445566'),
       (204, 'Olivia', 'Green', 'Dorpsweg', '8', '4000GH', 'Den Haag', '0644556677'),
       (205, 'Peter', 'Black', 'Molenpad', '15B', '5000IJ', 'Eindhoven', '0655667788');

INSERT INTO invoices (invoice_date, invoice_period, invoice_amount, payment_status)
VALUES ('2024-01-15', 'Januari 2024', 25.50, 'PAID'),
       ('2024-02-10', 'Februari 2024', 15.00, 'PENDING'),
       ('2024-03-05', 'Maart 2024', 30.75, 'OVERDUE'),
       ('2024-04-20', 'April 2024', 10.00, 'PAID'),
       ('2024-05-25', 'Mei 2024', 45.99, 'PENDING'),
       ('2024-06-12', 'Juni 2024', 5.25, 'OVERDUE');

INSERT INTO loans (loan_date, return_date, is_returned, book_copy_id, user_id)
VALUES
    ('2025-06-20', '2025-07-04', TRUE, 1, 201),
    ('2025-07-01', '2025-07-15', FALSE, 3, 202),
    ('2025-07-05', '2025-07-19', FALSE, 5, 203),
    ('2025-05-10', '2025-05-24', TRUE, 9, 204),
    ('2025-07-08', '2025-07-22', FALSE, 11, 205);