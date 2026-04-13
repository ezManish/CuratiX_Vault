# Contributing to CuratiX Vault

First off, thank you for considering contributing to CuratiX Vault! It's people like you that make high-quality hackathon tools possible.

---

## Architectural Principles

To maintain a "5-star" codebase, please adhere to these core principles:

1.  **Stateless First**: The backend must remain stateless. Use Firebase ID tokens for identity verification. Never store session data on the server.
2.  **Domain-Driven Entities**: Keep logic in Services, data in Entities, and communication in DTOs.
3.  **Defensive RBAC**: Every controller endpoint must explicitly check permissions via `PermissionService` before processing data.
4.  **Documentation is Code**: Every new API endpoint MUST be annotated with Swagger (`@Operation`). Every new service method MUST include JavaDoc.

---

## Development Workflow

### 1. Branching Strategy
- `main`: Production-ready code (Protected).
- `develop`: Integration branch for new features.
- `feature/*`: Individual feature branches.

### 2. Code Standards
- **Java**: Follow Google Java Style Guide. Use Lombok to reduce boilerplate.
- **TypeScript**: Use functional components and hooks. Prefer interfaces over types for data structures.
- **CSS**: Use the variables defined in `index.css` for consistent glassmorphism effects.

### 3. Testing
Before submitting a PR, ensure all backend tests pass:
```bash
cd backend
mvn test
```
We target **80%+ coverage** for Service-layer logic.

---

## Pull Request Checklist

When submitting a PR, please ensure:
- [ ] Your code follows the project's style guidelines.
- [ ] New functionality is covered by JUnit tests.
- [ ] API changes are reflected in Swagger annotations.
- [ ] The `README.md` is updated if setup requirements change.

---

## Reporting Issues

Use the GitHub Issue Tracker to report bugs or request features. Please include:
- A clear, descriptive title.
- Steps to reproduce (for bugs).
- Expected vs. Actual behavior.
- Screenshots if applicable.

---

## Security

If you discover a security vulnerability, please do NOT open a public issue. Instead, email the maintainers directly or report via the repository's security advisory tab.

