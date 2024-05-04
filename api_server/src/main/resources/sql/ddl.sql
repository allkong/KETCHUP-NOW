USE double_bean;

CREATE TABLE users
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    uuid        VARCHAR(36) NOT NULL DEFAULT (UUID()),
    login_id    VARCHAR(32) NOT NULL,
    nickname    VARCHAR(32) NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT unique_users_uuid UNIQUE (uuid),
    CONSTRAINT unique_users_login_id UNIQUE (login_id),
    CONSTRAINT unique_users_nickname UNIQUE (nickname)
);

CREATE TABLE passwords
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    user_id     INT          NOT NULL,
    password    VARCHAR(128) NOT NULL,
    salt        VARCHAR(128) NOT NULL,
    modified_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_passwords_users_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT unique_passwords_user_id UNIQUE (user_id)
);

CREATE TABLE attractions
(
    id              INT PRIMARY KEY NOT NULL,
    addr1           VARCHAR(100)    NOT NULL DEFAULT '',
    addr2           VARCHAR(100)    NOT NULL DEFAULT '',
    area_code       INT             NOT NULL,
    category_1      CHAR(3),
    category_2      CHAR(5),
    category_3      CHAR(9),
    content_type_id VARCHAR(50)     NOT NULL,
    first_image     VARCHAR(200)    NOT NULL DEFAULT '',
    second_image    VARCHAR(200)    NOT NULL DEFAULT '',
    longitude       DOUBLE          NOT NULL,
    latitude        DOUBLE          NOT NULL,
    sigungucode     CHAR(3)         NOT NULL DEFAULT '',
    tel             VARCHAR(100),
    title           VARCHAR(100)
);
