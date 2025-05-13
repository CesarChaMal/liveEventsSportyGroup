-- Insert sample users (password hashed with BCrypt "password")
-- INSERT INTO app_users (email, password_hash) VALUES ( 'john.doe@example.com', '$2a$10$VqPnXXAUyA5b0uz0CFysQuD3ImM75MPLedNJerzn51iEw8bWPqFtG'); -- password: pass123
-- INSERT INTO app_users (email, password_hash) VALUES ( 'jane.doe@example.com', '$2a$10$VeBlRbQmOek9LIKdjRBsWeJ1NrUgTlhdxUrQKI3gN.OO28mAeABLe'); -- password: pass456

INSERT INTO app_users (email, first_name, last_name, password_hash) VALUES ( 'john.doe@example.com', 'John', 'Doe', '$2a$10$VqPnXXAUyA5b0uz0CFysQuD3ImM75MPLedNJerzn51iEw8bWPqFtG'); -- password: pass123
INSERT INTO app_users (email, first_name, last_name, password_hash) VALUES ( 'jane.doe@example.com', 'Jane', 'Doe', '$2a$10$VeBlRbQmOek9LIKdjRBsWeJ1NrUgTlhdxUrQKI3gN.OO28mAeABLe'); -- password: pass456

-- Insert courses
INSERT INTO course (id, name, description) VALUES (1, 'Math 101', 'Introduction to Algebra');
INSERT INTO course (id, name, description) VALUES (2, 'English 101', 'Introduction to English Literature');

-- Insert students
INSERT INTO student (id, first_name, last_name, email) VALUES (1, 'John', 'Doe', 'john.doe@example.com');
INSERT INTO student (id, first_name, last_name, email) VALUES (2, 'Jane', 'Doe', 'jane.doe@example.com');

-- Map students to courses
INSERT INTO student_courses (student_id, course_id) VALUES (1, 1);
INSERT INTO student_courses (student_id, course_id) VALUES (2, 2);
