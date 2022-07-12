### java-filmorate

## ER-диаграмма базы данных проекта Filmorate
<img height="350" src="/Users/anna/dev/java-filmorate/src/main/resources/static/QuickDBD-export (2).png" width="600"/>

## Query examples

### Get all films
SELECT * </br>
FROM films;

### Get film by id
SELECT * </br>
FROM films </br>
WHERE film_id=?;

### Get 10 most popular films
SELECT f.* </br>
FROM films AS f </br>
LEFT JOIN film_likes AS fl ON f.film_id=fl.film_id </br>
GROUP BY f.film_id </br>
ORDER BY COUNT(fl.user_id) DESC </br>
LIMIT 10;

### Get all users
SELECT * </br>
FROM users;

### Get user by id
SELECT * </br> 
FROM users </br>
WHERE user_id=?;

### Get friends of some user
SELECT * </br>
FROM users </br>
WHERE user_id IN (SELECT accept_user_id </br>
FROM friends </br>
WHERE request_user_id=?);

### Get common friends of two users
SELECT * </br>
FROM users </br>
WHERE user_id IN (SELECT accept_user_id </br>
FROM friends </br>
WHERE request_user_id=? </br> 
AND accept_user_id IN ( </br>
SELECT accept_user_id </br>
FROM friends </br>
WHERE request_user_id=?));