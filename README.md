# Wildlife Tracker

A web application for tracking and documenting wildlife sightings on campus. Users can report animal sightings, view them on an interactive map, compete on leaderboards, and receive notifications about wildlife activity.

## Overview

Wildlife Tracker is a full-stack web application built with Spring Boot and JavaScript. It allows users to:

- **Report Wildlife Sightings**: Document species, location, description, and photos
- **Interactive Map**: View all sightings plotted on a campus map with real-time updates
- **Search & Filter**: Find sightings by species, location, or date
- **Leaderboard**: Compete with other users based on total sightings and unique species
- **User Profiles**: Customize profiles with display names, bios, and profile pictures
- **Notifications**: Receive alerts about new sightings in areas of interest
- **Command System**: Use text-based commands for quick actions (e.g., "report squirrel at Trousdale")

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.5.8
- **Language**: Java 17
- **Database**: MySQL (production), H2 (testing)
- **ORM**: Hibernate/JPA
- **Build Tool**: Maven
- **Security**: Spring Security (basic configuration)

### Frontend
- **HTML5/CSS3**: Static pages and styling
- **JavaScript (ES6+)**: Client-side logic and API integration
- **Fetch API**: RESTful communication with backend

## Usage Guide

### Creating an Account

1. Navigate to the login page (`login.html`)
2. Click "Sign Up" and enter a username and password
3. Click "Register" to create your account

### Logging In

1. Enter your username and password on the login page
2. Click "Login" to access the application

### Reporting a Sighting

**Method 1: Report Form**
1. Navigate to the Report page (`report.html`)
2. Fill in the sighting details:
   - Species name (e.g., "Squirrel", "Red-tailed Hawk")
   - Location (e.g., "Trousdale Parkway", "Alumni Park")
   - Description (optional)
   - Upload a photo (optional)
3. Click "Submit Sighting"

**Method 2: Command System**
1. Use the command input at the top of any page
2. Type a command like: `report squirrel at trousdale parkway`
3. Press Enter to quickly log a sighting

### Viewing the Map

1. Navigate to the main landing page or map view
2. Browse all sightings displayed as markers on the campus map
3. Click markers to view sighting details
4. Use the search bar to filter by species or location
5. Markers update automatically every 30 seconds

### Searching Sightings

1. Use the search bar on the map page
2. Enter species names (e.g., "hawk", "squirrel") or locations
3. Filter results in real-time
4. Search history is saved for quick access

### Checking the Leaderboard

1. Navigate to the Leaderboard section
2. View rankings based on:
   - Total animals logged
   - Unique species count
   - Recent activity
3. See your own rank highlighted

### Managing Your Profile

1. Go to your Profile page (`profile.html`)
2. Update your display name and bio
3. Upload a profile picture
4. View your statistics:
   - Total sightings
   - Unique species discovered
   - Recent activity

### Notifications

1. Notifications appear automatically for new sightings
2. Receive alerts for species or locations you're interested in
3. Mark notifications as read to clear them

## Command System

The application supports natural language commands:

| Command Pattern | Example | Action |
|----------------|---------|--------|
| `report [species] at [location]` | `report hawk at alumni park` | Quick sighting report |
| `search [query]` | `search squirrel` | Search sightings |
| `help` | `help` | Show available commands |

## Development

### Prerequisites

Before running the application, ensure you have:

- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0+**
- A modern web browser (Chrome, Firefox, Safari, Edge)

### 1. Clone the Repository

```bash
git clone <repository-url>
cd wildlifetracker
```

### 2. Configure the Database

Create a MySQL database:

```sql
CREATE DATABASE wildlife;
```

Configure database credentials in `backend/wildlifetracker/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/wildlife?useSSL=false&serverTimezone=UTC
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_PASSWORD_HERE
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

> **Note**: Copy `application-example.properties` to `application.properties` and update with your credentials.

### 3. Build and Run

Navigate to the backend directory:

```bash
cd backend/wildlifetracker
```

Build the project:

```bash
mvn clean package
```

Run the application:

```bash
mvn spring-boot:run
```

The backend API will start on `http://localhost:8080`.





