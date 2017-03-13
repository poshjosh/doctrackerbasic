drop database if exists `doctrackerbasic_db1`;
create database `doctrackerbasic_db1` CHARACTER SET = utf8 COLLATE = utf8_general_ci;

drop table if exists `doctrackerbasic_db1`.unit;
create table `doctrackerbasic_db1`.unit
(
    unitid INTEGER(8) AUTO_INCREMENT not null primary key,
    unit VARCHAR(100) not null UNIQUE,
    parentunit INTEGER(8) not null,

    FOREIGN KEY (parentunit) REFERENCES `doctrackerbasic_db1`.unit(unitid) ON DELETE CASCADE ON UPDATE CASCADE

)ENGINE=INNODB CHARACTER SET = utf8 COLLATE = utf8_general_ci;
insert into `doctrackerbasic_db1`.unit VALUES(1, 'Office of the Chief of Air Staff', 1);
insert into `doctrackerbasic_db1`.unit VALUES(2, 'Policy & Plans Branch', 1);
insert into `doctrackerbasic_db1`.unit VALUES(3, 'Training & Operations Branch', 1);
insert into `doctrackerbasic_db1`.unit VALUES(4, 'Aircraft Engineering Branch', 1);
insert into `doctrackerbasic_db1`.unit VALUES(5, 'Logistics & Communications Branch', 1);
insert into `doctrackerbasic_db1`.unit VALUES(6, 'Administration Branch', 1);
insert into `doctrackerbasic_db1`.unit VALUES(7, 'Standard Evaluation Branch', 1);
insert into `doctrackerbasic_db1`.unit VALUES(8, 'Medical Services Branch', 1);
insert into `doctrackerbasic_db1`.unit VALUES(9, 'Air Secretary Branch', 1);
insert into `doctrackerbasic_db1`.unit VALUES(10, 'Accounts and Budget Branch', 1);
insert into `doctrackerbasic_db1`.unit VALUES(11, 'SI List', 1);

drop table if exists `doctrackerbasic_db1`.appointment;
create table `doctrackerbasic_db1`.appointment
(
    appointmentid INTEGER(8) AUTO_INCREMENT not null primary key,
    appointment VARCHAR(100) not null UNIQUE,
    parentappointment INTEGER(8) not null,
    unit INTEGER(8) not null, 

    FOREIGN KEY (parentappointment) REFERENCES `doctrackerbasic_db1`.appointment(appointmentid) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (unit) REFERENCES `doctrackerbasic_db1`.unit(unitid) ON DELETE CASCADE ON UPDATE CASCADE

)ENGINE=INNODB CHARACTER SET = utf8 COLLATE = utf8_general_ci;
insert into `doctrackerbasic_db1`.appointment VALUES(1, 'Chief of the Air Staff', 1, 1);
insert into `doctrackerbasic_db1`.appointment VALUES(2, 'Chief of Policy & Plans', 1, 2);
insert into `doctrackerbasic_db1`.appointment VALUES(3, 'Chief of Training & Operations', 1, 3);
insert into `doctrackerbasic_db1`.appointment VALUES(4, 'Chief of Aircraft Engineering', 1, 4);
insert into `doctrackerbasic_db1`.appointment VALUES(5, 'Chief of Logistics & Communications', 1, 5);
insert into `doctrackerbasic_db1`.appointment VALUES(6, 'Chief of Administration', 1, 6);
insert into `doctrackerbasic_db1`.appointment VALUES(7, 'Chief of Standard Evaluation', 1, 7);
insert into `doctrackerbasic_db1`.appointment VALUES(8, 'Chief of Medical Services', 1, 8);
insert into `doctrackerbasic_db1`.appointment VALUES(9, 'Air Secretary', 1, 9);
insert into `doctrackerbasic_db1`.appointment VALUES(10, 'Chief of Accounts and Budget', 1, 10);
insert into `doctrackerbasic_db1`.appointment VALUES(11, 'Principal Air Staff Officer to the Chief of Air Staff', 1, 1);
insert into `doctrackerbasic_db1`.appointment VALUES(12, 'Air Assistant to the Chief of Air Staff', 1, 1);
insert into `doctrackerbasic_db1`.appointment VALUES(13, 'Director of Procurement', 5, 5);

drop table if exists `doctrackerbasic_db1`.doc;
create table `doctrackerbasic_db1`.doc
(
    docid INTEGER(8) AUTO_INCREMENT not null primary key,
    datesigned DATE null,
    referencenumber VARCHAR(255) null,
    subject VARCHAR(1000) not null,                  
    timecreated TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
    timemodified TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

)ENGINE=INNODB CHARACTER SET = utf8 COLLATE = utf8_general_ci;

drop table if exists `doctrackerbasic_db1`.task;
create table `doctrackerbasic_db1`.task
(
    taskid INTEGER(8) AUTO_INCREMENT not null primary key,
    doc INTEGER(8) not null,
    description VARCHAR(10000) null,
    author INTEGER(8) not null,
    reponsibility INTEGER(8) not null,
    timeopened DATETIME null,
    timeclosed DATETIME null, 
    timecreated TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
    timemodified TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (doc) REFERENCES `doctrackerbasic_db1`.doc(docid) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (author) REFERENCES `doctrackerbasic_db1`.appointment(appointmentid) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (reponsibility) REFERENCES `doctrackerbasic_db1`.appointment(appointmentid) ON DELETE CASCADE ON UPDATE CASCADE

)ENGINE=INNODB CHARACTER SET = utf8 COLLATE = utf8_general_ci;

drop table if exists `doctrackerbasic_db1`.taskresponse;
create table `doctrackerbasic_db1`.taskresponse
(
    taskresponseid INTEGER(8) AUTO_INCREMENT not null primary key,
    task INTEGER(8) not null,
    author INTEGER(8) not null,
    response VARCHAR(10000) not null,
    deadline TIMESTAMP null,
    timecreated TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
    timemodified TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (task) REFERENCES `doctrackerbasic_db1`.task(taskid) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (author) REFERENCES `doctrackerbasic_db1`.appointment(appointmentid) ON DELETE CASCADE ON UPDATE CASCADE

)ENGINE=INNODB CHARACTER SET = utf8 COLLATE = utf8_general_ci;


