/*DROP DATABASE SimpleLearnerMockup;*/
CREATE DATABASE SimpleLearnerMockup;
USE SimpleLearnerMockup;

CREATE TABLE Student(
	  ID VARCHAR(10) PRIMARY KEY,
    Password VARCHAR(10) NOT NULL,
    Firstname VARCHAR(20) NOT NULL,
    Lastname VARCHAR(20) NOT NULL);
    
CREATE TABLE Teacher(
	  ID VARCHAR(10) PRIMARY KEY,
    Password VARCHAR(10) NOT NULL,
    Firstname VARCHAR(20) NOT NULL,
    Lastname VARCHAR(20) NOT NULL);
    
CREATE TABLE Subject(
	  ID VARCHAR(20) PRIMARY KEY,
    Abbrev VARCHAR(4) NOT NULL UNIQUE);
    
CREATE TABLE TeacherTeaches(
	Teacher VARCHAR(10) REFERENCES Teacher(LID),
    Subject VARCHAR(10) REFERENCES Subject(FID),
    PRIMARY KEY(Teacher, Subject));
    
CREATE TABLE Category(
	  ID VARCHAR(100) PRIMARY KEY,
    Subject VARCHAR(10) REFERENCES Subject(FID));

CREATE TABLE Block(
	  ID VARCHAR(100) PRIMARY KEY,
    Teacher VARCHAR(10) REFERENCES Teacher(LID),
    Category VARCHAR(100) REFERENCES Category(KID));
 
CREATE TABLE Task(
	  ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    Block VARCHAR(100) NOT NULL,
    CONSTRAINT fk_Block Foreign Key (Block) REFERENCES Block(bid) ON UPDATE CASCADE,
    Question VARCHAR(500) NOT NULL);
    
CREATE TABLE Answer(
	  ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    Answertext VARCHAR(100) NOT NULL,
    Correct BOOLEAN NOT NULL,
    Task INTEGER REFERENCES Task(AID));

CREATE TABLE StudentSolvesBlock(
    Student VARCHAR(10) REFERENCES Student(SID),
    Block VARCHAR(100) REFERENCES Block(BID),
    Time DOUBLE,
    CONSTRAINT SLBID PRIMARY KEY(Student, Block));
	
CREATE TABLE StudentSolvesTask(
	  ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    Task INTEGER REFERENCES Task(AID),
    Student VARCHAR(10) REFERENCES Student(SID),
    Time DOUBLE,
    Answer VARCHAR(100));
    
    
    
insert into Student(sid, Password, Firstname, Lastname) values("testUser", "1234", "Marcel", "Noack");
insert into Student(sid, Password, Firstname, Lastname) values("testUser2", "1234", "Marcel", "Noack");

insert into Teacher(lid, Password, Firstname, Lastname) values("testLehrer", "5678", "Olaf", "Müller");

insert into Subject(fid, Abbrev) values("Mathematik", "MA");
insert into Subject(fid, Abbrev) values("Englisch", "EN");

insert into TeacherTeaches(Teacher, Subject) values("testLehrer", "Mathematik");

insert into Category(kid, Subject) values("Grundschulmathematik", "Mathematik");
insert into Category(kid, Subject) values("Funktionen", "Mathematik");

insert into block(bid, Teacher, Category) values('Matheaufgaben: Einfach', "testLehrer", "Grundschulmathematik");
insert into block(bid, Teacher, Category) values('Matheaufgaben: Fortgeschritten', "testLehrer", "Grundschulmathematik");
insert into block(bid, Teacher, Category) values('Matheaufgaben: Experte', "testLehrer", "Grundschulmathematik");

insert into Task(block, Question) values('Matheaufgaben: Einfach', "Was ist 1 + 1?");
insert into Task(block, Question) values('Matheaufgaben: Einfach', "Was ist die 1.Ableitung von x²?");
insert into Task(block, Question) values('Matheaufgaben: Einfach', "Die Seitenlängen eines Dreiecks betragen 5cm, 6cm und 2cm. Wie groß ist der Umfang dieses Dreiecks?");

insert into Task(block, Question) values('Matheaufgaben: Fortgeschritten', "Was ist 1 + 2?");
insert into Task(block, Question) values('Matheaufgaben: Fortgeschritten', "Was ist 1 + 3?");
insert into Task(block, Question) values('Matheaufgaben: Fortgeschritten', "Was ist 1 + 4?");

insert into Answer(Answertext, Correct, Task) values("2", true, 1);
insert into Answer(Answertext, Correct, Task) values("3", false, 1);
insert into Answer(Answertext, Correct, Task) values("4", false, 1);
insert into Answer(Answertext, Correct, Task) values("x", false, 2);
insert into Answer(Answertext, Correct, Task) values("2x", true, 2);
insert into Answer(Answertext, Correct, Task) values("2", false, 2);
insert into Answer(Answertext, Correct, Task) values("30cm", false, 3);
insert into Answer(Answertext, Correct, Task) values("10cm", false, 3);
insert into Answer(Answertext, Correct, Task) values("13cm", true, 3);
insert into Answer(Answertext, Correct, Task) values("1", false, 4);
insert into Answer(Answertext, Correct, Task) values("2", false, 4);
insert into Answer(Answertext, Correct, Task) values("3", true, 4);
insert into Answer(Answertext, Correct, Task) values("2", false, 5);
insert into Answer(Answertext, Correct, Task) values("4", true, 5);
insert into Answer(Answertext, Correct, Task) values("3", false, 5);
insert into Answer(Answertext, Correct, Task) values("1", false, 6);
insert into Answer(Answertext, Correct, Task) values("10", false, 6);
insert into Answer(Answertext, Correct, Task) values("5", true, 6);


insert into StudentSolvesBlock(Student, block) values("testUser", "Matheaufgaben: Einfach");
insert into StudentSolvesBlock(Student, block) values("testUser2", "Matheaufgaben: Einfach");
insert into StudentSolvesBlock(Student, block) values("testUser", "Matheaufgaben: Fortgeschritten");

insert into StudentSolvesTask(Task, Student, Answer) values(1, "testUser", "3");
insert into StudentSolvesTask(Task, Student, Answer) values(2, "testUser", "2x");
insert into StudentSolvesTask(Task, Student, Answer) values(3, "testUser", "10cm");
insert into StudentSolvesTask(Task, Student, Answer) values(1, "testUser2", "3");
insert into StudentSolvesTask(Task, Student, Answer) values(2, "testUser2", "x");
insert into StudentSolvesTask(Task, Student, Answer) values(3, "testUser2", "10cm");

select StudentSolvesTask.Student, StudentSolvesTask.Answer, Answer.Correct from StudentSolvesTask
join Task on StudentSolvesTask.Task = Task.aid
join Student on StudentSolvesTask.Student = Student.SID
join Answer on Task.aid = Answer.Task
where Task.Question = "Was ist 1 + 1?" and Answer.Answertext = StudentSolvesTask.Answer and StudentSolvesTask.Student = "testUser";

select * from StudentSolvesBlock
join block on StudentSolvesBlock.block = block.bid
join Teacher on block.Teacher = Teacher.lid;

select Task.Question from Task
join block on Task.block = block.bid
where block.bid = "Matheaufgaben: Experte";

select Answertext from Answer
join Task on Answer.Task = Task.aid
join block on Task.block = block.bid
where block.bid = "Matheaufgaben: Einfach" and Task.Question= "Was ist 1 + 1?";





