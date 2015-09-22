CREATE TABLE room (
  id         BIGINT       NOT NULL AUTO_INCREMENT,
  user_id    BIGINT       NULL,
  password   VARCHAR(255) NULL,
  name       VARCHAR(255) NOT NULL,
  limitt     INT          NOT NULL,
  locked     BOOLEAN      NOT NULL,
  deleted_at TIMESTAMP    NULL,
  updated_at TIMESTAMP    NOT NULL,
  created_at TIMESTAMP    NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE session (
  id         BIGINT       NOT NULL AUTO_INCREMENT,
  token      VARCHAR(255) NOT NULL,
  timeout    BIGINT       NOT NULL,
  deleted_at TIMESTAMP    NULL,
  updated_at TIMESTAMP    NOT NULL,
  created_at TIMESTAMP    NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE user (
  id         BIGINT       NOT NULL AUTO_INCREMENT,
  role       INT          NOT NULL,
  password   VARCHAR(255) NOT NULL,
  name       VARCHAR(255) NOT NULL,
  sex        INT          NULL,
  prefecture VARCHAR(255) NULL,
  bio        VARCHAR(255) NULL,
  color      VARCHAR(255) NULL,
  image      VARCHAR(255) NULL,
  deleted_at TIMESTAMP    NULL,
  created_at TIMESTAMP    NOT NULL,
  updated_at TIMESTAMP    NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE room_user_authority (
  id         BIGINT    NOT NULL AUTO_INCREMENT,
  room_id    BIGINT    NOT NULL,
  user_id    BIGINT    NOT NULL,
  authority  INT       NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  UNIQUE (room_id, user_id, authority),
  FOREIGN KEY (room_id) REFERENCES room (id),
  FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE blacklist (
  id          BIGINT    NOT NULL AUTO_INCREMENT,
  room_id     BIGINT    NOT NULL,
  user_id     BIGINT    NOT NULL,
  operator_id BIGINT    NOT NULL,
  created_at  TIMESTAMP NOT NULL,
  updated_at  TIMESTAMP NOT NULL,
  UNIQUE (room_id, user_id),
  FOREIGN KEY (room_id) REFERENCES room (id),
  FOREIGN KEY (user_id) REFERENCES user (id),
  FOREIGN KEY (operator_id) REFERENCES user (id)
);

CREATE TABLE whitelist (
  id          BIGINT    NOT NULL AUTO_INCREMENT,
  room_id     BIGINT    NOT NULL,
  user_id     BIGINT    NOT NULL,
  operator_id BIGINT    NOT NULL,
  created_at  TIMESTAMP NOT NULL,
  updated_at  TIMESTAMP NOT NULL,
  UNIQUE (room_id, user_id),
  FOREIGN KEY (room_id) REFERENCES room (id),
  FOREIGN KEY (user_id) REFERENCES user (id),
  FOREIGN KEY (operator_id) REFERENCES user (id)
)
