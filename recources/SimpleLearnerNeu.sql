/*DROP DATABASE SimpleLearnerMockup;*/
CREATE DATABASE SimpleLearnerMockup;
USE SimpleLearnerMockup;

CREATE TABLE Schueler(
	SID VARCHAR(10) PRIMARY KEY,
    Passwort VARCHAR(10) NOT NULL,
    Vorname VARCHAR(20) NOT NULL,
    Nachname VARCHAR(20) NOT NULL);
    
CREATE TABLE Lehrer(
	LID VARCHAR(10) PRIMARY KEY,
    Passwort VARCHAR(10) NOT NULL,
    Vorname VARCHAR(20) NOT NULL,
    Nachname VARCHAR(20) NOT NULL);
    
CREATE TABLE Fach(
	FID VARCHAR(20) PRIMARY KEY,
    Kuerzel VARCHAR(4) NOT NULL UNIQUE);
    
CREATE TABLE LehrerUnterrichtet(
	Lehrer VARCHAR(10) REFERENCES Lehrer(LID),
    Fach VARCHAR(10) REFERENCES Fach(FID),
    PRIMARY KEY(Lehrer, Fach));
    
CREATE TABLE Kategorie(
	KID VARCHAR(100) PRIMARY KEY,
    Fach VARCHAR(10) REFERENCES Fach(FID));

CREATE TABLE Block(
	BID VARCHAR(100) PRIMARY KEY,
    Lehrer VARCHAR(10) REFERENCES Lehrer(LID),
    Kategorie VARCHAR(100) REFERENCES Kategorie(KID));
 
CREATE TABLE Aufgabe(
	AID INTEGER PRIMARY KEY AUTO_INCREMENT,
    Block VARCHAR(100) NOT NULL,
    CONSTRAINT fk_Block Foreign Key (Block) REFERENCES Block(bid) ON UPDATE CASCADE,
    Frage VARCHAR(500) NOT NULL);
    
CREATE TABLE Antwort(
	AID INTEGER PRIMARY KEY AUTO_INCREMENT,
    Antworttext VARCHAR(100) NOT NULL,
    isTrue BOOLEAN NOT NULL,
    Aufgabe INTEGER REFERENCES Aufgabe(AID));

CREATE TABLE SchuelerLoestBlock(
    Schueler VARCHAR(10) REFERENCES Schueler(SID),
    Block VARCHAR(100) REFERENCES Block(BID),
    CONSTRAINT SLBID PRIMARY KEY(Schueler, Block),
    Zeit DOUBLE);
	
CREATE TABLE SchuelerLoestAufgabe(
	SLAID INTEGER PRIMARY KEY AUTO_INCREMENT,
    Aufgabe INTEGER REFERENCES Aufgabe(AID),
    Schueler VARCHAR(10) REFERENCES Schueler(SID),
    Zeit DOUBLE,
    antwortS VARCHAR(100));
    
    
    
insert into schueler(sid, passwort, vorname, nachname) values("testUser", "1234", "Marcel", "Noack");
insert into schueler(sid, passwort, vorname, nachname) values("testUser2", "1234", "Marcel", "Noack");

insert into lehrer(lid, passwort, vorname, nachname) values("testLehrer", "5678", "Olaf", "Müller");

insert into fach(fid, kuerzel) values("Mathematik", "MA");
insert into fach(fid, kuerzel) values("Englisch", "EN");

insert into lehrerunterrichtet(lehrer, fach) values("testLehrer", "Mathematik");

insert into kategorie(kid, fach) values("Grundschulmathematik", "Mathematik");
insert into kategorie(kid, fach) values("Funktionen", "Mathematik");

insert into block(bid, lehrer, kategorie) values('Matheaufgaben: Einfach', "testLehrer", "Grundschulmathematik");
insert into block(bid, lehrer, kategorie) values('Matheaufgaben: Fortgeschritten', "testLehrer", "Grundschulmathematik");
insert into block(bid, lehrer, kategorie) values('Matheaufgaben: Experte', "testLehrer", "Grundschulmathematik");

insert into aufgabe(block, frage) values('Matheaufgaben: Einfach', "Was ist 1 + 1?");
insert into aufgabe(block, frage) values('Matheaufgaben: Einfach', "Was ist die 1.Ableitung von x²?");
insert into aufgabe(block, frage) values('Matheaufgaben: Einfach', "Die Seitenlängen eines Dreiecks betragen 5cm, 6cm und 2cm. Wie groß ist der Umfang dieses Dreiecks?");

insert into aufgabe(block, frage) values('Matheaufgaben: Fortgeschritten', "Was ist 1 + 2?");
insert into aufgabe(block, frage) values('Matheaufgaben: Fortgeschritten', "Was ist 1 + 3?");
insert into aufgabe(block, frage) values('Matheaufgaben: Fortgeschritten', "Was ist 1 + 4?");

insert into antwort(antworttext, istrue, aufgabe) values("2", true, 1);
insert into antwort(antworttext, istrue, aufgabe) values("3", false, 1);
insert into antwort(antworttext, istrue, aufgabe) values("4", false, 1);
insert into antwort(antworttext, istrue, aufgabe) values("x", false, 2);
insert into antwort(antworttext, istrue, aufgabe) values("2x", true, 2);
insert into antwort(antworttext, istrue, aufgabe) values("2", false, 2);
insert into antwort(antworttext, istrue, aufgabe) values("30cm", false, 3);
insert into antwort(antworttext, istrue, aufgabe) values("10cm", false, 3);
insert into antwort(antworttext, istrue, aufgabe) values("13cm", true, 3);
insert into antwort(antworttext, istrue, aufgabe) values("1", false, 4);
insert into antwort(antworttext, istrue, aufgabe) values("2", false, 4);
insert into antwort(antworttext, istrue, aufgabe) values("3", true, 4);
insert into antwort(antworttext, istrue, aufgabe) values("2", false, 5);
insert into antwort(antworttext, istrue, aufgabe) values("4", true, 5);
insert into antwort(antworttext, istrue, aufgabe) values("3", false, 5);
insert into antwort(antworttext, istrue, aufgabe) values("1", false, 6);
insert into antwort(antworttext, istrue, aufgabe) values("10", false, 6);
insert into antwort(antworttext, istrue, aufgabe) values("5", true, 6);


insert into schuelerloestblock(schueler, block) values("testUser", "Matheaufgaben: Einfach");
insert into schuelerloestblock(schueler, block) values("testUser2", "Matheaufgaben: Einfach");
insert into schuelerloestblock(schueler, block) values("testUser", "Matheaufgaben: Fortgeschritten");

insert into schuelerloestaufgabe(aufgabe, schueler, antwortS) values(1, "testUser", "3");
insert into schuelerloestaufgabe(aufgabe, schueler, antwortS) values(2, "testUser", "2x");
insert into schuelerloestaufgabe(aufgabe, schueler, antwortS) values(3, "testUser", "10cm");
insert into schuelerloestaufgabe(aufgabe, schueler, antwortS) values(1, "testUser2", "3");
insert into schuelerloestaufgabe(aufgabe, schueler, antwortS) values(2, "testUser2", "x");
insert into schuelerloestaufgabe(aufgabe, schueler, antwortS) values(3, "testUser2", "10cm");

select schuelerloestaufgabe.schueler, schuelerloestaufgabe.antwortS, antwort.isTrue from schuelerloestaufgabe
join aufgabe on schuelerloestaufgabe.aufgabe = aufgabe.aid
join schueler on schuelerloestaufgabe.schueler = schueler.SID
join antwort on aufgabe.aid = antwort.aufgabe
where aufgabe.frage = "Was ist 1 + 1?" and antwort.antworttext = schuelerloestaufgabe.antwortS and schuelerloestaufgabe.schueler = "testUser";

select * from schuelerloestblock
join block on schuelerloestblock.block = block.bid
join lehrer on block.lehrer = lehrer.lid;

select aufgabe.frage from aufgabe
join block on aufgabe.block = block.bid
where block.bid = "Matheaufgaben: Experte";

select antworttext from antwort
join aufgabe on antwort.aufgabe = aufgabe.aid
join block on aufgabe.block = block.bid
where block.bid = "Matheaufgaben: Einfach" and aufgabe.frage= "Was ist 1 + 1?";





