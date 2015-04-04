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

