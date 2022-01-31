alter table user_service.users add column name varchar(255);

update user_service.users u set name = ou.provider_name from user_service.oauth_users ou where ou.id = u.id;

update user_service.users u
set name = first_name
from user_service.local_users lu
where lu.id = u.id
  and first_name is not null;

update user_service.users u
set name = last_name
from user_service.local_users lu
where lu.id = u.id
  and last_name is not null;

update user_service.users u
set name = concat(first_name, ' ', last_name)
from user_service.local_users lu
where lu.id = u.id
  and first_name is not null
  and last_name is not null;

