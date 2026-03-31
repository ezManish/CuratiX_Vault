**CURATIX VAULT**

Team Data & Hackathon Resource Platform

**Product Requirements Document**

Version 1.0 | March 2026

Status: Draft

Prepared by: Manish

# 1\. Project Overview

CuratiX Vault is a lightweight, full-stack web platform that gives hackathon teams a single place to store, organize, and access all their team data - member profiles, hackathon records, GitHub links, project files, PPTs, and more. Think of it as a private Notion-lite built specifically for the hackathon workflow, where everything lives in one structured Board instead of scattered across WhatsApp groups, Google Drive folders, and personal notes.

| **Attribute**     | **Detail**                                                    |
| ----------------- | ------------------------------------------------------------- |
| **Project Name**  | CuratiX Vault                                                 |
| **Version**       | 1.0 (MVP)                                                     |
| **Platform**      | Web (React SPA + Spring Boot REST API)                        |
| **Primary Users** | Manish's team; extensible to other college hackathon teams    |
| **Core Unit**     | Board - a shared workspace for one hackathon team             |
| **Auth Provider** | Firebase Authentication (Google OAuth + Email/Password)       |
| **File Storage**  | Cloudinary (free tier - 25 GB storage, 25 GB/month bandwidth) |
| **Database**      | MySQL (via Aiven free tier or Railway)                        |
| **Target Scale**  | Up to 100 Daily Active Users on free-tier infrastructure      |

# 2\. Problem Statement

During hackathons, team data gets spread across multiple platforms:

- Member info lives in personal contacts or WhatsApp group descriptions
- GitHub / LinkedIn links get shared in chats and are hard to find later
- PPTs and project files end up in multiple Drive folders with no clear structure
- Enrollment numbers and admission IDs have to be re-asked every time a form needs filling
- No central record of which hackathon the team participated in, their idea, or their outcome

CuratiX Vault solves this by providing a structured, permission-controlled Board per team, accessible by all members, where every piece of data and file lives in one organized location.

# 3\. Goals & Non-Goals

## 3.1 Goals (In Scope - MVP)

- Create and manage team Boards with owner-controlled access
- Store structured member profiles inside a Board
- Attach and view files (PPTs, docs, images) per Board
- Granular access control: Owner, Editor, Viewer
- Invite members via shareable link or email-based invite
- View and search Board data in a clean UI
- Export Board member data as CSV
- Works well on desktop browsers; responsive for mobile

## 3.2 Non-Goals (Out of Scope for v1)

- Real-time collaborative editing (Google Docs-style concurrent editing)
- Native mobile app (React Native)
- Payment or subscription features
- Integration with external hackathon platforms (Devfolio, Unstop, etc.)
- AI-powered features (idea generation, auto-fill)
- Email notification system (deferred to v2)

# 4\. User Roles & Permissions

Every Board has exactly one Owner and any number of Editors or Viewers. Permissions are Board-scoped, not global.

| **Permission**            | **Owner** | **Editor** | **Viewer** | **Notes**                      |
| ------------------------- | --------- | ---------- | ---------- | ------------------------------ |
| Create Board              | ✅        | ❌         | ❌         | Any logged-in user can create  |
| Delete Board              | ✅        | ❌         | ❌         | Owner only                     |
| Invite Members            | ✅        | ✅         | ❌         | Editor can invite Viewers only |
| Change Member Role        | ✅        | ❌         | ❌         | Owner only                     |
| Remove Member             | ✅        | ❌         | ❌         | Owner only                     |
| Add / Edit Member Profile | ✅        | ✅         | ❌         | Members can edit own profile   |
| Delete Member Profile     | ✅        | ✅         | ❌         |                                |
| Upload / Delete Files     | ✅        | ✅         | ❌         |                                |
| View All Data & Files     | ✅        | ✅         | ✅         |                                |
| Export Board (CSV)        | ✅        | ✅         | ✅         |                                |
| Generate Invite Link      | ✅        | ✅         | ❌         | Link scoped to a role          |

# 5\. Core Features (Detailed)

## 5.1 Authentication

Handled entirely by Firebase Authentication. No custom auth code on the backend - the Spring Boot API only receives and verifies the Firebase ID Token (JWT) on every request.

- Sign in with Google (OAuth 2.0) - one-click, no password
- Sign in with Email + Password - fallback option
- Token verified server-side using Firebase Admin SDK
- Session persists via Firebase SDK on the frontend
- No rate limit issues - Firebase Spark plan handles 10,000 auth operations/month easily

## 5.2 Board Management

A Board is the top-level container. It represents one team across one or multiple hackathons. Each Board has:

- Board Name (e.g., 'Team Nexus - SynapHack 2026')
- Description / tagline
- Cover color or emoji (cosmetic)
- Visibility: Private (invite-only) - all Boards are private in v1
- Owner (auto-assigned to creator)
- Creation date, last updated timestamp

A user can be a member of multiple Boards with different roles in each.

## 5.3 Member Profiles

Each person added to a Board has a structured Member Profile. This is separate from their platform account - it's the hackathon-specific data the team needs. Fields:

| **Field**                | **Type**           | **Notes**                          |
| ------------------------ | ------------------ | ---------------------------------- |
| **Full Name**            | Text               | Required                           |
| **Admission Number**     | Text               | College-issued ID                  |
| **Enrollment Number**    | Text               | University enrollment ID           |
| **Email**                | Email              | Auto-filled from Firebase account  |
| **Phone Number**         | Text               | Optional                           |
| **GitHub Profile URL**   | URL                | Clickable link                     |
| **LinkedIn Profile URL** | URL                | Clickable link                     |
| **GitHub Repo URL**      | URL                | Project-specific repo              |
| **Role in Team**         | Text               | e.g., Frontend, ML, Full-stack     |
| **Year / Branch**        | Text               | Academic info                      |
| **Profile Photo**        | Image (Cloudinary) | Optional upload                    |
| **Skills / Tags**        | Tag list           | Comma-separated, e.g., React, Java |
| **Bio / Notes**          | Text area          | Free text, 500 chars max           |

## 5.4 Hackathon Details Section

Each Board can store one or more Hackathon Records - structured entries about which hackathon the team participated in. Fields:

- Hackathon Name (e.g., SynapHack 3.0)
- Platform (Devfolio / Unstop / College Portal / Other)
- Date & Venue
- Theme / Domain
- Team Name at that event
- Problem Statement selected
- Project Idea / One-liner pitch
- Result (Participated / Shortlisted / Top N / Winner)
- Prize / Reward if any
- Submission URL
- Notes / Learnings

## 5.5 File & Material Storage

Each Board has a Files section where team materials can be uploaded. Files are stored on Cloudinary (free: 25 GB storage, 25 GB bandwidth/month).

- Supported types: PDF, PPT/PPTX, DOC/DOCX, PNG, JPG, MP4 (demo videos)
- Each file has a Label (e.g., 'Final PPT', 'Architecture Diagram')
- Files can be tagged: Presentation / Documentation / Design / Code / Other
- One-click download and direct preview link
- Files can be deleted by Owner or Editor
- Upload size limit: 25 MB per file

## 5.6 Invite System

Two ways to bring people into a Board:

- Shareable Invite Link - Owner/Editor generates a role-scoped link (e.g., join as Viewer). Link expires after 7 days or after N uses (configurable). When clicked, user logs in and is auto-added to Board with the role.
- Email Invite - Owner enters email address; the system sends an email with the invite link (via a simple SMTP service like Resend.com - free for up to 3,000 emails/month). User clicks link, logs in, and joins.

## 5.7 Search & Filter

- Search members by name, GitHub handle, enrollment number, or skill tag
- Filter Board list by hackathon name or date
- Filter files by type or tag

## 5.8 CSV Export

Any Board member (including Viewer) can export the member profile table as a CSV file. Useful for filling hackathon registration forms quickly.

# 6\. Technology Stack

| **Layer**            | **Technology**                       | **Reason**                            |
| -------------------- | ------------------------------------ | ------------------------------------- |
| **Frontend**         | React (Vite) + Tailwind CSS          | Fast dev, familiar stack              |
| **State Management** | Zustand or React Query               | Lightweight, no Redux boilerplate     |
| **Backend**          | Spring Boot 3 (Java 21)              | Your primary stack, production-ready  |
| **Database**         | MySQL 8 via Aiven (free tier)        | Familiar, relational, structured data |
| **ORM**              | Spring Data JPA (Hibernate)          | Standard with Spring Boot             |
| **Auth**             | Firebase Auth (Admin SDK on backend) | Free, supports 100 DAU, Google OAuth  |
| **File Storage**     | Cloudinary Java SDK                  | 25 GB free, no cold starts like S3    |
| **Email**            | Resend.com (SMTP / REST API)         | 3,000 emails/month free               |
| **Deployment - FE**  | Vercel (free)                        | Zero-config React deploy              |
| **Deployment - BE**  | Railway or Render (free tier)        | Spring Boot JAR deployment            |
| **Version Control**  | GitHub (private repo)                | Standard                              |

# 7\. Database Schema (High-Level)

All tables use MySQL. Firebase UID is stored as the user identifier - no separate password management.

## 7.1 Core Tables

### users

| **Column**       | **Type**     | **Notes**                    |
| ---------------- | ------------ | ---------------------------- |
| **id**           | BIGINT PK    | Auto-increment               |
| **firebase_uid** | VARCHAR(128) | Unique, from Firebase token  |
| **email**        | VARCHAR(255) | Unique                       |
| **display_name** | VARCHAR(255) | From Firebase profile        |
| **photo_url**    | VARCHAR(500) | Firebase profile picture URL |
| **created_at**   | DATETIME     | Auto-set on insert           |

### boards

| **Column**                  | **Type**     | **Notes**           |
| --------------------------- | ------------ | ------------------- |
| **id**                      | BIGINT PK    | Auto-increment      |
| **name**                    | VARCHAR(255) | Board title         |
| **description**             | TEXT         | Optional            |
| **cover_color**             | VARCHAR(7)   | Hex color, cosmetic |
| **owner_id**                | BIGINT FK    | FK → users.id       |
| **is_deleted**              | BOOLEAN      | Soft delete         |
| **created_at / updated_at** | DATETIME     | Audit timestamps    |

### board_members

| **Column**    | **Type**  | **Notes**                   |
| ------------- | --------- | --------------------------- |
| **id**        | BIGINT PK |                             |
| **board_id**  | BIGINT FK | FK → boards.id              |
| **user_id**   | BIGINT FK | FK → users.id               |
| **role**      | ENUM      | 'OWNER', 'EDITOR', 'VIEWER' |
| **joined_at** | DATETIME  |                             |

### member_profiles

Stores hackathon-specific data for each person in a Board. One user can have different profiles in different Boards.

| **Column**                             | **Type**     | **Notes**                                               |
| -------------------------------------- | ------------ | ------------------------------------------------------- |
| **id**                                 | BIGINT PK    |                                                         |
| **board_id**                           | BIGINT FK    | FK → boards.id                                          |
| **user_id**                            | BIGINT FK    | FK → users.id (nullable - can add non-platform members) |
| **full_name**                          | VARCHAR(255) | Required                                                |
| **admission_no**                       | VARCHAR(50)  |                                                         |
| **enrollment_no**                      | VARCHAR(50)  |                                                         |
| **email**                              | VARCHAR(255) |                                                         |
| **phone**                              | VARCHAR(20)  | Optional                                                |
| **github_url, linkedin_url, repo_url** | VARCHAR(500) | URLs                                                    |
| **role_in_team**                       | VARCHAR(100) | e.g., Backend Dev                                       |
| **year_branch**                        | VARCHAR(100) | e.g., 3rd Year CSE                                      |
| **skills**                             | TEXT         | Comma-separated string                                  |
| **bio**                                | TEXT         | Max 500 chars                                           |
| **photo_url**                          | VARCHAR(500) | Cloudinary URL                                          |

### hackathon_records

| **Column**                       | **Type**       | **Notes**                                     |
| -------------------------------- | -------------- | --------------------------------------------- |
| **id**                           | BIGINT PK      |                                               |
| **board_id**                     | BIGINT FK      | FK → boards.id                                |
| **hackathon_name**               | VARCHAR(255)   | Required                                      |
| **platform**                     | VARCHAR(100)   | Devfolio, Unstop, etc.                        |
| **date, venue**                  | DATE / VARCHAR |                                               |
| **theme, team_name**             | VARCHAR(255)   |                                               |
| **problem_statement**            | TEXT           |                                               |
| **project_idea**                 | TEXT           | One-liner pitch                               |
| **result**                       | ENUM           | 'PARTICIPATED','SHORTLISTED','TOP_N','WINNER' |
| **submission_url, prize, notes** | TEXT / VARCHAR |                                               |

### board_files

| **Column**               | **Type**      | **Notes**                                              |
| ------------------------ | ------------- | ------------------------------------------------------ |
| **id**                   | BIGINT PK     |                                                        |
| **board_id**             | BIGINT FK     | FK → boards.id                                         |
| **uploaded_by**          | BIGINT FK     | FK → users.id                                          |
| **label**                | VARCHAR(255)  | e.g., 'Final PPT'                                      |
| **file_type**            | ENUM          | 'PRESENTATION','DOCUMENTATION','DESIGN','CODE','OTHER' |
| **cloudinary_url**       | VARCHAR(1000) | Public URL from Cloudinary                             |
| **cloudinary_public_id** | VARCHAR(500)  | For deletion via API                                   |
| **file_size_kb**         | INT           |                                                        |
| **uploaded_at**          | DATETIME      |                                                        |

### invite_links

| **Column**               | **Type**    | **Notes**                                 |
| ------------------------ | ----------- | ----------------------------------------- |
| **id**                   | BIGINT PK   |                                           |
| **board_id**             | BIGINT FK   |                                           |
| **token**                | VARCHAR(64) | UUID or random hex - used in the link URL |
| **role**                 | ENUM        | Role granted to user who uses this link   |
| **created_by**           | BIGINT FK   | FK → users.id                             |
| **expires_at**           | DATETIME    | 7 days from creation                      |
| **max_uses / use_count** | INT         | Optional usage cap                        |
| **is_active**            | BOOLEAN     | Owner can revoke                          |

# 8\. REST API Design (Key Endpoints)

All endpoints require a valid Firebase ID Token in the Authorization header: Authorization: Bearer &lt;firebaseIdToken&gt;

| **Method** | **Endpoint**                                  | **Auth Required** | **Description**                            |
| ---------- | --------------------------------------------- | ----------------- | ------------------------------------------ |
| **POST**   | /api/auth/sync                                | Firebase Token    | Sync Firebase user to MySQL on first login |
| **GET**    | /api/boards                                   | Firebase Token    | List all boards for current user           |
| **POST**   | /api/boards                                   | Firebase Token    | Create a new Board                         |
| **GET**    | /api/boards/{boardId}                         | Member            | Get Board details                          |
| **PUT**    | /api/boards/{boardId}                         | Owner             | Update Board metadata                      |
| **DELETE** | /api/boards/{boardId}                         | Owner             | Soft delete Board                          |
| **GET**    | /api/boards/{boardId}/members                 | Member            | List all member profiles                   |
| **POST**   | /api/boards/{boardId}/members                 | Owner/Editor      | Add a member profile                       |
| **PUT**    | /api/boards/{boardId}/members/{memberId}      | Owner/Editor/Self | Update member profile                      |
| **DELETE** | /api/boards/{boardId}/members/{memberId}      | Owner/Editor      | Remove member profile                      |
| **PUT**    | /api/boards/{boardId}/members/{memberId}/role | Owner             | Change member role                         |
| **GET**    | /api/boards/{boardId}/files                   | Member            | List Board files                           |
| **POST**   | /api/boards/{boardId}/files                   | Owner/Editor      | Upload file to Cloudinary                  |
| **DELETE** | /api/boards/{boardId}/files/{fileId}          | Owner/Editor      | Delete file from Cloudinary + DB           |
| **GET**    | /api/boards/{boardId}/hackathons              | Member            | List hackathon records                     |
| **POST**   | /api/boards/{boardId}/hackathons              | Owner/Editor      | Add hackathon record                       |
| **PUT**    | /api/boards/{boardId}/hackathons/{hId}        | Owner/Editor      | Update hackathon record                    |
| **POST**   | /api/boards/{boardId}/invite/link             | Owner/Editor      | Generate invite link                       |
| **POST**   | /api/invite/join/{token}                      | Firebase Token    | Join Board via invite link                 |
| **GET**    | /api/boards/{boardId}/export/csv              | Member            | Download member data as CSV                |

# 9\. UI Screens & User Flows

## 9.1 Screen List

| **Screen**               | **Description**                                                                                      |
| ------------------------ | ---------------------------------------------------------------------------------------------------- |
| **Login / Landing**      | Google Sign In button + Email/Password form. Redirects to Dashboard after auth.                      |
| **Dashboard**            | List of all Boards the user is a member of. 'Create New Board' CTA. Shows role badge per Board.      |
| **Board - Overview**     | Summary cards: member count, hackathon count, file count. Quick access to each section.              |
| **Board - Members**      | Table/grid of all member profiles with search, filter, and CSV export. Click to expand full profile. |
| **Member Profile Modal** | Full profile view with all fields. Edit mode for Owner/Editor. Links are clickable.                  |
| **Board - Hackathons**   | Cards per hackathon record. Expandable detail. Add/edit form for Owner/Editor.                       |
| **Board - Files**        | File cards with label, type badge, uploader, date. Upload button for Editor/Owner.                   |
| **Board - Settings**     | Rename Board, change color, manage members (roles, remove), generate invite links, delete Board.     |
| **Invite Join Page**     | Landing page for invite links. Shows Board name and role being granted. CTA: 'Join with Google'.     |
| **User Account Page**    | Profile photo, display name, email. Log out button.                                                  |

## 9.2 Key User Flows

### Flow A: Create Board and Invite Team

- User signs in with Google
- Clicks 'Create New Board' on Dashboard → fills Board name + description → Board created
- Goes to Board Settings → Generate Invite Link → selects role (Editor or Viewer) → copies link
- Shares link with teammates on WhatsApp/Discord
- Teammate opens link → signs in → auto-joins Board with assigned role

### Flow B: Add Member Profile

- Editor opens Board → Members tab → 'Add Member'
- Fills in form: name, admission no, enrollment no, GitHub, LinkedIn, etc.
- Optionally uploads profile photo → saved to Cloudinary
- Member appears in the Board member table immediately

### Flow C: Upload a PPT

- Editor goes to Board → Files tab → 'Upload File'
- Selects file (PPT), enters label (e.g., 'Final Pitch Deck'), selects tag 'Presentation'
- File uploaded to Cloudinary, URL stored in DB
- File card appears with download link and preview URL

# 10\. Non-Functional Requirements

| **Requirement**     | **Target**                                                                                          |
| ------------------- | --------------------------------------------------------------------------------------------------- |
| **Performance**     | API response < 500ms for non-file endpoints under normal load                                       |
| **Scale**           | Supports 100 DAU comfortably on free-tier infra (Aiven MySQL + Railway BE + Vercel FE)              |
| **Security**        | All endpoints protected by Firebase token verification. Role check before every mutating operation. |
| **Data Privacy**    | Boards are private by default. No public Board discovery. Board data only visible to members.       |
| **Reliability**     | Aiven free-tier MySQL has daily backups. Cloudinary files are replicated by Cloudinary.             |
| **Responsiveness**  | Usable on mobile browsers (responsive layout). Not a native app.                                    |
| **Browser Support** | Chrome, Firefox, Safari - latest 2 versions each.                                                   |

# 11\. Development Milestones

| **Phase** | **Name**               | **Deliverables**                                                                             | **Est. Duration** |
| --------- | ---------------------- | -------------------------------------------------------------------------------------------- | ----------------- |
| **1**     | Project Setup          | Spring Boot init, MySQL schema, Firebase Admin setup, React + Vite scaffold, Tailwind config | 2-3 days          |
| **2**     | Auth Flow              | Firebase Auth (Google + Email), token verification filter, /auth/sync endpoint, login screen | 2 days            |
| **3**     | Board CRUD             | Create/list/delete boards, Dashboard UI, Board overview screen                               | 3 days            |
| **4**     | Member Profiles        | Add/edit/delete member profiles, Members tab UI, search functionality                        | 4 days            |
| **5**     | Hackathon Records      | Add/edit hackathon records, Hackathons tab UI                                                | 2 days            |
| **6**     | File Upload            | Cloudinary integration, file upload/delete, Files tab UI                                     | 3 days            |
| **7**     | Invite System          | Generate invite links, join-via-link flow, Board Settings UI                                 | 3 days            |
| **8**     | Roles & Access Control | Role-based UI gating, backend permission middleware, role management UI                      | 2 days            |
| **9**     | CSV Export + Polish    | CSV download, responsive fixes, loading states, error handling, empty states                 | 2 days            |
| **10**    | Deployment             | Deploy to Vercel (FE) + Railway (BE), Aiven MySQL (prod), environment variables              | 1 day             |

Total estimated time: ~24-26 days of focused development. Can be done faster if building solo and skipping email invites initially (link-only invites for v1).

# 12\. Future Enhancements (v2+)

- Email notifications - invite emails, Board activity digest
- Public Board mode - discoverable boards for open teams
- Activity Log - audit trail of who changed what and when
- React Native mobile app - iOS/Android
- AI auto-fill - paste GitHub profile URL, auto-extract name, bio, skills
- Analytics dashboard - hackathon win rate, skills heatmap across team
- Integration with Devfolio / Unstop - auto-import hackathon details
- Board templates - pre-filled structure for common hackathon workflows
- Two-factor auth / passkeys

# 13\. Risks & Mitigations

| **Risk**                                                | **Likelihood**                               | **Mitigation**                                                                  |
| ------------------------------------------------------- | -------------------------------------------- | ------------------------------------------------------------------------------- |
| Cloudinary free tier bandwidth exceeded (25 GB/month)   | Low for 100 DAU, Medium if many large files  | Enforce 25 MB upload cap; compress images on upload                             |
| Railway/Render free tier cold starts (BE goes to sleep) | High - free tiers spin down after inactivity | Use UptimeRobot to ping BE every 10 mins to keep warm                           |
| Firebase Auth token expiry not handled on FE            | Medium                                       | Use Firebase onIdTokenChanged to auto-refresh tokens                            |
| Scope creep - adding features mid-build                 | High (typical)                               | Strictly follow milestone order. Log ideas in a backlog, don't build mid-sprint |
| Aiven free MySQL storage limit (5 GB)                   | Very Low at current scale                    | Monitor via Aiven dashboard; migrate to paid if needed                          |

CuratiX Vault PRD v1.0 | Prepared by Manish | March 2026 | Confidential