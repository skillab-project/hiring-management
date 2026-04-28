# Hiring Management Service (LabourPulse Ecosystem)

**Hiring Management Service** handles the recruitment lifecycle, from job posting to final candidate evaluation. It is designed to work in a multi-tenant environment sharing a centralized database with the Employee Management Service.

## 🚀 Key Features

*   **Recruitment Lifecycle:** End-to-end management of Job Ads, Candidates, and Interview workflows.
*   **Skill-Based Evaluation:** Granular scoring of candidates against specific skills (ESCO standard) during various interview steps.
*   **Multi-Tenancy via Headers:** Strict data isolation using the `X-User-Organization` header. All data queries are scoped to the requesting organization.
*   **Advanced Analytics:** Native SQL-powered reporting for job ad performance, candidate metrics, and skill difficulty analysis.
*   **Shared Infrastructure:** Connects to the unified `labourpulse_db` schema managed by the Employee Service.

## 🛠 Tech Stack

*   **Java 21**
*   **Spring Boot 3.3.x**
*   **Spring Data JPA** (Hibernate 6)
*   **PostgreSQL 16**
*   **Docker & Docker Compose**
