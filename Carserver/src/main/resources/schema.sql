CREATE TABLE user (
    userId TEXT PRIMARY KEY NOT NULL,
    configs TEXT,
    userKms INTEGER DEFAULT 0
);

CREATE TABLE firmware (
    id TEXT PRIMARY KEY NOT NULL,
    version INTEGER
);

CREATE TABLE car (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    configs TEXT,
    firmwareId TEXT,
    FOREIGN KEY(firmwareId) REFERENCES firmware(id)
);

CREATE TABLE audit (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    userId TEXT,
    carId INTEGER,
    kms INTEGER,
    FOREIGN KEY(userId) REFERENCES user(userId),
    FOREIGN KEY(carId) REFERENCES car(id)
);
