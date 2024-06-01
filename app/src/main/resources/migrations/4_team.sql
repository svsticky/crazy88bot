CREATE TABLE teams (
    team_id INT NOT NULL,
    PRIMARY KEY (team_id)
);

CREATE TABLE team_available_locations (
    team_id INT NOT NULL,
    location_id INT NOT NULL,
    PRIMARY KEY (team_id, location_id)
);

CREATE TABLE team_available_assignments (
    team_id INT NOT NULL,
    location_id INT NOT NULL,
    assignment TEXT,
    PRIMARY KEY (team_id, location_id, assignment)
);