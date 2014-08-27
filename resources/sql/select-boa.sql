select
  BANK_ID
  ,POSTING_DATE
  ,AMOUNT
  ,RECORD_CREATED
from
  finkratzen.BOA_CHECKING
where
  POSTING_DATE between ? and ?

