-- Table to store users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Table to store chatrooms
CREATE TABLE IF NOT EXISTS chatrooms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    owner_id BIGINT NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES users(id),
    UNIQUE (name, owner_id)
);

-- Table to store messages
CREATE TABLE IF NOT EXISTS messages (
    id BIGSERIAL PRIMARY KEY,
    author_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    text TEXT NOT NULL,
    datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (room_id) REFERENCES chatrooms(id)
);

-- Table to track which users are in which chatrooms
CREATE TABLE IF NOT EXISTS user_chatrooms (
    user_id BIGINT NOT NULL,
    chatroom_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, chatroom_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (chatroom_id) REFERENCES chatrooms(id)
); 