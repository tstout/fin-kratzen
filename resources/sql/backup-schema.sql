--liquibase formatted sql

--changeset backup:1

create table FINKRATZEN.BACKUP_FILES (
  ID varchar(200) primary key not null,
  FILE_NAME varchar(400) not null,
  RECORD_CREATED datetime default current_timestamp() not null
);

--changeset backup:2

create sequence FINKRATZEN.BACKUP_SEQ start with 0 increment by 1;

