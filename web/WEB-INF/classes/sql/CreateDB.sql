/*
Database for RSS feeds reader
 */

-- Users table
CREATE TABLE IF NOT EXISTS USER (id int not null auto_increment primary key,
                                 name     varchar(255),
                                 login    varchar(255),
                                 password varchar(255)
                                );

-- Feed channels table
CREATE TABLE IF NOT EXISTS CHANNEL (id int not null auto_increment primary key,
                                    rssLink varchar(255),
                                    title varchar(255),
                                    link varchar(255),
                                    description text
                                   );

-- Feed items table
CREATE TABLE IF NOT EXISTS ITEM (guid varchar(255) not null primary key,
                                 title varchar(255),
                                 link varchar(255),
                                 description text,
                                 pubDate timestamp,

                                 fk_channel_id int,

                                 foreign key(fk_channel_id) references channel(id) on delete cascade
                                );

-- User feeds table
CREATE TABLE IF NOT EXISTS USER_CHANNEL (id int not null auto_increment primary key,

                                         fk_user_id int,
                                         fk_channel_id int,

                                         foreign key(fk_user_id) references user(id) on delete cascade,
                                         foreign key(fk_channel_id) references channel(id) on delete cascade,

--                                         constraint pk_user_channel_id primary key (fk_user_id, fk_channel_id)
                                        );

-- User items table
CREATE TABLE IF NOT EXISTS USER_ITEM (isRead boolean not null default false,
--                        fk_user_id int,
                        fk_user_channel_id int,
                        fk_item_guid varchar(255),

--                        foreign key(fk_user_id) references user(id) on delete cascade,
                        foreign key(fk_user_channel_id) references user_channel(id) on delete cascade,
                        foreign key(fk_item_guid) references item(guid) on delete cascade,

--                        constraint pk_item_id primary key (fk_user_id, fk_item_guid)
                        );
