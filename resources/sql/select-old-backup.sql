select
  id
  ,file_name
from
  finkratzen.backup_files
where
  RECORD_CREATED <= getdate() - 30