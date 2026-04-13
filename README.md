# ◈ CuratiX Vault

**Elevate your hackathon team organization.**  
CuratiX Vault is a premium, lightweight project management platform designed specifically for hackathon teams to consolidate member profiles, track project assets, and maintain a historical record of their innovation journey.

[![Live](https://img.shields.io/badge/live-curatix.co.in-6366f1?style=flat-square)](https://www.curatix.co.in/)
[![Mirror](https://img.shields.io/badge/mirror-vercel-black?style=flat-square&logo=vercel)](https://curati-x-vault.vercel.app/)
[![API Docs](https://img.shields.io/badge/api-swagger-85EA2D?style=flat-square&logo=swagger&logoColor=black)](https://curatix-vault.onrender.com/swagger-ui/index.html)
[![License](https://img.shields.io/badge/license-MIT-blue?style=flat-square)](./LICENSE)

---

## Quick Links

- **Live Platform**: [curatix.co.in](https://www.curatix.co.in/)
- **Mirror Link**: [curati-x-vault.vercel.app](https://curati-x-vault.vercel.app/)
- **Interactive API Docs**: [Swagger UI](https://curatix-vault.onrender.com/swagger-ui/index.html)
- **Backend API**: [curatix-vault.onrender.com](https://curatix-vault.onrender.com)

---

## Key Features

- **Dynamic Project Boards**: Create dedicated spaces for each hackathon with custom colors, emojis, and metadata.
- **Role-Based Collaboration**: Multi-tier permission system (OWNER, EDITOR, VIEWER) for secure team management.
- **Smart Member Cards**: Automatically syncs user professional data across all boards while allowing board-specific bios and skills.
- **The Vault (Asset Manager)**: Centralized storage for Pitch Decks, PRDs, and Codebase links with direct Cloudinary integration.
- **Insightful Exports**: One-click CSV exports for member data (Admission/Enrollment numbers) for quick form filling.
- **Invitation System**: Secure, shareable UUID tokens for quick team onboarding.

---

## System Architecture

CuratiX Vault uses a modern, stateless architecture to ensure scalability and ease of deployment.

![System Architecture](https://github.com/user-attachments/assets/dc3c56c7-533a-4515-bede-5b4704f0d40c)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React 18, Vite, Tailwind CSS, Zustand, Lucide Icons |
| Backend | Java 17, Spring Boot 3.2, Spring Security, Hibernate/JPA |
| Database | MySQL 8.0 with Flyway migrations |
| Auth | Firebase (OIDC / ID token verification) |
| Storage | Cloudinary (file uploads and asset hosting) |

---

## Quick Start (Local Development)

### Prerequisites

- **Java 17+**
- **Node.js 18+**
- **MySQL 8.0**
- **Firebase Project** (for Auth)
- **Cloudinary Account** (for File Uploads)

### Environment Variables

All required configuration in one place:

**Backend** — `backend/src/main/resources/application.properties`

| Variable | Description |
|---|---|
| `spring.datasource.url` | MySQL JDBC connection string |
| `spring.datasource.username` | MySQL username |
| `spring.datasource.password` | MySQL password |
| `CLOUDINARY_URL` | Full Cloudinary URL (`cloudinary://key:secret@cloud`) |
| `firebase-service-account.json` | Place in `src/main/resources/` (not a property — a file) |

**Frontend** — `frontend/.env`

| Variable | Description |
|---|---|
| `VITE_FIREBASE_CONFIG` | Firebase project config object (JSON stringified) |
| `VITE_API_BASE_URL` | Backend base URL (e.g. `http://localhost:8080`) |

---

### Backend Setup

1. Configure `backend/src/main/resources/application.properties`:
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

## API Reference

All protected endpoints require a Firebase ID token as a Bearer token in the `Authorization` header.

### Retrieving your ID token

If you are logged into the web app, run this in the browser console:
```javascript
await (await import('firebase/auth')).getAuth().currentUser.getIdToken();
```

### Making authenticated requests
```bash
curl -X GET "http://localhost:8080/api/boards" \
     -H "Authorization: Bearer <YOUR_ID_TOKEN>"
```

> [!TIP]
> The full interactive API reference is available at [`/swagger-ui/index.html`](https://curatix-vault.onrender.com/swagger-ui/index.html) — every endpoint can be tested directly from the browser.

---

## Deployment

| Service | Platform | Notes |
|---|---|---|
| Frontend | [Vercel](https://vercel.com) | Auto-deploys on push to `main`. Set `VITE_*` env vars in project settings. |
| Backend | [Render](https://render.com) | Deployed as a Web Service. Set all backend env vars in the Render dashboard. Cold starts may cause a ~30s delay on the free tier. |
| Database | PlanetScale / Railway / any MySQL host | Update `spring.datasource.url` to point to your hosted instance. |

> [!NOTE]
> The Render free tier spins down after inactivity. The first request after idle may be slow — this is expected and not a bug.

---

## Contributing

We welcome professional contributions. Please see [CONTRIBUTING.md](./CONTRIBUTING.md) for architectural guidelines and code standards.

---

## License

This project is licensed under the [MIT License](./LICENSE).
