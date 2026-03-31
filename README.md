# CuratiX Vault

**CuratiX Vault** is an enterprise-grade, lightweight full-stack application designed specifically for hackathon teams and project builders. It acts as a central command station to organize team profiles, project assets, hackathon logistics, and collaborative resources under a unified **"One Board = One Project"** architecture.

Built with a focus on speed, high-fidelity UI (glassmorphism), and robust access control, CuratiX Vault eliminates the friction of managing hackathon data across scattered Google Docs, Discord channels, and messy folders.

---

## Key Features

*   **Project Dossiers (Boards)**: A single, organized dashboard for a specific hackathon project. Stores the problem statement, active ideas, event logistics, repository links, platform, and results.
*   **Global User Profiles**: Fill out your profile once. Whenever you join a new project board, your public identity (skills, university details, GitHub/LinkedIn) is automatically synchronized.
*   **Role-Based Access Control (RBAC)**: Securely manage who can view, edit, or own a project board. Invite collaborators via secure, expiring email links or direct token links.
*   **Asset Management**: Seamlessly upload architecture diagrams, pitch decks, and code snippets to Cloudinary, attached directly to your project board.
*   **Dynamic Data Export**: Instantly export your team profiles and board logistics into structured `.csv` files for hackathon submission portals without manual typing.

---

## Architecture & Tech Stack

This project was developed with a modern, decoupled architecture featuring a reactive UI and a highly optimized relational backend.

### Frontend
*   **Framework**: React 19 (TypeScript) + Vite
*   **Styling**: Vanilla CSS (CSS Modules) + TailwindCSS (for utility layout) + Glassmorphism UI
*   **State Management**: Zustand (Global Auth State) + React Query (Data Fetching)
*   **Icons & Alerts**: Lucide React + React Hot Toast
*   **Routing**: React Router DOM (v7)

### Backend
*   **Framework**: Java 21 + Spring Boot 3 + Spring Web
*   **Database**: MySQL 8.x
*   **ORM / Migrations**: Spring Data JPA + Hibernate + Flyway (V1 through V8)
*   **Security**: Spring Security + Stateless JWT validation
*   **External APIs**: Firebase Admin SDK (Authentication) + Cloudinary API (File Storage)

---

## Prerequisites

Before you begin, ensure you have the following installed and configured:
*   [Node.js](https://nodejs.org/en/) (v18+)
*   [Java Development Kit (JDK)](https://adoptium.net/) (v21+)
*   [Maven](https://maven.apache.org/)
*   [MySQL Server](https://dev.mysql.com/downloads/installer/) (Local or Cloud e.g., Aiven)
*   **Firebase Project**: You need a Firebase project with Google/Email Auth enabled. Download the `firebase-adminsdk.json` service account key.
*   **Cloudinary Account**: You need a free Cloudinary account for asset storage.

---

## Getting Started

### 1. Backend Setup (Spring Boot)
1.  Navigate to the backend directory:
    ```bash
    cd backend
    ```
2.  Configure your environment parameters:
    *   Set up your system environment variables OR create an `application-local.yml` overriding the `application.yml` properties:
        *   `DB_URL` (e.g., `jdbc:mysql://localhost:3306/curatix`)
        *   `DB_USER` and `DB_PASS`
        *   `FIREBASE_PROJECT_ID`
        *   `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`
3.  Place your `firebase-adminsdk.json` file inside `backend/src/main/resources/`. *(Note: This file is ignored by git for security).*
4.  Run the application:
    ```bash
    mvn clean spring-boot:run
    ```
    *Flyway will automatically execute migrations V1 through V8 to build your MySQL tables.*

### 2. Frontend Setup (React/Vite)
1.  Navigate to the frontend directory:
    ```bash
    cd frontend
    ```
2.  Install dependencies:
    ```bash
    npm install
    ```
3.  Create a `.env.local` file in the `frontend` root and add your Firebase public configuration:
    ```env
    VITE_FIREBASE_API_KEY=your_api_key
    VITE_FIREBASE_AUTH_DOMAIN=your_project.firebaseapp.com
    VITE_FIREBASE_PROJECT_ID=your_project_id
    VITE_FIREBASE_STORAGE_BUCKET=your_project.appspot.com
    VITE_FIREBASE_MESSAGING_SENDER_ID=123456789
    VITE_FIREBASE_APP_ID=1:123456789:web:abcdef
    VITE_API_BASE_URL=http://localhost:8080
    ```
4.  Start the development server:
    ```bash
    npm run dev
    ```
5.  Open [http://localhost:5173](http://localhost:5173) in your browser. Ensure the backend is running so data can sync properly.

---

## Architectural Note (The V8 Migration)
To streamline project management, the `hackathon_records` database was entirely merged into the main `boards` entity during Migration V8. 
**"One Board represents exactly One Hackathon Project."** This reduces data redundancy, improves read performance, and delivers a highly focused, premium UX for developers executing their vision.

---

## License
This codebase is strictly educational and proprietary. Please consult the repository owner before cloning or distributing.
