alter table user_service.oauth_users add column email varchar(255);

update user_service.oauth_users u set email = login where provider = 'GOOGLE' or provider = 'FACEBOOK';


