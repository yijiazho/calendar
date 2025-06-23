# Calendar Aggregator & Booking Service

A personal calendar aggregation and booking system that consolidates multiple calendar sources, shows unified availability, and allows external users to book appointments directly into your schedule.

## Project Structure

```
calendar/
├── backend/                 # Spring Boot Backend
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── com/
│   │               └── calendar/
│   │                   ├── CalendarApplication.java
│   │                   ├── config/
│   │                   ├── controller/
│   │                   ├── converter/
│   │                   ├── dto/
│   │                   ├── enums/
│   │                   ├── exception/
│   │                   ├── model/
│   │                   └── service/
│   ├── .env.example         # Example environment file
│   └── pom.xml
├── frontend/               # React TypeScript Frontend
│   ├── public/
│   │   └── index.html
│   ├── src/
│   │   ├── App.tsx
│   │   ├── App.css
│   │   ├── index.tsx
│   │   └── index.css
│   ├── package.json
│   └── tsconfig.json
├── start-services.bat      # Script to start both services
└── README.md
```

## Prerequisites

- Java 17 or higher
- Node.js 16 or higher
- Maven
- npm

## Setup Instructions

### Backend Setup

1.  **Environment Variables:**
    Create a `.env` file in the `backend` directory. You can copy the structure from `.env.example` if it exists. Populate it with your OAuth2 credentials from Google and Outlook.

    ```bash
    # .env file content
    
    # Google OAuth2 Credentials
    GOOGLE_CLIENT_ID=your-google-client-id
    GOOGLE_CLIENT_SECRET=your-google-client-secret
    GOOGLE_REDIRECT_URI=http://localhost:8080/login/oauth2/code/google
    GOOGLE_APPLICATION_NAME=Your-Application-Name

    # Outlook OAuth2 Credentials
    OUTLOOK_CLIENT_ID=your-outlook-client-id
    OUTLOOK_CLIENT_SECRET=your-outlook-client-secret
    OUTLOOK_REDIRECT_URI=http://localhost:8080/login/oauth2/code/outlook
    OUTLOOK_TENANT_ID=your-outlook-tenant-id  # (usually 'common' for multi-tenant apps)
    ```

2.  **Navigate to the backend directory:**
    ```bash
    cd backend
    ```

3.  **Install dependencies:**
    ```bash
    mvn install
    ```

4.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```

The backend will be available at: http://localhost:8080

### Frontend Setup

1.  Navigate to the frontend directory:
    ```bash
    cd frontend
    ```

2.  Install dependencies:
    ```bash
    npm install
    ```

3.  Run the application:
    ```bash
    npm start
    ```

The frontend will be available at: http://localhost:3000

### Quick Start (Windows)

You can use the provided batch file to start both services simultaneously:

```bash
start-services.bat
```

This will:
1.  Start the Spring Boot backend
2.  Wait for 10 seconds to let the backend initialize
3.  Start the React frontend

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

## Development

### Backend Development

The backend is built with:
-   Spring Boot 3.2.3
-   Java 17
-   Maven for dependency management
-   Spring Security for OAuth2 authentication
-   Spring Data JPA

### Frontend Development

The frontend is built with:
-   React 18
-   TypeScript
-   Create React App

## Project Status

Currently in Phase 1 (MVP) development. Features implemented:
-   Basic project structure
-   Health check endpoint
-   Basic frontend setup
-   OAuth2 authentication for Google and Outlook
-   Backend services for calendar event management

## Future Improvements

See `project_requirements_document.md` for detailed information about planned features and improvements. 