DROP SCHEMA IF EXISTS store;
CREATE SCHEMA store;

use store;

CREATE TABLE users (
	id INT UNIQUE NOT NULL AUTO_INCREMENT,
    username VARCHAR(500) UNIQUE NOT NULL,
    firstname VARCHAR(500) NOT NULL,
    lastname VARCHAR(500) NOT NULL,
    email VARCHAR(300) NOT NULL,
    phone VARCHAR(100) NOT NULL,
    password VARCHAR(300) NOT NULL,
    role VARCHAR(20) NOT NULL,
    comments VARCHAR(5000)
);


CREATE TABLE items(
	id INT UNIQUE NOT NULL AUTO_INCREMENT,
	name VARCHAR(500) NOT NULL,
    count INT NOT NULL,
    price DOUBLE NOT NULL,
    article_num VARCHAR(100) NOT NULL,
    description VARCHAR(5000)
);

CREATE TABLE orders(
	id INT UNIQUE NOT NULL AUTO_INCREMENT,
    item_id INT NOT NULL,
    user_id INT,
	user_firstname VARCHAR(500) NOT NULL,
	user_lastname VARCHAR(500) NOT NULL,
	phone VARCHAR(100) NOT NULL,
	is_paid BOOLEAN DEFAULT FALSE
);

INSERT INTO users(username, firstname, lastname, email, phone, password, role, comments)
VALUES
	('Rambo', 'John', 'Rembo', 'john_rembo@gmail.com', '+1-534-215-94-16', '$2a$12$aw/kJBYsVtsZEdSKzW/1veKpDlbsaBO0Hx6Cy3MLdN9wIv3xDC34u', 'ROLE_ADMIN', 'Very shy guy'), #pass: 'First blood'
    ('SarrahTheSkyNetDestroyer', 'Sarah', 'Connor', 'sarrah_o_connor@yahoo.com', '+1-243-452-34-34', '$2a$08$hU6YaNaUnpmD6KQ4HEYj2OXX77TCdwKck2KBIMa76B/R35cqEaIky', 'ROLE_USER', 'Calm person'), #pass: 'SkynetWatchingYou#@553'
    ('Jedi_Obi_Wan', 'Obi-Wan', 'Kenobi', 'obi.one@mail.com', '+1-224-235-46-12', '$2a$08$BhH4M0JtKM6cht2hRrv0ZOoECYtSVp35jwNVWbPUPte0cyny0sfq.', 'ROLE_USER', 'Star Wars fan'), #pass: 'LittleYoda123'
    ('test_admin', 'Test', 'Test', 'test.admin@mail.com', '+00000000000', '$2a$10$VWOZVuEB5k/tfc5GXvbRhuYmA7qvh/krxVeaKokVOcZkdIOfj.14S', 'ROLE_ADMIN', 'Administrator for test purposes only'), #pass: 'Test'
    ('test_user', 'Test', 'Test', 'test.user@mail.com', '+00000000000', '$10$CxWGwZzqnODw70a.pYQGHe27jKJI6UrFVINQdDolXpquA/M61ZtMi', 'ROLE_USER', 'User for test purposes only'); #pass: 'Test'


INSERT INTO items(id, name, count, price, article_num, description)
VALUES
	(1, 'Colgate Medium Toothbrash', 300, 12.99, 'LT-1501-73', 'Colgate Medium Toothbrash, color white'),
    (2, 'Sword of the Jedi, blue ray', 5, 1500000.0, 'SJ-01B', 'Jedi laser sword, ray color: blue'),
    (3, 'Sword of the Jedi, red ray', 5, 1500000.0, 'SJ-01R', 'Jedi laser sword, ray color: red'),
    (4, 'Lenovo Legion 5 15ACH6H Dark Blue', 12, 2500.99, '82JU01C3RA',  'Lenovo Legion Gaming Series, Generation 5, CPU: Core i9-13430 3.2Ghz, Matrix: IPS 1920x1080, Video: 16Gb NVIDIA GeForce RTX 3070, RAM: 8GB, SSD: 1Gb');


INSERT INTO orders(item_id, user_id, user_firstname, user_lastname, phone, is_paid)
VALUES
	(1, 3, '', '', '', true),
    (3, NULL, 'Din', 'Djarin the Mandalorian', '0-000-000-000', false),
    (4, 1, '', '', '', true),
    (2, 2, '', '', '', false);


