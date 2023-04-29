drop table IF EXISTS films_ratings CASCADE;
drop table IF EXISTS films CASCADE;
drop table IF EXISTS genre CASCADE;
drop table IF EXISTS films_category CASCADE;
drop table IF EXISTS users CASCADE;
drop table IF EXISTS users_friends_status CASCADE;
drop table IF EXISTS user_likes_for_films CASCADE;
drop table IF EXISTS director CASCADE;
drop table IF EXISTS director_films CASCADE;
drop table IF EXISTS review CASCADE;
drop table IF EXISTS review_likes CASCADE;
drop table IF EXISTS user_feeds CASCADE;


create TABLE IF NOT EXISTS films_ratings (
	rating_id serial NOT NULL PRIMARY KEY,
	name varchar(255) NOT NULL UNIQUE 
);

create TABLE IF NOT EXISTS films (
	film_id serial NOT NULL PRIMARY KEY,
	rating_id int NOT NULL REFERENCES films_ratings(rating_id) ON delete CASCADE,
	name varchar(255) NOT NULL,
	description varchar(255) NOT NULL, 
	release_date date NOT NULL,
	duration int NOT NULL
);

create TABLE IF NOT EXISTS genre (
	genre_id serial NOT NULL PRIMARY KEY ,
	name varchar(255) NOT NULL UNIQUE
);

create TABLE IF NOT EXISTS films_category (
	film_id int NOT NULL REFERENCES films(film_id) ON delete CASCADE,
	genre_id int NOT NULL REFERENCES genre(genre_id) ON delete CASCADE
);

create TABLE IF NOT EXISTS users (
	user_id serial NOT NULL PRIMARY KEY,
	email varchar(255) NOT NULL UNIQUE,
	login varchar(255) NOT NULL UNIQUE,
	name varchar(255) NOT NULL,
	birthday date NOT NULL
);

create TABLE IF NOT EXISTS users_friends_status (
	user_id int NOT NULL REFERENCES users(user_id) ON delete CASCADE,
	friend_id int NOT NULL REFERENCES users(user_id) ON delete CASCADE,
	friendship_status boolean NOT null
);

create TABLE IF NOT EXISTS user_likes_for_films(
	film_id int NOT NULL REFERENCES films(film_id) ON delete CASCADE,
	user_id int NOT NULL REFERENCES users(user_id) ON delete CASCADE
);

create TABLE IF NOT EXISTS director (
	director_id serial NOT NULL PRIMARY KEY,
	name varchar(255) NOT NULL UNIQUE
);

create TABLE IF NOT EXISTS director_films (
	film_id int NOT NULL REFERENCES films(film_id) ON delete CASCADE,
	director_id int NOT NULL REFERENCES director(director_id) ON delete CASCADE
);

create table IF NOT EXISTS review (
    review_id serial NOT NULL PRIMARY KEY,
    content varchar(255) NOT NULL,
    is_positive boolean,
    film_id int NOT NULL REFERENCES films(film_id) ON delete CASCADE,
	user_id int NOT NULL REFERENCES users(user_id) ON delete CASCADE,
	useful int
);
--
create table IF NOT EXISTS review_likes (
    review_id int NOT NULL REFERENCES review(review_id) ON delete CASCADE,
    user_id int NOT NULL REFERENCES users(user_id) ON delete CASCADE,
    review_like boolean NOT NULL
);

create TABLE IF NOT EXISTS user_feeds (
    event_id serial NOT NULL PRIMARY KEY,
    user_id int NOT NULL REFERENCES users(user_id) ON delete CASCADE,
    time_stamp bigint NOT NULL,
    event_type varchar(8) NOT NULL,
    operation_type varchar(8) NOT NULL,
    entity_id int NOT NULL
);
