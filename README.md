# Calendar Aggregator

## Overview
A full-stack calendar aggregator application with Java Spring Boot 3 (backend) and React TypeScript (frontend). Supports OAuth2 login with Google and Outlook, and aggregates calendar events.

---

## Backend (Spring Boot)

### Configuration
- All configuration is now managed in `backend/src/main/resources/application.yml`.
- **.env files are no longer used.**
- OAuth2 credentials and redirect URIs for Google and Outlook are set in `application.yml` under `spring.security.oauth2.client.registration`.

#### Example `application.yml` (snippet):
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: <your-google-client-id>
            client-secret: <your-google-client-secret>
            redirect-uri: http://localhost:8080/login/oauth2/code/google
            scope:
              - openid
              - email
              - profile
              - https://www.googleapis.com/auth/calendar
              - https://www.googleapis.com/auth/calendar.events
          outlook:
            client-id: <your-outlook-client-id>
            client-secret: <your-outlook-client-secret>
            redirect-uri: http://localhost:8080/login/oauth2/code/outlook
            scope:
              - Calendars.ReadWrite
              - offline_access
```

- **Redirect URIs:**
  - Google: `http://localhost:8080/login/oauth2/code/google`
  - Outlook: `http://localhost:8080/login/oauth2/code/outlook`
- Make sure to register these URIs in the Google and Microsoft developer consoles for your OAuth2 apps.

### Running the Backend
```sh
cd backend
./mvnw spring-boot:run
```

---

## Frontend (React)

### Configuration
- All OAuth2 and API configuration is now managed in `frontend/src/config/constants.ts`.
- **.env files are no longer used.**

#### Example `constants.ts` (snippet):
```ts
export const API_BASE_URL = 'http://localhost:8080';

export const GOOGLE_OAUTH = {
  clientId: '<your-google-client-id>',
  clientSecret: '<your-google-client-secret>',
  redirectUri: 'http://localhost:8080/login/oauth2/code/google',
  scope: [
    'https://www.googleapis.com/auth/calendar',
    'https://www.googleapis.com/auth/calendar.events',
  ],
  authorizationUri: 'https://accounts.google.com/o/oauth2/auth',
  tokenUri: 'https://oauth2.googleapis.com/token',
  userInfoUri: 'https://www.googleapis.com/oauth2/v3/userinfo',
};

export const OUTLOOK_OAUTH = {
  clientId: '<your-outlook-client-id>',
  clientSecret: '<your-outlook-client-secret>',
  redirectUri: 'http://localhost:8080/login/oauth2/code/outlook',
  scope: [
    'Calendars.ReadWrite',
    'offline_access',
  ],
  authorizationUri: 'https://login.microsoftonline.com/<your-tenant-id>/oauth2/v2.0/authorize',
  tokenUri: 'https://login.microsoftonline.com/<your-tenant-id>/oauth2/v2.0/token',
  userInfoUri: 'https://graph.microsoft.com/v1.0/me',
  tenantId: '<your-tenant-id>',
};

export const CALENDAR_API = {
  events: '/api/calendar/events',
};
```

### Running the Frontend
```sh
cd frontend
npm install
npm start
```

---

## OAuth2 Setup
- Register your app in the Google and Microsoft developer consoles.
- Set the redirect URIs to:
  - `http://localhost:8080/login/oauth2/code/google` (Google)
  - `http://localhost:8080/login/oauth2/code/outlook` (Outlook)
- Copy the client IDs and secrets into both `application.yml` (backend) and `constants.ts` (frontend).

---

## Development Scripts
- Use `start-services.bat` or `debug-services.bat` to run both backend and frontend together (Windows only).

---

## Available Endpoints

### Backend

-   **Health Check:** `GET http://localhost:8080/health`
-   **Authentication:**
    -   `GET /auth/login/google` - Initiates Google OAuth2 login.
    -   `GET /auth/login/outlook` - Initiates Outlook OAuth2 login.
    -   `POST /auth/logout` - Logs the user out.
    -   `GET /auth/status` - Checks the current authentication status.
-   **Calendar API:**
    -   `GET /api/calendar/events` - Fetches calendar events for the authenticated user.
    -   `POST /api/calendar/events` - Creates a new event.
    -   `PUT /api/calendar/events` - Updates an existing event.
    -   `DELETE /api/calendar/events/{id}` - Deletes an event.

---

## Notes
- All sensitive credentials are now managed in `application.yml` and `constants.ts`. Do **not** commit real secrets to version control.
- For production, use environment variables or a secure secrets manager for credentials. 