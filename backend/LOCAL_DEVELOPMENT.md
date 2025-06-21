# Local Development Setup

This guide will help you set up the Calendar Aggregation API for local development and testing.

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Postman (for API testing)
- VS Code (optional, for debugging)

## Configuration

### 1. Application Configuration

The application uses `application.yml` with profile-based configuration:
- **Default profile**: Security enabled, production settings
- **Local profile**: Security disabled for easier testing, debug logging enabled

The configuration automatically switches based on the active profile:
- `spring.security.enabled: true` (default)
- `spring.security.enabled: false` (when using `local` profile)

### 2. Environment Variables (Optional)

Create a `.env` file in the backend directory for sensitive data:

```env
GOOGLE_CREDENTIALS_FILE=path/to/your/google-credentials.json
OUTLOOK_CLIENT_ID=your-outlook-client-id
OUTLOOK_CLIENT_SECRET=your-outlook-client-secret
OUTLOOK_TENANT_ID=your-outlook-tenant-id
```

## Running the Application

### Option 1: Maven Command Line

```bash
# Run with local profile (security disabled)
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Run with default profile (security enabled)
mvn spring-boot:run

# Run with debug mode
mvn spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Option 2: VS Code Debug

1. Open the project in VS Code
2. Go to Run and Debug (Ctrl+Shift+D)
3. Select "Debug Calendar Application (Local)"
4. Press F5 to start debugging

### Option 3: IDE (IntelliJ IDEA)

1. Open the project in IntelliJ IDEA
2. Create a new Run Configuration
3. Set Main class: `com.calendar.CalendarApplication`
4. Add VM options: `-Dspring.profiles.active=local`
5. Run or Debug

### Option 4: JAR File

```bash
# Build the application
mvn clean package -DskipTests

# Run with local profile
java -jar target/calendar-aggregator-0.0.1-SNAPSHOT.jar --spring.profiles.active=local

# Run with default profile
java -jar target/calendar-aggregator-0.0.1-SNAPSHOT.jar
```

## Testing with Postman

### 1. Set Up Postman

1. Open Postman
2. Create a new collection for "Calendar API"
3. Set up environment variables:
   - `baseUrl`: `http://localhost:8080`

### 2. Available Endpoints

#### Health Check
- **GET** `{{baseUrl}}/health`
- Returns application status

#### Fetch All Events
- **GET** `{{baseUrl}}/api/calendar/events`
- Query params: `userId`, `start`, `end`
- Example: `?userId=test-user&start=2024-01-01T00:00:00&end=2024-01-31T23:59:59`

#### Create Event
- **POST** `{{baseUrl}}/api/calendar/events`
- Query param: `userId`
- Body: JSON with event details

#### Update Event
- **PUT** `{{baseUrl}}/api/calendar/events`
- Query param: `userId`
- Body: JSON with updated event details

#### Delete Event
- **DELETE** `{{baseUrl}}/api/calendar/events/{eventId}`
- Query param: `userId`
- Path param: `eventId`

### 3. Sample Request Body

```json
{
  "id": null,
  "title": "Test Meeting",
  "description": "This is a test meeting",
  "location": "Conference Room A",
  "startTime": "2024-01-15T10:00:00",
  "endTime": "2024-01-15T11:00:00",
  "allDay": false,
  "status": "CONFIRMED",
  "calendarSource": "GOOGLE"
}
```

## Debugging

### Breakpoints

Set breakpoints in your controller methods:
- `CalendarController.fetchAllEvents()`
- `CalendarController.createEvent()`
- `CalendarController.updateEvent()`
- `CalendarController.deleteEvent()`

### Logs

With the local profile, you'll see detailed logs including:
- HTTP request/response details
- Spring Security events (when enabled)
- Application-specific debug messages

### Common Issues

1. **Port already in use**: Change port in `application.yml`
2. **Security blocking requests**: Ensure you're using the `local` profile
3. **Missing dependencies**: Run `mvn clean install` to rebuild
4. **OAuth2 configuration errors**: These are automatically disabled in local profile

## Profile Configuration

### Local Profile (`--spring.profiles.active=local`)
- Security disabled
- Debug logging enabled
- OAuth2 beans not created
- Suitable for development and testing

### Default Profile (no profile specified)
- Security enabled
- OAuth2 beans created
- Production-ready configuration
- Requires proper OAuth2 setup

## Next Steps

1. Test all endpoints with Postman
2. Set up real OAuth2 credentials for Google/Outlook
3. Enable security when ready for production testing
4. Add more comprehensive error handling
5. Implement actual calendar provider integrations 