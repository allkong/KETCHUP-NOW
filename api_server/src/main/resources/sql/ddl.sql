create table if not exists double_bean.attractions
(
    id              int                     not null
        primary key,
    addr1           varchar(300) default '' not null,
    addr2           varchar(300) default '' not null,
    area_code       int                     not null,
    category_1      char(3)                 null,
    category_2      char(5)                 null,
    category_3      char(9)                 null,
    content_type_id varchar(50)             not null,
    first_image     varchar(200) default '' not null,
    second_image    varchar(200) default '' not null,
    longitude       double                  not null,
    latitude        double                  not null,
    sigungucode     char(3)      default '' not null,
    tel             varchar(300)            null,
    title           varchar(300)            null
)
    charset = utf8mb4;

create table if not exists double_bean.users
(
    id          int auto_increment
        primary key,
    uuid        varchar(36) default (uuid())          not null,
    login_id    varchar(32)                           not null,
    nickname    varchar(32)                           not null,
    created_at  timestamp   default CURRENT_TIMESTAMP not null,
    modified_at timestamp   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint unique_users_login_id
        unique (login_id),
    constraint unique_users_nickname
        unique (nickname),
    constraint unique_users_uuid
        unique (uuid)
)
    charset = utf8mb4;

create table if not exists double_bean.passwords
(
    id          int auto_increment
        primary key,
    user_id     int                                 not null,
    password    varchar(128)                        not null,
    salt        varchar(128)                        not null,
    modified_at timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint unique_passwords_user_id
        unique (user_id),
    constraint fk_passwords_users_user_id
        foreign key (user_id) references double_bean.users (id)
            on delete cascade
)
    charset = utf8mb4;

create table if not exists double_bean.story_bases
(
    id         int auto_increment
        primary key,
    uuid       varchar(36) default (uuid())          not null,
    author_id  int                                   not null,
    created_at timestamp   default CURRENT_TIMESTAMP not null,
    constraint fk_story_bases_users_author_id
        foreign key (author_id) references double_bean.users (id)
            on delete cascade
)
    charset = utf8mb4;

create table if not exists double_bean.stories
(
    id                  int auto_increment
        primary key,
    uuid                varchar(36) default (uuid())          not null,
    story_base_id       int                                   not null,
    version             int                                   not null,
    status              varchar(10)                           not null,
    title               varchar(30)                           not null,
    description         mediumtext                            null,
    sido                varchar(10)                           null,
    gungu               varchar(10)                           null,
    image_uri           varchar(500)                          null,
    thumbnail_image_uri varchar(500)                          null,
    created_at          timestamp   default CURRENT_TIMESTAMP not null,
    modified_at         timestamp   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint fk_stories_story_bases_story_base_id
        foreign key (story_base_id) references double_bean.story_bases (id)
            on delete cascade
)
    charset = utf8mb4;

create table if not exists double_bean.spots
(
    id                        int auto_increment
        primary key,
    uuid                      varchar(36) default (uuid())          not null,
    story_id                  int                                   not null,
    latitude                  double                                not null,
    longitude                 double                                not null,
    order_index               double                                not null,
    title                     varchar(50)                           not null,
    description               text                                  null,
    image_uri                 varchar(500)                          null,
    thumbnail_image_uri       varchar(500)                          null,
    created_at                timestamp   default CURRENT_TIMESTAMP not null,
    modified_at               timestamp   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    event_type                varchar(10)                           null,
    event_image_uri           varchar(500)                          null,
    event_thumbnail_image_uri varchar(500)                          null,
    json_event_content        text                                  null,
    constraint fk_spots_story_story_id
        foreign key (story_id) references double_bean.stories (id)
            on delete cascade
);

create table if not exists double_bean.story_playings
(
    id         int auto_increment
        primary key,
    uuid       varchar(36) default (uuid())          not null,
    story_id   int                                   not null,
    player_id  int                                   not null,
    created_at timestamp   default CURRENT_TIMESTAMP not null,
    cleared_at timestamp                             null,
    constraint fk_story_playings_stories_story_id
        foreign key (story_id) references double_bean.stories (id)
            on delete cascade,
    constraint fk_story_playings_users_player_id
        foreign key (player_id) references double_bean.users (id)
            on delete cascade
);

create table if not exists double_bean.story_reviews
(
    id          int auto_increment primary key,
    uuid        varchar(36) default (uuid())          not null,
    story_id    int                                   not null,
    user_id     int                                   not null,
    title       varchar(50)                           not null,
    content     text,
    score       int                                   not null,
    created_at  timestamp   default CURRENT_TIMESTAMP not null,
    modified_at timestamp   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint fk_story_reviews_stories_story_id foreign key (story_id) references double_bean.stories (id) on delete cascade,
    constraint fk_story_reviews_users_user_id foreign key (user_id) references double_bean.users (id) on delete cascade
)