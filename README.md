# CuratiX Vault
### The Command Center for Hackathon Teams. One Board = One Project.

[![Backend](https://img.shields.io/badge/Backend-Render-brightgreen)](https://curatix-vault.onrender.com/swagger-ui/index.html)
[![Frontend](https://img.shields.io/badge/Frontend-Vercel-black)](https://curatix.co.in)
[![Domain](https://img.shields.io/badge/Domain-GoDaddy-blue)](https://curatix.co.in)
[![Java](https://img.shields.io/badge/Stack-Java_21-orange)](https://adoptium.net/)
[![React](https://img.shields.io/badge/Stack-React_19-blue)](https://react.dev/)

CuratiX Vault is an enterprise-grade workspace built specifically for hackathon teams. It eliminates the chaos of scattered files, unclear ownership, and messy submissions by organizing team data, project assets, roles, and logistics into a single structured system.

**Live Production Site:** https://curatix.co.in/  
**API Documentation:** https://curatix-vault.onrender.com/swagger-ui/index.html

---

## Workflow Overview

1. **Initialize Project**: Create a dedicated Board for your hackathon project.
2. **Onboard Team**: Invite teammates via secure, expiring links or direct email invitations.
3. **Define Scope**: Centralize problem statements, tech stacks, and critical submission links.
4. **Manage Roles**: Assign Owner, Editor, or Viewer permissions to maintain data integrity.
5. **Centralize Assets**: Upload design patterns, pitch decks, and codebases to a unified vault.
6. **Export & Submit**: Generate structured CSV reports for hackathon submission portals.

---

## Technical Features

### Board-Based Workspaces
Every project operates within its own isolation. Store comprehensive project details, track lifecycle milestones from participation to winning, and maintain a permanent record of hackathon achievements.

### Identity Synchronization
Configure your core identity — including university details, enrollment data, and social professional links (GitHub, LinkedIn) — a single time. Your profile synchronizes automatically across every board you join within the ecosystem.

### Fine-Grained Access Control
Implement Role-Based Access Control (RBAC) with Owner, Editor, and Viewer permissions. This ensures that sensitive project data is only modifiable by authorized team members.

### Resource Vault
Securely store and share critical project assets including architecture diagrams, presentation decks, and documentation. Powered by Cloudinary for high-availability storage.

### Data Intelligence & Export
Instant generation of team profiles and project metadata as structured CSV files. Designed to meet the requirements of major hackathon submission platforms.

---

## API Reference

Comprehensive API documentation is available via the interactive Swagger UI:  
https://curatix-vault.onrender.com/swagger-ui/index.html

### Core Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/boards | Retrieve all accessible project boards |
| POST | /api/boards/{id}/profiles | Synchronize or update regional member profiles |
| POST | /api/boards/{id}/files | Upload assets to the project vault |
| POST | /api/invite/join/{token} | Authenticate and join a team via token |
| GET | /api/health | System heartbeat and monitoring (24/7 Keep-Alive) |

---

## Technology Stack

### Backend Infrastructure
- **Language**: Java 21 LTS
- **Framework**: Spring Boot 3.4.4
- **Security**: Firebase Admin SDK (Stateless JWT Authentication)
- **Migrations**: Flyway (Automated DB versioning)
- **Database**: MySQL 8.x

### Frontend Architecture
- **Framework**: React 19 (TypeScript)
- **Build Tool**: Vite
- **State Management**: Zustand (Auth) and React Query (Server Sync)
- **Icons**: Lucide
- **UI Feedback**: React Hot Toast

---

## Local Development

### Backend Setup
1. Navigate to the `backend` directory.
2. Add `firebase-adminsdk.json` to the `src/main/resources` folder.
3. Configure your local `.env` with Database, Firebase, and Cloudinary credentials.
4. Execute `mvn spring-boot:run`.

### Frontend Setup
1. Navigate to the `frontend` directory.
2. Install dependencies: `npm install`.
3. Create a `.env` file with your Firebase public keys.
4. Set `VITE_API_BASE_URL=http://localhost:8080`.
5. Execute `npm run dev`.

---

## Operational Notes

**Cold Starts**: The backend is hosted on Render's free tier. Initial requests may experience a 30-60 second latency as the instance initializes. To maintain high availability, use a service like cron-job.org to ping the `/api/health` endpoint every 10 minutes.

---

## Future Roadmap

The following architectural enhancements are planned for future releases:
- AI-Driven PRD Generation: Integrated project requirement analysis.
- Real-Time Team Collaboration: Integrated messaging and live updates.
- Deep Repository Integration: Live commit tracking and PR synchronization.
- Automated Team Matching: Skill-based gap analysis and teammate suggestions.

---

*Authored by the CuratiX Development Team.*