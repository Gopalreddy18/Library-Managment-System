# AI-Powered DSA Mentor

AI-Powered DSA Mentor is an intelligent full-stack coding platform that helps students accelerate their Data Structures and Algorithms (DSA) preparation through deterministic static analysis, deep AI-driven code reviews, personalized roadmap plans, realistic recruiter mock sessions, and detail analytics dashboards.

---

## 🌟 Key Features

1. **Authentication & JWT security**: Role-based access controls (`STUDENT`, `ADMIN`), secure password hashing (BCrypt), JWT token lifecycle management, and refresh token rotation.
2. **Problem Bank**: Categorized problems (Arrays, Hashing, Stack, Graphs, trees, etc.) with custom complexity thresholds, constraints, and test scenarios.
3. **Deterministic Complexity Engine**: Compares code layout (loop nesting, recursive paths, HashMaps) to compute execution complexity metrics and confidence levels before calling AI.
4. **AI-Powered Code Analysis**: Integrates Gemini 1.5 Flash to review submitted algorithms, detailing logical correctness, style conventions, and optimization guidelines.
5. **Personalized Roadmaps**: Generates custom 4-week learning timelines containing core structures, practice tasks, and mock objectives targeting companies (e.g. Google, Meta).
6. **Mock Recruiter Simulator**: An interactive chat environment mimicking DSA, Java core, System Design, or HR behavioral interview rounds, generating an AI scorecard evaluation.
7. **Dashboards & Analytics**: Student and Admin widgets summarizing streaks, accuracy, solved counts, topic strengths, weaknesses, and system resources.

---

## 🛠️ Technology Stack

* **Backend**: Java 21, Spring Boot 3.x, Spring Security (Stateless JWT), Spring Data JPA, Hibernate, Maven.
* **Database**: MySQL 8.x.
* **AI Provider**: Google Gemini API (Configurable AI Layer).
* **Frontend**: React 18, TypeScript, Material UI (MUI), Axios, React Router 6.
* **DevOps**: Docker, Docker Compose, GitHub Actions.

---

## 📂 Project Architecture

```
AI dsa mentor/
├── backend/
│   ├── src/
│   │   ├── main/java/com/dsa/mentor/       # Spring Boot Backend classes
│   │   └── resources/                      # Application configs
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/                                # React Frontend source
│   ├── index.html
│   ├── package.json
│   ├── tsconfig.json
│   └── vite.config.ts
├── .github/workflows/
│   └── ci-cd.yml                           # GitHub Actions build pipeline
├── docker-compose.yml                      # Production DB & Service orchestrator
├── dsa-mentor.postman_collection.json      # Postman Collection
└── README.md                               # Setup & Operations manual
```

---

## 🚀 Getting Started

### 1. Database Configuration
Ensure MySQL 8 is running. Create a schema named `dsa_mentor`:
```sql
CREATE DATABASE dsa_mentor;
```
Default connection parameters in `application.yml` target `localhost:3306` with user `root` / password `root`. You can override these using:
```bash
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/dsa_mentor"
$env:SPRING_DATASOURCE_USERNAME="your-user"
$env:SPRING_DATASOURCE_PASSWORD="your-password"
```

### 2. Setup Gemini API Key
To trigger AI reviews and mock assessments, acquire an API Key from Google AI Studio, then set it in your environment:
```bash
# Windows (PowerShell)
$env:GEMINI_API_KEY="your-gemini-api-key"

# Linux/macOS
export GEMINI_API_KEY="your-gemini-api-key"
```
*Note: If no API key is specified, the application falls back gracefully to structured Mock AI evaluations.*

### 3. Launching the Backend
Navigate to `/backend` and compile/boot the Spring Boot application:
```bash
cd backend
mvn clean test
mvn spring-boot:run
```
The server will bind to `http://localhost:8080`.

### 4. Running the Frontend
Install Node packages and launch the Vite dev server:
```bash
cd frontend
npm install
npm run dev
```
Open `http://localhost:5173` in your browser.

### 5. Running with Docker Compose
To run both MySQL and Spring Boot within dockerized containers:
```bash
docker-compose up --build
```

---

## 🛡️ Default Seed Credentials
The system automatically initializes default accounts on first run:

* **Student Account**:
  * Email: `student@dsamentor.com`
  * Password: `studentpassword123`
* **Admin Account**:
  * Email: `admin@dsamentor.com`
  * Password: `adminpassword123`

---

## 📡 REST API Specifications

### Authentication (`/api/auth`)
* `POST /api/auth/register`: Register new user profiles.
* `POST /api/auth/login`: Authenticate and receive Access token (JWT) & Refresh token.
* `POST /api/auth/refreshtoken`: Rotate expired access token using refresh token.
* `POST /api/auth/logout`: Log out user and clean current session state.

### Problems (`/api/problems`)
* `GET /api/problems`: Get list of problems (optional query filters: `difficulty`, `tag`).
* `GET /api/problems/{id}`: Detailed problem description and test requirements.
* `POST /api/problems` (Admin): Add new problems to the bank.

### Submissions (`/api/submissions`)
* `POST /api/submissions`: Submit code (Java, Python, C++, JavaScript) for validation.
* `GET /api/submissions/problem/{problemId}`: Fetch submission history for a problem.
* `GET /api/submissions/{id}/analysis`: Get deterministic and AI-powered reviews.

### Roadmaps (`/api/roadmap`)
* `POST /api/roadmap/generate`: Request AI to formulate a customized 4-week study plan.
* `GET /api/roadmap/current`: Retrieve the student's active roadmap.

### Mock Interviews (`/api/interview`)
* `POST /api/interview/start`: Spawn an interview session (`DSA`, `JAVA`, `SYSTEM_DESIGN`, `HR`).
* `POST /api/interview/{id}/respond`: Reply to the recruiter query with code/text explanation.
* `POST /api/interview/{id}/submit`: Finish interview to run grading and retrieve rating card.

---

## 🐳 Production Deployment

### 1. Render / Railway
* Deploy the Backend by linking the repo. Set the build command to `mvn clean package -DskipTests` and start command to `java -jar backend/target/*.jar`. Add environment variables: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `GEMINI_API_KEY`, `JWT_SECRET`.
* Deploy the Frontend using the Vite static build option. Point redirect rules to `/index.html` to support React Router path bindings.

### 2. AWS EC2
* Spin up an EC2 instance, install Docker & Docker-compose.
* Copy `docker-compose.yml` and project files, build containers and serve through an Nginx proxy configured with SSL certificates (Let's Encrypt).
