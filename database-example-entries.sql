insert into Student values('ID1', 'Anna');
insert into Student values('ID2', 'Stephano');
insert into Student values('ID3', 'George');

insert into Course(code, name, points) values('CODE1', 'Biologi', 15);
insert into Course values('CODE2', 'Mathematics', 15);
insert into Course values('CODE3', 'History', 15);

insert into Studies values('CODE3', 'ID3');
insert into Studies values('CODE2', 'ID1');

insert into Studied values('CODE1', 'ID1', 'A');
insert into Studied values('CODE2', 'ID1', 'C');