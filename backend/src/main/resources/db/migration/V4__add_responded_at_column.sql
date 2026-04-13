-- Fix schema mapping for BoardInvitationEntity
-- Add missing responded_at column to board_invitations

ALTER TABLE board_invitations ADD COLUMN responded_at DATETIME;
