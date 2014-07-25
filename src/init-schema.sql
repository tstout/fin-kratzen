--liquibase formatted sql

--changeset boa:1
create schema if not exists finkratzen;
--rollback drop schema finkratzen;


create table finkratzen.BOA_CHECKING (
  BANK_ID varchar(100) not null,
  POSTING_DATE date not null,
  AMOUNT decimal(19,4),
  RECORD_CREATED datetime default current_timestamp(),
  primary key (BANK_ID, POSTING_DATE)
);
--rollback drop table finkratzen.BOA_CHECKING;