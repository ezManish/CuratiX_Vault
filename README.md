# ◈ CuratiX Vault

**Elevate your hackathon team organization.**  
CuratiX Vault is a premium, lightweight project management platform designed specifically for hackathon teams to consolidate member profiles, track project assets, and maintain a historical record of their innovation journey.

---

## Quick Links

- **Live Platform**: [curatix.co.in](https://www.curatix.co.in/)
- **Mirror Link**: [curatix-vault.vercel.app](https://curatix-vault.vercel.app)
- **Interactive API Docs**: [Swagger UI](https://curatix-vault.onrender.com/swagger-ui/index.html)
- **Backend API**: [curatix-vault.onrender.com](https://curatix-vault.onrender.com)

---

## System Architecture

CuratiX Vault uses a modern, stateless architecture to ensure scalability and ease of deployment.

```mermaid
graph TD
    User((Developer)) -->|React + Vite| Frontend[Frontend UI]
    Frontend -->|Stateless API| Backend[Spring Boot API]
    
    subgraph Security
        Backend -->|Verify Token| Firebase[Firebase Auth]
    end
    
    subgraph Storage
        Backend -->|Relational Data| MySQL[(MySQL)]
        Backend -->|Asset Hosting| Cloudinary[Cloudinary]
    end
    
    style User fill:#6366f1,color:#fff
    style Frontend fill:#10b981,color:#fff
    style Backend fill:#3b82f6,color:#fff
    style MySQL fill:#f59e0b,color:#fff
    style Cloudinary fill:#ec4899,color:#fff
```

---

## Key Features

- **Dynamic Project Boards**: Create dedicated spaces for each hackathon with custom colors, emojis, and metadata.
- **Role-Based Collaboration**: Multi-tier permission system (OWNER, EDITOR, VIEWER) for secure team management.
- **Smart Member Cards**: Automatically syncs user professional data across all boards while allowing board-specific bios and skills.
- **The Vault (Asset Manager)**: Centralized storage for Pitch Decks, PRDs, and Codebase links with direct Cloudinary integration.
- **Insightful Exports**: One-click CSV exports for member data (Admission/Enrollment numbers) for quick form filling.
- **Invitation System**: Secure, shareable UUID tokens for quick team onboarding.

---

## Quick Start (Local Development)

### Prerequisites

- **Java 17+**
- **Node.js 18+**
- **MySQL 8.0**
- **Firebase Project** (for Auth)
- **Cloudinary Account** (for File Uploads)

### Backend Setup

1. Configure environment in `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/curatix
   spring.datasource.username=YOUR_USER
   spring.datasource.password=YOUR_PASS
   
   CLOUDINARY_URL=cloudinary://API_KEY:API_SECRET@CLOUD_NAME
   ```
2. Place your `firebase-service-account.json` in `backend/src/main/resources/`.
3. Run with Maven:
   ```bash
   cd backend
   mvn spring-boot:run
   ```

### Frontend Setup

1. Install dependencies:
   ```bash
   cd frontend
   npm install
   ```
2. Configure `.env`:
   ```env
   VITE_FIREBASE_CONFIG=...
   VITE_API_BASE_URL=http://localhost:8080
   ```
3. Launch:
   ```bash
   npm run dev
   ```

---

## API Authentication Guide

All protected endpoints require a Bearer Token in the `Authorization` header.

### 1. Retrieve ID Token (Frontend Console)
If you are logged into the web app, you can get your token via:
```javascript
// Run in browser console on CuratiX dashboard
await (await import('firebase/auth')).getAuth().currentUser.getIdToken();
```

### 2. Make Authenticated Requests
```bash
curl -X GET "http://localhost:8080/api/boards" \
     -H "Authorization: Bearer <YOUR_ID_TOKEN>"
```

> [!TIP]
> **Swagger UI** is available at `/swagger-ui/index.html` for interactive API exploration.

---

## Tech Stack

- **Frontend**: React 18, Vite, Tailwind CSS (minimal), Lucide Icons, Zustand (State Management).
- **Backend**: Java 17, Spring Boot 3.2, Spring Security, Hibernate/JPA.
- **Database**: MySQL 8.0 with Flyway migrations.
- **Infrastructure**: Cloudinary (Storage), Firebase (Auth/OIDC).

---

## Contributing

We welcome professional contributions. Please see [CONTRIBUTING.md](./CONTRIBUTING.md) for our architectural guidelines and code standards.
