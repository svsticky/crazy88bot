CREATE TABLE users (
    user_id BIGINT NOT NULL,
    user_type VARCHAR(32) NOT NULL,
    team_id INT DEFAULT NULL,
    helper_station_id INT DEFAULT NULL,
    PRIMARY KEY (user_id)
);