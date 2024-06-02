CREATE TABLE teams (
    team_id INT NOT NULL
);

CREATE TABLE team_available_locations (
    team_id INT NOT NULL,
    location_id INT NOT NULL
);

CREATE TABLE team_available_assignments (
    assignment_id INT NOT NULL,
    team_id INT NOT NULL,
    location_id INT NOT NULL,
    assignment TEXT
);