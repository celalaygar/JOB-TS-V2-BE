# 🚀 JOB TRACKING SYSTEM V2 - Backend (WebFlux + MongoDB + MinIO)

The **backend** of Job Tracking System V2 is built using a **reactive, scalable, and secure architecture**.  
It powers all business logic, persistence, and integrations required to deliver a next-generation project management experience.

---

## 🛠️ Technology Stack

- **Java 21**
- **Spring Boot 3.x**
- **Spring WebFlux** → Fully non-blocking reactive REST APIs
- **Spring Security + JWT** → Role-based authentication & authorization
- **Reactive MongoDB** → Primary NoSQL database for users, projects, tasks
- **MinIO (S3 compatible)** → Object storage for attachments and media
- **Gradle** → Build automation & dependency management
- **Docker** → Containerization for deployment
- **Nginx** → Reverse proxy for production
- **Reactive Email Integration** → Invitations, password resets, notifications
- **API-based Logging** → Request/response with headers for full auditing

---

## 📂 Project Structure

```
job-ts-v2-be/
│── src/main/java/com/tracker/job_ts
│   ├── auth/                 # Authentication & Authorization
│   │   ├── config/           # Security & JWT configuration
│   │   ├── controller/       # Login/Register endpoints
│   │   └── service/          # Token provider & auth logic
│   │
│   ├── project/              # Project CRUD & team handling
│   │   ├── controller/
│   │   ├── service/
│   │   └── repository/
│   │
│   ├── task/                 # Task, backlog & subtask management
│   ├── sprint/               # Sprint planning & Agile workflows
│   ├── kanban/               # Drag-drop board APIs
│   ├── weekly/               # Weekly hour tracking
│   ├── common/               # DTOs, Mappers, Validators, Utils
│   └── config/               # Global WebFlux, CORS, MinIO, Mongo configs
│
└── build.gradle
```

---

## 🔐 Authentication & User Management

- **Register & Login** (JWT-based stateless auth)
- **Role Management**: USER, ADMIN, PROJECT_OWNER
- **Secure Profile Updates** (with email confirmation)
- **Invitation System** (via email, project-specific tokens)
- **Forgot Password** (OTP + email reset)

---

## 📊 Core Modules

### 1. User Module
- Register, Login, Update Profile, Change Password
- Invitation via email
- Manage roles per project

### 2. Project Module
- CRUD operations
- Project teams & members
- Custom project roles & statuses

### 3. Task Module
- CRUD operations
- Assign users
- Automatic backlog management
- Subtasks & comments

### 4. Backlog Module
- Auto-handling of unscheduled tasks
- Move tasks in/out of backlog

### 5. Sprint Module
- Create, list, delete sprints
- Assign tasks & users to sprint
- Mark sprint as completed

### 6. Kanban Module
- Drag & drop task statuses
- Sprint & project-based views

### 7. Weekly Module
- Track user work hours weekly
- Weekly board & list views

---

## 🗄️ Database Design

### MongoDB Collections
- `users`
- `projects`
- `project_users`
- `tasks`
- `sprints`
- `weekly_entries`
- `invitations`

### MinIO Storage
- `/attachments/{projectId}/{taskId}/filename.ext`

---

## 📡 API Endpoints (Examples)

| Method | Endpoint                        | Description                    | Auth |
|--------|---------------------------------|--------------------------------|------|
| POST   | `/api/auth/register`            | Register a new user            | ❌   |
| POST   | `/api/auth/login`               | Login & get JWT token          | ❌   |
| GET    | `/api/projects`                 | List user projects             | ✅   |
| POST   | `/api/projects`                 | Create project                 | ✅   |
| POST   | `/api/projects/{id}/invite`     | Invite user via email          | ✅   |
| POST   | `/api/tasks`                    | Create task                    | ✅   |
| PUT    | `/api/tasks/{id}/assign`        | Assign user to task            | ✅   |
| POST   | `/api/sprints/{id}/complete`    | Complete sprint                | ✅   |

---

## ⚙️ Local Setup

```bash
# Clone repository
git clone https://github.com/celalaygar/JOB-TS-V2-BE.git
cd JOB-TS-V2-BE

# Run with Gradle
./gradlew bootRun

# Or build Docker image
docker build -t job-ts-v2-be .
```

**Environment Variables**

```env
SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/job-ts-v2
MINIO_ENDPOINT=http://localhost:9000
MINIO_ACCESS_KEY=minio
MINIO_SECRET_KEY=minio123
JWT_SECRET=your-secret-key
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USER=your-email
EMAIL_PASS=your-password
```

---

## 📸 Screenshots

![Project Dashboard](https://raw.githubusercontent.com/celalaygar/main/refs/heads/main/project/job-tracking-system-v2/job-ts-1.png)
![Kanban Board](https://raw.githubusercontent.com/celalaygar/main/refs/heads/main/project/job-tracking-system-v2/job-ts-2.png)
![Weekly Board](https://raw.githubusercontent.com/celalaygar/main/refs/heads/main/project/job-tracking-system-v2/job-ts-3.png)

---

## 🚀 Deployment

- Backend runs in **Docker container**
- MinIO & MongoDB as separate services
- Nginx for reverse proxy (HTTPS + domain)
- CI/CD with GitHub Actions (optional)

---

## 📜 License

MIT License © 2025 [Celal Aygar](https://celalaygar.github.io)
