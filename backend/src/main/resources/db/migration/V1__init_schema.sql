-- ============================================================
-- CuratiX Vault — Final Active MySQL Schema
-- ============================================================

-- Users table: synced from Firebase on first login, extended profile fields
CREATE TABLE IF NOT EXISTS users (
    id           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    firebase_uid VARCHAR(128) NOT NULL UNIQUE,
    email        VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(255),
    photo_url    VARCHAR(500),
    admission_no VARCHAR(50),
    enrollment_no VARCHAR(50),
    phone        VARCHAR(20),
    github_url   VARCHAR(500),
    linkedin_url VARCHAR(500),
    bio          TEXT,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Boards table: unified container for a Hackathon Project (Merged from V8)
CREATE TABLE IF NOT EXISTS boards (
    id                BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(255) NOT NULL,
    description       TEXT,
    cover_color       VARCHAR(7)   DEFAULT '#6366f1',
    cover_emoji       VARCHAR(10),
    owner_id          BIGINT       NOT NULL,
    is_deleted        BOOLEAN      NOT NULL DEFAULT FALSE,
    platform          VARCHAR(100),
    event_date        DATE,
    venue             VARCHAR(255),
    theme             VARCHAR(255),
    team_name         VARCHAR(255),
    problem_statement TEXT,
    project_idea      TEXT,
    result            ENUM('PARTICIPATED', 'SHORTLISTED', 'TOP_N', 'WINNER') DEFAULT 'PARTICIPATED',
    prize             VARCHAR(255),
    submission_url    VARCHAR(500),
    repo_url          VARCHAR(500),
    notes             TEXT,
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_board_owner FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- Board members: who has access to which board, at what role
CREATE TABLE IF NOT EXISTS board_members (
    id         BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    board_id   BIGINT      NOT NULL,
    user_id    BIGINT      NOT NULL,
    role       ENUM('OWNER','EDITOR','VIEWER') NOT NULL DEFAULT 'VIEWER',
    joined_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_board_user (board_id, user_id),
    CONSTRAINT fk_bm_board FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE,
    CONSTRAINT fk_bm_user  FOREIGN KEY (user_id)  REFERENCES users(id)  ON DELETE CASCADE
);

-- Member profiles: hackathon-specific data per person per board
CREATE TABLE IF NOT EXISTS member_profiles (
    id                   BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    board_id             BIGINT       NOT NULL,
    user_id              BIGINT,
    full_name            VARCHAR(255) NOT NULL,
    admission_no         VARCHAR(50),
    enrollment_no        VARCHAR(50),
    email                VARCHAR(255),
    phone                VARCHAR(20),
    github_url           VARCHAR(500),
    linkedin_url         VARCHAR(500),
    repo_url             VARCHAR(500),
    role_in_team         VARCHAR(100),
    year_branch          VARCHAR(100),
    skills               TEXT,
    bio                  TEXT,
    photo_url            VARCHAR(500),
    cloudinary_public_id VARCHAR(500),
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_mp_board_user (board_id, user_id),
    CONSTRAINT fk_mp_board FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE,
    CONSTRAINT fk_mp_user  FOREIGN KEY (user_id)  REFERENCES users(id)  ON DELETE SET NULL
);

-- Board files: uploaded to Cloudinary, metadata stored here
CREATE TABLE IF NOT EXISTS board_files (
    id                   BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    board_id             BIGINT        NOT NULL,
    uploaded_by          BIGINT        NOT NULL,
    label                VARCHAR(255),
    file_type            ENUM('PRESENTATION','DOCUMENTATION','DESIGN','CODE','OTHER') DEFAULT 'OTHER',
    cloudinary_url       VARCHAR(1000) NOT NULL,
    cloudinary_public_id VARCHAR(500)  NOT NULL,
    original_filename    VARCHAR(255),
    file_size_kb         INT,
    uploaded_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bf_board    FOREIGN KEY (board_id)    REFERENCES boards(id) ON DELETE CASCADE,
    CONSTRAINT fk_bf_uploader FOREIGN KEY (uploaded_by) REFERENCES users(id)
);

-- Invite links: link-only invites
CREATE TABLE IF NOT EXISTS invite_links (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    board_id    BIGINT       NOT NULL,
    token       VARCHAR(64)  NOT NULL UNIQUE,
    role        ENUM('EDITOR','VIEWER') NOT NULL DEFAULT 'VIEWER',
    created_by  BIGINT       NOT NULL,
    expires_at  DATETIME     NOT NULL,
    max_uses    INT,
    use_count   INT          NOT NULL DEFAULT 0,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_il_board   FOREIGN KEY (board_id)   REFERENCES boards(id) ON DELETE CASCADE,
    CONSTRAINT fk_il_creator FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Board invitations: Email-based pending invites
CREATE TABLE IF NOT EXISTS board_invitations (
    id              BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    board_id        BIGINT       NOT NULL,
    inviter_id      BIGINT       NOT NULL,
    recipient_email VARCHAR(255) NOT NULL,
    role            VARCHAR(50)  NOT NULL,
    status          VARCHAR(50)  NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_bi_board_email (board_id, recipient_email),
    CONSTRAINT fk_bi_board    FOREIGN KEY (board_id)   REFERENCES boards(id),
    CONSTRAINT fk_bi_inviter  FOREIGN KEY (inviter_id) REFERENCES users(id)
);

-- Indexes for common queries
CREATE INDEX idx_boards_owner     ON boards(owner_id);
CREATE INDEX idx_bm_board         ON board_members(board_id);
CREATE INDEX idx_bm_user          ON board_members(user_id);
CREATE INDEX idx_mp_board         ON member_profiles(board_id);
CREATE INDEX idx_bf_board         ON board_files(board_id);
CREATE INDEX idx_il_board         ON invite_links(board_id);
CREATE INDEX idx_il_token         ON invite_links(token);
