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