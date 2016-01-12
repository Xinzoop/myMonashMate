-- User account table
CREATE TABLE ACCOUNT
(
USERID INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1000, INCREMENT BY 1),
USERNAME VARCHAR(30) NOT NULL UNIQUE,
PASSWORD VARCHAR(256) NOT NULL,
PUBLKEY VARCHAR(256)
);
CREATE TABLE PRIVACY
(
USERID INT NOT NULL,
ATTRID INT NOT NULL,
VISIBILITY BOOLEAN NOT NULL
);

CREATE TABLE UNIT
(
ID INT NOT NULL PRIMARY KEY,
NAME VARCHAR(120) NOT NULL,
DETAILS VARCHAR(450)
);

INSERT INTO UNIT (ID, NAME) VALUES (1,'FIT3121 Archival systems');
INSERT INTO UNIT (ID, NAME) VALUES (2,'FIT3123 Information access');
INSERT INTO UNIT (ID, NAME) VALUES (3,'FIT3124 Professional practice');
INSERT INTO UNIT (ID, NAME) VALUES (4,'FIT3128 Database systems design');
INSERT INTO UNIT (ID, NAME) VALUES (5,'FIT3130 Computer network design and deployment');
INSERT INTO UNIT (ID, NAME) VALUES (6,'FIT3134 IT-based entrepreneurship');
INSERT INTO UNIT (ID, NAME) VALUES (7,'FIT3136 IT governance and strategy for business');
INSERT INTO UNIT (ID, NAME) VALUES (8,'FIT3138 Real time enterprise systems');
INSERT INTO UNIT (ID, NAME) VALUES (9,'FIT3139 Computational science');
INSERT INTO UNIT (ID, NAME) VALUES (10,'FIT3140 Advanced programming');
INSERT INTO UNIT (ID, NAME) VALUES (11,'FIT3141 Data communications and computer networks');
INSERT INTO UNIT (ID, NAME) VALUES (12,'FIT5046 Mobile and distributed computing system');
INSERT INTO UNIT (ID, NAME) VALUES (13,'FIT5123 Introduction to business information system');
INSERT INTO UNIT (ID, NAME) VALUES (14,'FIT5130 System design and analysis');
INSERT INTO UNIT (ID, NAME) VALUES (15,'FIT5057 Project management');


CREATE TABLE COURSE
(
ID INT NOT NULL PRIMARY KEY,
NAME VARCHAR(120) NOT NULL,
DETAILS VARCHAR(450)
);

insert into course (id, name) values (1,'2402 Master of Information Technology (Professional)');
insert into course (id, name) values (2, '3342 Master of Business Information Systems');
insert into course (id, name) values (3, '3341 Master of Business Information Systems (Professional)');
insert into course (id, name) values (4, '3348 Master of Information Technology');
insert into course (id, name) values (5, '2770 Bachelor of Software Engineering');
insert into course (id, name) values (6, '2380 Bachelor of Computer Science');
insert into course (id, name) values (7, '4312 Master of Networks and Security');
insert into course (id, name) values (8, '3337 Master of Philosophy');
insert into course (id, name) values (9, '0190 Doctor of Philosophy');
insert into course (id, name) values (10, '3333 Bachelor of Business Information System');
insert into course (id, name) values (11, '4307 Bachelor of Computer and Information Sciences');
insert into course (id, name) values (12, '2380 Bachelor of Computer Science');
insert into course (id, name) values (13, '3334 Bachelor of Information Technology and Systems');
insert into course (id, name) values (14, '3517 Bachelor of Science and Computeter Science');
insert into course (id, name) values (15, '2770 Bachelor of Software Engineering');

CREATE TABLE PROFILE 
(
ID INT NOT NULL PRIMARY KEY,
FIRSTNAME VARCHAR(30) NOT NULL,
SURNAME VARCHAR(30) NOT NULL,
NICKNAME VARCHAR(30),
LATITUDE DECIMAL(9,6) NOT NULL,
LONGITUDE DECIMAL(9,6) NOT NULL,
COURSEID INT,
NATIONALITY VARCHAR(30),
NATIVELANGID VARCHAR(30),
SECONDLANGID VARCHAR(30),
SUBURB VARCHAR(30),
FAVFOOD VARCHAR(30),
FAVMOVIE VARCHAR(30),
FAVPROGLANG VARCHAR(30),
FAVUNITID INT,
CURJOB VARCHAR(30),
PREVJOB VARCHAR(30)
);

CREATE TABLE USERUNIT
(
USERID INT NOT NULL,
UNITID INT NOT NULL
);
