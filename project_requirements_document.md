# Project Requirements Document: Personal Calendar Aggregator & Booking Service

## Overview

This project aims to create a **personal calendar aggregation and booking system** that consolidates multiple calendar sources (personal, work, academic), shows unified availability, and allows external users to book appointments directly into your schedule without authentication.

Initially, the system will run **locally**, with the intention of being **lightweight**, **self-managed**, and **secure**. Cloud deployment and advanced features will be introduced progressively.

---

## Functional Requirements

### Phase 1: Local MVP (Minimum Viable Product)

1. **Calendar Integration**
   - Connect to and fetch events from multiple external calendar accounts.
   - Read-only access to all external calendars.
   - Normalize and merge event data into a unified internal format.

2. **Availability Engine**
   - Aggregate availability by excluding times blocked in any source calendar.
   - Support working hours configuration and time zone awareness.
   - Identify and display available booking slots.

3. **Booking Mechanism**
   - Allow a user (unauthenticated) to book a time slot.
   - Automatically block booked time on all integrated calendars.
   - Prevent double-booking by checking real-time availability before confirmation.

4. **Web Interface**
   - Public-facing booking page to display available time slots.
   - Simple and responsive user experience for booking.
   - Confirmation page upon booking.

5. **Local Deployment**
   - Entire project runs locally inside Docker.
   - Configuration driven by local environment variables or configuration files.

---

### Phase 2: Cloud Deployment

1. **Public Hosting**
   - Expose the booking interface to the internet securely.
   - Use a domain name and HTTPS.

2. **Persistent Scheduling**
   - Background jobs for periodic calendar syncing (e.g., every 5 minutes).
   - Optional persistence of booking data and logs (file-based or lightweight DB).

3. **Security Enhancements**
   - Enable HTTPS and rate limiting.
   - Add CAPTCHA or other spam-prevention mechanisms.

4. **Monitoring & Health**
   - Simple health checks and logging.
   - Uptime monitoring for public-facing endpoints.

---

## Non-Functional Requirements

- **Lightweight**: The service must be minimal in resource usage, suitable for a Raspberry Pi or small VPS.
- **Secure by Design**: No public exposure of internal APIs; only booking interface exposed.
- **Self-contained**: All components run inside a Docker container or Docker Compose setup.
- **Extensible**: System should allow easy addition of new calendar sources or booking logic.

---

## ☁️ Deployment Requirements

- **Containerization**
  - Dockerfile to run the full application.
  - Optional Docker Compose setup for future database or background workers.

- **Cloud Compatibility**
  - Able to run on any Linux-based cloud instance (e.g., DigitalOcean, AWS Lightsail, Hetzner).
  - Minimal disk, memory, and CPU footprint.

- **Configuration**
  - Environment-based configuration system.
  - Secret keys (calendar API credentials) mounted securely.

---

## Future Improvements

1. **Authentication Layer**
   - Admin-only dashboard to view booking stats or manually block time.
   - Optional user authentication for repeated bookings or client access.

2. **Recurring Booking Support**
   - Ability to configure recurring meetings (e.g., weekly coaching sessions).

3. **Calendar Event Enhancements**
   - Add metadata to events (description, links, calendar location).
   - Email confirmation to both parties.

4. **Time Zone Adaptability**
   - Show available time slots in visitor’s local time zone.

5. **Calendar UI View**
   - Integrated calendar view for owner to manage availability visually.

6. **Calendar Source Management**
   - UI or config-based way to add/remove calendar sources dynamically.

7. **Advanced Availability Rules**
   - Buffer times between meetings.
   - Block off lunch breaks or out-of-office times.

8. **Admin Analytics Dashboard**
   - Track how many meetings are booked, from whom, and when.
