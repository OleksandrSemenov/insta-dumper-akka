CREATE TABLE  fake_users (id  serial not null, password varchar(255) not null, user_name varchar(255) not null, primary key (id));
CREATE TABLE  followers (id  serial not null, url varchar(255) not null, user_id int4 not null, primary key (id));
CREATE TABLE  users (id  serial not null, avatar_url varchar(255), biography text, business_contact_method varchar(255), direct_messaging varchar(255), email varchar(255), external_lynx_url text, external_url text, follower_count int4, following_count int4, full_name varchar(255), geo_media_count int4, has_anonymous_profile_picture boolean, has_biography_translation boolean, has_chaining boolean, hd_profile_pic_url varchar(255), hd_profile_pic_versions text, is_business boolean, is_private boolean, is_scanned boolean, is_verified boolean, latitude float4, location varchar(255), longitude float4, media_count int4, phone_country_code varchar(255), phone_number varchar(255), pk int8, profile_pic_id varchar(255), street text, user_name varchar(255), user_tags_count int4, zip varchar(255), primary key (id));

ALTER TABLE followers
  ADD CONSTRAINT fk_followers_users FOREIGN KEY (user_id) REFERENCES users(id);

CREATE INDEX  fts_idx ON users USING GIN (to_tsvector('simple', full_name));