TRUNCATE TABLE games;

INSERT INTO games (name, developer, genre, year, file_size, os, image_url)
VALUES
    ('The Witcher 3', 'CD Projekt RED', 'RPG', 2015, 50.0, 'Windows', '/images/witcher3.png'),
    ('Cyberpunk 2077', 'CD Projekt RED', 'Action RPG', 2020, 70.0, 'Windows', '/images/cyberpunk.png'),
    ('Counter-Strike: Global Offensive', 'Valve', 'Shooter', 2012, 15.0, 'Windows, Mac, Linux', '/images/csgo.png');
