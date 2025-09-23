create database mini_sosmed_restful_api;

create table users(
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY (username)
) ENGINE InnoDB;

create table posts(
    id VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL,
    body TEXT,
    image_url VARCHAR(100),
    status VARCHAR(100),
    created_at VARCHAR(100),
    primary key (id),
    foreign key fk_users_post (username) references users (username)
) ENGINE InnoDB;

create table comments(
    id VARCHAR(100) NOT NULL,
    post_id VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL,
    body VARCHAR(100),
    created_at VARCHAR(100),
    primary key (id),
    foreign key fk_users_comment (username) references users (username),
    foreign key fk_posts_comment (post_id) references posts (id)
) ENGINE InnoDB;

create table likes(
    id VARCHAR(100) NOT NULL,
    post_id VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL,
    foreign key fk_users_likes (username) references users (username),
    foreign key fk_posts_likes (post_id) references posts (id)
) ENGINE InnoDB;

select * from users;
select * from posts;
select * from comments;
select * from likes;

-- 1. Tambahkan kolom id baru
ALTER TABLE users
ADD COLUMN id VARCHAR(100) NOT NULL;

-- 2. Ubah username menjadi UNIQUE, bukan PK
ALTER TABLE users
MODIFY COLUMN username VARCHAR(100) NOT NULL UNIQUE;

-- 3. Jadikan id sebagai primary key
ALTER TABLE users
DROP PRIMARY KEY,
ADD PRIMARY KEY (id);

-- 4. (Opsional) tambah kolom email jika belum ada
ALTER TABLE users
MODIFY COLUMN email VARCHAR(100) UNIQUE;

ALTER TABLE posts ADD COLUMN user_id VARCHAR(100) NOT NULL;
ALTER TABLE comments ADD COLUMN user_id VARCHAR(100) NOT NULL;
ALTER TABLE likes ADD COLUMN user_id VARCHAR(100) NOT NULL;

UPDATE posts p
JOIN users u ON p.username = u.username
SET p.user_id = u.id;

UPDATE comments c
JOIN users u ON c.username = u.username
SET c.user_id = u.id;

UPDATE likes l
JOIN users u ON l.username = u.username
SET l.user_id = u.id;

ALTER TABLE posts DROP FOREIGN KEY posts_ibfk_1;
ALTER TABLE posts ADD CONSTRAINT posts_ibfk_1 FOREIGN KEY (user_id) REFERENCES users(id)

ALTER TABLE comments DROP FOREIGN KEY comments_ibfk_1;
ALTER TABLE comments ADD CONSTRAINT comments_ibfk_1 FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE likes DROP COLUMN username;

ALTER TABLE likes DROP FOREIGN KEY likes_ibfk_1;
ALTER TABLE likes ADD CONSTRAINT likes_ibfk_1 FOREIGN KEY (user_id) REFERENCES users(id);

SELECT
    constraint_name, table_name
FROM
    information_schema.table_constraints
WHERE
    table_schema = 'mini_sosmed_restful_api' -- ganti dengan nama DB kamu
    AND table_name = 'likes'
    AND constraint_type = 'FOREIGN KEY';

