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
    addr1           VARCHAR(300)    NOT NULL DEFAULT '',
    addr2           VARCHAR(300)    NOT NULL DEFAULT '',
    area_code       INT             NOT NULL,
    category_1      CHAR(3),
    category_2      CHAR(5),
    category_3      CHAR(9),
    content_type_id VARCHAR(50)     NOT NULL,
    first_image     VARCHAR(500)    NOT NULL DEFAULT '',
    second_image    VARCHAR(500)    NOT NULL DEFAULT '',
    longitude       DOUBLE          NOT NULL,
    latitude        DOUBLE          NOT NULL,
    sigungucode     CHAR(3)         NOT NULL DEFAULT '',
    tel             VARCHAR(300),
    title           VARCHAR(300)
);

CREATE TABLE story_bases
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    uuid       VARCHAR(36) NOT NULL DEFAULT (UUID()),
    author_id  INT         NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_story_bases_users_author_id FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE stories
(
    id                  INT PRIMARY KEY AUTO_INCREMENT,
    uuid                VARCHAR(36) NOT NULL DEFAULT (UUID()),
    story_base_id       INT         NOT NULL,
    version             INT         NOT NULL,
    status              VARCHAR(10) NOT NULL,
    title               VARCHAR(30) NOT NULL,
    description         TEXT,
    sido                VARCHAR(10),
    gungu               VARCHAR(10),
    image_uri           VARCHAR(500),
    thumbnail_image_uri VARCHAR(500),
    created_at          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at         TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_stories_story_bases_story_base_id FOREIGN KEY (story_base_id) REFERENCES story_bases (id) ON DELETE CASCADE
);

CREATE TABLE story_playings
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    uuid       VARCHAR(36) NOT NULL DEFAULT (UUID()),
    story_id   INT         NOT NULL,
    player_id  INT         NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cleared_at TIMESTAMP,
    CONSTRAINT fk_story_playings_stories_story_id FOREIGN KEY (story_id) REFERENCES stories (id) ON DELETE CASCADE,
    CONSTRAINT fk_story_playings_users_player_id FOREIGN KEY (player_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE spots
(
    id                        INT PRIMARY KEY AUTO_INCREMENT,
    uuid                      VARCHAR(36) NOT NULL DEFAULT (UUID()),
    story_id                  INT         NOT NULL,
    latitude                  DOUBLE      NOT NULL,
    longitude                 DOUBLE      NOT NULL,
    order_index               DOUBLE      NOT NULL,
    title                     VARCHAR(50) NOT NULL,
    description               TEXT,
    image_uri                 VARCHAR(500),
    thumbnail_image_uri       VARCHAR(500),
    created_at                TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at               TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    event_type                VARCHAR(10),
    event_image_uri           VARCHAR(500),
    event_thumbnail_image_uri VARCHAR(500),
    json_event_content        TEXT,
    CONSTRAINT fk_spots_story_story_id FOREIGN KEY (story_id) REFERENCES stories (id) ON DELETE CASCADE
)