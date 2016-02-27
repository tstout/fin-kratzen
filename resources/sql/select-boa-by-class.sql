select
	checking.BANK_ID
	,checking.POSTING_DATE
	,checking.DESCRIPTION
from
  FINKRATZEN.BOA_CHECKING checking
  inner join FINKRATZEN.BOA_CHECKING_CATEGORY map
  on checking.BANK_ID = map.BANK_ID
  and checking.POSTING_DATE = map.POSTING_DATE
where
  map.CATEGORY = ?