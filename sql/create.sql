CREATE TABLE organization (
  id           BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name         VARCHAR(255) NOT NULL,
  display_name VARCHAR(255) NULL,
  locked       BOOLEAN      NOT NULL,
  deleted_at   TIMESTAMP    NULL,
  updated_at   TIMESTAMP    NOT NULL,
  created_at   TIMESTAMP    NOT NULL,
  UNIQUE (name)
);

CREATE TABLE organization_member (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  organization_id BIGINT    NOT NULL,
  user_id         BIGINT    NOT NULL,
  role            INT       NOT NULL,
  created_by      BIGINT    NOT NULL,
  updated_by      BIGINT    NOT NULL,
  created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (organization_id, user_id)
);

CREATE TABLE invitations (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  organization_id BIGINT    NOT NULL,
  user_id         BIGINT    NOT NULL,
  role            INT       NOT NULL,
  status          INT       NOT NULL,
  created_by      BIGINT    NOT NULL,
  created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
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
  id           BIGINT       NOT NULL AUTO_INCREMENT,
  role         INT          NOT NULL,
  password     VARCHAR(255) NOT NULL,
  name         VARCHAR(255) NOT NULL,
  display_name VARCHAR(255) NOT NULL,
  image        VARCHAR(255) NULL,
  deleted_at   TIMESTAMP    NULL,
  created_at   TIMESTAMP    NOT NULL,
  updated_at   TIMESTAMP    NOT NULL,
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

CREATE TABLE label (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  organization_id BIGINT       NOT NULL,
  name            VARCHAR(255) NOT NULL,
  UNIQUE (organization_id, name),
  FOREIGN KEY (organization_id) REFERENCES organization (id)
);

CREATE TABLE issue (
  id         BIGINT       NOT NULL AUTO_INCREMENT,
  created_by BIGINT       NOT NULL,
  title      VARCHAR(255) NOT NULL,
  body       TEXT         NOT NULL,
  closed_at  TIMESTAMP    NULL,
  created_at TIMESTAMP    NOT NULL,
  updated_at TIMESTAMP    NOT NULL,
  FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE milestone (
  id         BIGINT       NOT NULL AUTO_INCREMENT,
  state INT       NOT NULL,
  title      VARCHAR(255) NOT NULL,
  body       TEXT         NOT NULL,
  created_at TIMESTAMP    NOT NULL,
  updated_at TIMESTAMP    NOT NULL,
  closed_at  TIMESTAMP    NULL,
  FOREIGN KEY (user_id) REFERENCES user (id)
);
# assignee, milestone,
