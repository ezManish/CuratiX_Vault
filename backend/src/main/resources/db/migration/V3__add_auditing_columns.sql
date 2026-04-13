-- Add auditing columns to track WHO created and modified record
-- Using VARCHAR(128) to store Firebase UIDs

-- 1. Users table (Already altered in failed attempt)
-- ALTER TABLE users ADD COLUMN created_by VARCHAR(128);
-- ALTER TABLE users ADD COLUMN last_modified_by VARCHAR(128);
UPDATE users SET created_by = 'SYSTEM_INIT' WHERE created_by IS NULL;

-- 2. Boards table (Already altered in failed attempt)
-- ALTER TABLE boards ADD COLUMN created_by VARCHAR(128);
-- ALTER TABLE boards ADD COLUMN last_modified_by VARCHAR(128);
UPDATE boards b JOIN users u ON b.owner_id = u.id SET b.created_by = u.firebase_uid WHERE b.created_by IS NULL;

-- 3. Board Members table (Already altered in failed attempt)
-- ALTER TABLE board_members ADD COLUMN created_by VARCHAR(128);
-- ALTER TABLE board_members ADD COLUMN last_modified_by VARCHAR(128);
UPDATE board_members bm JOIN boards b ON bm.board_id = b.id JOIN users u ON b.owner_id = u.id SET bm.created_by = u.firebase_uid WHERE bm.created_by IS NULL;

-- 4. Member Profiles table (Already altered in failed attempt)
-- ALTER TABLE member_profiles ADD COLUMN created_by VARCHAR(128);
-- ALTER TABLE member_profiles ADD COLUMN last_modified_by VARCHAR(128);
UPDATE member_profiles mp JOIN users u ON mp.user_id = u.id SET mp.created_by = u.firebase_uid WHERE mp.created_by IS NULL;

-- 5. Board Files table (Likely already altered)
-- ALTER TABLE board_files ADD COLUMN created_by VARCHAR(128);
-- ALTER TABLE board_files ADD COLUMN last_modified_by VARCHAR(128);
UPDATE board_files bf JOIN users u ON bf.uploaded_by = u.id SET bf.created_by = u.firebase_uid WHERE bf.created_by IS NULL;

-- 6. Invite Links table (Likely already altered)
-- ALTER TABLE invite_links ADD COLUMN last_modified_by VARCHAR(128);

-- 7. Board Invitations table (Likely already altered)
-- ALTER TABLE board_invitations ADD COLUMN last_modified_by VARCHAR(128);
