DROP TABLE IF EXISTS user_answered_questions;
DROP TABLE IF EXISTS scalping_quiz;
DROP TABLE IF EXISTS scalping_trivia;
DROP TABLE IF EXISTS users;
DROP SEQUENCE global_seq;
CREATE SEQUENCE global_seq;

CREATE TABLE users
(
    id         INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    chat_id    INTEGER UNIQUE                NOT NULL,
    name       TEXT                       NOT NULL,
    score      INTEGER             DEFAULT 0 NOT NULL,
    high_score INTEGER             DEFAULT 0 NOT NULL,
    bot_state  TEXT                       NOT NULL
);
CREATE UNIQUE INDEX users_unique_chatid_idx ON users (chat_id);

CREATE TABLE scalping_quiz
(
    id             INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    question       TEXT NOT NULL,
    answer_correct TEXT NOT NULL
);
CREATE TABLE scalping_trivia
(
    id             INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
    question       TEXT NOT NULL,
    answer_correct TEXT NOT NULL
);
