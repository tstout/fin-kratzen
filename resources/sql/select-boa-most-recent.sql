select top 10
	checking.POSTING_DATE
	,checking.AMOUNT
	,checking.DESCRIPTION
from
  FINKRATZEN.BOA_CHECKING checking
order by
	checking.POSTING_DATE
desc