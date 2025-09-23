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

