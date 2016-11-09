CREATE TABLE department
(
    id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    dept_name VARCHAR(50)
);
CREATE TABLE employee
(
    id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    employee_name VARCHAR(50),
    email VARCHAR(50),
    birth DATE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    dept_id INT(11)
);