-- Create Database
DROP DATABASE IF EXISTS myUSCPeerReviews;
CREATE DATABASE myUSCPeerReviews;

-- Use the created database
USE myUSCPeerReviews;

-- Create 'users' table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    pass VARCHAR(255) NOT NULL,
    fname VARCHAR(255) NOT NULL,
    lname VARCHAR(255) NOT NULL,
    is_guest INT NOT NULL
);

-- Create 'pdf_storage' table
CREATE TABLE pdf_storage (
    pdf_id INT PRIMARY KEY AUTO_INCREMENT,
    pdf_file LONGBLOB NOT NULL
    -- date_uploaded DATETIME NOT NULL
);

-- Create 'feedbacks' table
CREATE TABLE feedbacks (
    feedback_id INT PRIMARY KEY AUTO_INCREMENT,
    pdf_id INT NOT NULL,
    FOREIGN KEY (pdf_id) REFERENCES pdf_storage(pdf_id)
);

-- Create 'essays' table
CREATE TABLE essays (
    essay_id INT PRIMARY KEY AUTO_INCREMENT,
    essay_name VARCHAR(255),
    user_id INT NOT NULL,
    -- reviewer_id INT, -- don't need if matched essay id???
    matched_id INT,
    pdf_id INT NOT NULL,
    feedback_id INT,
    tags VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (pdf_id) REFERENCES pdf_storage(pdf_id),
    FOREIGN KEY (feedback_id) REFERENCES feedbacks(feedback_id)
);

CREATE TABLE queue (
    queue_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    essay_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (essay_id) REFERENCES essays(essay_id)
);