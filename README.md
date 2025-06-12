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
│   │                   └── controller/
│   │                       └── HealthController.java
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

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Install dependencies:
   ```bash
   mvn install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The backend will be available at: http://localhost:8080

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Run the application:
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
1. Start the Spring Boot backend
2. Wait for 10 seconds to let the backend initialize
3. Start the React frontend

## Available Endpoints

### Backend

- Health Check: `GET http://localhost:8080/health`

## Development

### Backend Development

The backend is built with:
- Spring Boot 3.2.3
- Java 17
- Maven for dependency management

### Frontend Development

The frontend is built with:
- React 18
- TypeScript
- Create React App

## Project Status

Currently in Phase 1 (MVP) development. Features implemented:
- Basic project structure
- Health check endpoint
- Basic frontend setup

## Future Improvements

See `project_requirements_document.md` for detailed information about planned features and improvements. 