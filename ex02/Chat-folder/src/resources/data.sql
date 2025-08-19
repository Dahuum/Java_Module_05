-- Insert some users
INSERT INTO users (login, password) VALUES 
('alice', 'password123'),
('bob', 'password456'), 
('charlie', 'password789'),
('diana', 'password321'),
('eve', 'password654');

-- Insert some chatrooms (owner_id references users.id)
INSERT INTO chatrooms (name, owner_id) VALUES
('General', 1),    -- Alice owns General
('Random', 2),     -- Bob owns Random
('Tech Talk', 1),  -- Alice owns Tech Talk
('Gaming', 3),     -- Charlie owns Gaming
('Study Group', 4); -- Diana owns Study Group

-- Insert some messages
INSERT INTO messages (author_id, room_id, text) VALUES
(1, 1, 'Welcome to the general chat!'),  -- Alice in General
(2, 1, 'Thanks Alice!'),                -- Bob in General
(3, 2, 'Anyone up for a game?'),        -- Charlie in Random
(1, 3, 'Check out this new framework'), -- Alice in Tech Talk
(4, 5, 'Study session at 3pm');         -- Diana in Study Group

-- Add users to chatrooms (many-to-many relationship)
INSERT INTO user_chatrooms (user_id, chatroom_id) VALUES
(1, 1), (2, 1), (3, 1),  -- Alice, Bob, Charlie in General
(2, 2), (3, 2),          -- Bob, Charlie in Random  
(1, 3), (4, 3),          -- Alice, Diana in Tech Talk
(3, 4), (5, 4),          -- Charlie, Eve in Gaming
(1, 5), (4, 5), (5, 5);  -- Alice, Diana, Eve in Study Group
