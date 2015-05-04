--liquibase formatted sql

--changeset boa:1
create SCHEMA IF NOT EXISTS FINKRATZEN;

create table FINKRATZEN.BOA_CHECKING (
  BANK_ID varchar(100) not null,
  POSTING_DATE date not null,
  AMOUNT decimal(19,4),
  DESCRIPTION varchar(200),
  RECORD_CREATED datetime default current_timestamp(),
  primary key (BANK_ID, POSTING_DATE)
);
--rollback drop table FINKATZEN.BOA_CHECKING;

--changeset boa:2
create table FINKRATZEN.LOG (
   id      int identity(1,1) primary key not null
  ,when   datetime not null
  ,level  varchar(32) not null
  ,msg    varchar(4096) not null
  ,logger varchar(200) not null
  ,thread varchar(200) not null
  ,ndc varchar(1000)
);
--rollback drop table FINKRATZEN.LOG;

--changeset boa:3
create table FINKRATZEN.CATEGORY (
  name varchar(100) primary key not null
);

create table FINKRATZEN.CATEGORY_TRAINING (
  category varchar(100)
  ,data varchar(500)
  ,foreign key (category) references FINKRATZEN.CATEGORY(name)
);

insert into FINKRATZEN.CATEGORY values
  ('cisd-payroll'),
  ('tcs-payroll'),
  ('utilities'),
  ('groceries'),
  ('life-insurance'),
  ('car-insurance'),
  ('mortgage'),
  ('restaurant');

insert into FINKRATZEN.CATEGORY_TRAINING values
  ('cisd-payroll', 'CFBISD'),
  ('tcs-payroll', 'CONTAINER STORE'),
  ('utilities', 'CITY OF COPPELL  DES:Water Bill'),
  ('utilities', 'ATMOS ENERGY RCR DES:UTIL PYMT'),
  ('utilities', 'TXU ENERGY'),
  ('groceries', 'COSTCO WHSE'),
  ('groceries', 'WALGREENS'),
  ('groceries', 'MARKET STREET'),
  ('groceries', 'WHOLEFDS'),
  ('life-insurance', 'NEW YORK LIFE'),
  ('car-insurance', 'ALLSTATE'),
  ('mortgage', 'WF HOME MTG');

--rollback drop table FINKRATZEN.CATEGORY_TRAINING;
--rollback drop table FINKRATZEN.CATEGORIES;

--changeset boa:4
create table FINKRATZEN.BOA_CHECKING_CATEGORY (
  bank_id varchar(100) not null,
  posting_date date not null,
  category varchar(500),
  foreign key (category) references FINKRATZEN.CATEGORY(name),
  foreign key (bank_id) references FINKRATZEN.BOA_CHECKING(bank_id)
--   foreign key (posting_date) references FINKRATZEN.BOA_CHECKING(posting_date)
);

--rollback drop table FINKRATZEN.BOA_CHECKING_CATEGORY