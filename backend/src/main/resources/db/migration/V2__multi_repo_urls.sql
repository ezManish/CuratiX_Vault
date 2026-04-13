-- ============================================================
-- V2: Replace single repo_url on boards with a one-to-many
--     board_repo_urls table so a board can have multiple repos.
-- ============================================================

-- 1. Create the new join table
CREATE TABLE IF NOT EXISTS board_repo_urls (
    id       BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    board_id BIGINT        NOT NULL,
    url      VARCHAR(500)  NOT NULL,
    CONSTRAINT fk_bru_board FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE
);

CREATE INDEX idx_bru_board ON board_repo_urls(board_id);

-- 2. Migrate existing single repo_url values into the new table (skip NULLs/blanks)
INSERT INTO board_repo_urls (board_id, url)
SELECT id, repo_url
FROM boards
WHERE repo_url IS NOT NULL AND TRIM(repo_url) != '';

-- 3. Drop the old column
ALTER TABLE boards DROP COLUMN repo_url;
