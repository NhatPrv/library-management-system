CREATE DATABASE librarydb;
USE librarydb;

CREATE TABLE books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    author VARCHAR(255),
    year INT
);

USE librarydb;

INSERT INTO books (title, author, year) VALUES
('Clean Code', 'Robert C. Martin', 2008),
('Effective Java', 'Joshua Bloch', 2018),
('Design Patterns: Elements of Reusable Object-Oriented Software', 'Erich Gamma', 1994),
('Refactoring: Improving the Design of Existing Code', 'Martin Fowler', 1999),
('Introduction to Algorithms', 'Thomas H. Cormen', 2009),
('Head First Design Patterns', 'Eric Freeman', 2004),
('The Pragmatic Programmer', 'Andrew Hunt', 1999),
('Java Concurrency in Practice', 'Brian Goetz', 2006),
('Algorithms', 'Robert Sedgewick', 2011),
('Artificial Intelligence: A Modern Approach', 'Stuart Russell', 2020);
