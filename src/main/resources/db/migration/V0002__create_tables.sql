CREATE TABLE users
(
    user_id      BIGINT GENERATED ALWAYS AS IDENTITY,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    email        VARCHAR(100) NOT NULL UNIQUE,
    password     TEXT         NOT NULL,
    phone_number VARCHAR(20),
    enabled      BOOLEAN DEFAULT FALSE,
    role         VARCHAR(20),
    created_at   TIMESTAMP,

    CONSTRAINT PK_USER_ID PRIMARY KEY (user_id)
);

CREATE TABLE sleep_sessions
(
    sleep_session_id BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id          BIGINT NOT NULL,
    date             DATE   NOT NULL,
    start_hour       TIME   NOT NULL,
    end_hour         TIME   NOT NULL,
    sleep_score      INTEGER CHECK (sleep_score >= 0 AND sleep_score <= 100),
    total_hours      NUMERIC(4, 2),

    CONSTRAINT PK_SLEEP_SESSION_ID PRIMARY KEY (sleep_session_id),
    CONSTRAINT FK_SLEEP_SESSION_USER_ID FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE goals
(
    goal_id              BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id              BIGINT        NOT NULL,
    expected_sleep_hours NUMERIC(4, 2) NOT NULL,
    expected_sleep_score INTEGER       NOT NULL CHECK (expected_sleep_score BETWEEN 0 AND 100),
    created_at           TIMESTAMP,

    CONSTRAINT PK_GOAL_ID PRIMARY KEY (goal_id),
    CONSTRAINT FK_GOAL_USER_ID FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE devices
(
    device_id        BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id          BIGINT UNIQUE NOT NULL,
    device_name      VARCHAR(100),
    model            VARCHAR(100),
    firmware_version VARCHAR(50),
    registered_at    TIMESTAMP,

    CONSTRAINT PK_DEVICE_ID PRIMARY KEY (device_id),
    CONSTRAINT FK_DEVICE_USER_ID FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);