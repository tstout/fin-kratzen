select
	BANK_ID
	,POSTING_DATE
	,AMOUNT
	,DESCRIPTION
from
	finkratzen.BOA_CHECKING
where
	POSTING_DATE between getdate() - 7 and getdate()