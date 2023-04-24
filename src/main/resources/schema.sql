--DROP TABLE IF EXISTS films_ratings CASCADE;
--DROP TABLE IF EXISTS films CASCADE;
--DROP TABLE IF EXISTS genre CASCADE;
--DROP TABLE IF EXISTS films_category CASCADE;
--DROP TABLE IF EXISTS users CASCADE;
--DROP TABLE IF EXISTS users_friends_status CASCADE;
--DROP TABLE IF EXISTS user_likes_for_films CASCADE;


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
	genre_id int NOT NULL UNIQUE REFERENCES genre(genre_id) ON delete CASCADE
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
