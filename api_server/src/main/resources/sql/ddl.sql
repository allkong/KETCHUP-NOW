USE double_bean;

CREATE TABLE users (
	id INT PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(36) NOT NULL DEFAULT (UUID()),
    login_id VARCHAR(32) NOT NULL,
    nickname VARCHAR(32) NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT unique_users_uuid UNIQUE (uuid),
    CONSTRAINT unique_users_login_id UNIQUE (login_id),
    CONSTRAINT unique_users_nickname UNIQUE (nickname)
);

CREATE TABLE passwords (
	id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    password VARCHAR(128) NOT NULL,
    salt VARCHAR(128) NOT NULL,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_passwords_users_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT unique_passwords_user_id UNIQUE (user_id)
);
