# Fitness Tracker

A comprehensive Java-based fitness tracker application with MySQL backend for individuals to log steps, calories, workouts, and duration, while trainers monitor progress, analyze trends, and suggest personalized plans.

## Features

### For Individuals
- **Activity Logging**: Track daily steps, workouts, and exercise duration
- **Calorie Management**: Log meals and track calorie intake with macronutrients
- **Health Metrics**: Record weight, height, blood pressure, and heart rate
- **BMI-Based Health Classification**: Automatic BMI calculation with categorization and personalized health alerts
- **Dynamic Dashboard**: Real-time overview of fitness progress with weekly/monthly trends
- **Progress Reports**: Detailed analytics on steps, workouts, and nutrition
- **Personalized Goals**: Goal recommendations based on BMI category

### For Trainers
- **Client Management**: View and manage assigned individuals
- **Progress Monitoring**: Track client workout frequency, steps, and health metrics
- **Trend Analysis**: Analyze 30-day trends for informed coaching decisions
- **Health Alerts**: Identify clients needing attention based on BMI status

### AI Fitness Chatbot
- **Interactive Dialogue**: Natural language conversation about fitness
- **Personalized Tips**: Workout and diet suggestions based on user's BMI and activity
- **Motivation**: Encouraging messages and fitness quotes
- **Progress Summaries**: Quick stats and goal tracking
- **Health Information**: BMI explanations and ideal weight ranges

### Security
- **Role-Based Access Control**: Separate permissions for individuals and trainers
- **Secure Password Hashing**: BCrypt encryption for all passwords
- **Input Validation**: Comprehensive validation to prevent invalid data entry

## Technology Stack

- **Language**: Java 11
- **Build Tool**: Maven
- **Database**: MySQL 8.0+
- **Password Hashing**: BCrypt (jBCrypt)
- **Testing**: JUnit 5, Mockito

## Project Structure

```
fitness-tracker/
├── src/
│   ├── main/
│   │   ├── java/com/fitnesstracker/
│   │   │   ├── model/          # Data models
│   │   │   ├── dao/            # Data Access Objects
│   │   │   ├── service/        # Business logic services
│   │   │   ├── util/           # Utility classes
│   │   │   ├── ui/             # Console UI
│   │   │   └── FitnessTrackerApp.java
│   │   └── resources/
│   │       ├── database.properties
│   │       └── schema.sql
│   └── test/
│       └── java/com/fitnesstracker/
│           └── FitnessTrackerTest.java
├── pom.xml
└── README.md
```

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher

## Installation

### 1. Clone the Repository
```bash
git clone https://github.com/Harshitha2007-coder/fitness-tracker.git
cd fitness-tracker
```

### 2. Set Up MySQL Database
```bash
mysql -u root -p < src/main/resources/schema.sql
```

Or manually execute the schema.sql file in MySQL Workbench.

### 3. Configure Database Connection
Edit `src/main/resources/database.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/fitness_tracker
db.username=your_username
db.password=your_password
```

### 4. Build the Application
```bash
mvn clean install
```

### 5. Run the Application
```bash
mvn exec:java -Dexec.mainClass="com.fitnesstracker.FitnessTrackerApp"
```

Or run the JAR file:
```bash
java -jar target/fitness-tracker-1.0.0.jar
```

## Running Tests
```bash
mvn test
```

## Usage

### Getting Started

1. **Register**: Create an account as either an Individual or Trainer
2. **Login**: Use your username and password to access the application
3. **Navigate**: Use the menu options to access different features

### Individual User Features

- **Log Steps**: Record your daily step count and set goals
- **Log Workout**: Track exercise type, duration, and calories burned
- **Log Meals**: Record food intake with calories and macros
- **Record Health Metrics**: Update weight, height, blood pressure
- **View Dashboard**: See your fitness summary at a glance
- **Chat with AI**: Get personalized fitness advice

### Trainer Features

- **View Clients**: See all assigned individuals
- **Monitor Progress**: Track client activity and health metrics
- **Analyze Trends**: Review 30-day data trends for coaching insights

### AI Chatbot Commands

Try these prompts in the chatbot:
- "workout suggestions" - Get exercise recommendations
- "diet tips" - Receive nutrition advice
- "my BMI" - View your health metrics
- "progress" - See your weekly summary
- "motivation" - Get inspired!
- "help" - See all available commands

## BMI Categories

| BMI Range | Category | Alert Level |
|-----------|----------|-------------|
| < 18.5 | Underweight | Warning |
| 18.5 - 24.9 | Normal | None |
| 25 - 29.9 | Overweight | Warning |
| ≥ 30 | Obese | Critical |

## API Reference

### Models
- `User` - User accounts with role-based access
- `Workout` - Exercise sessions with type, duration, calories
- `DailySteps` - Daily step tracking with goals
- `CalorieIntake` - Meal and nutrition tracking
- `HealthMetrics` - BMI, weight, blood pressure, heart rate
- `FitnessGoal` - User fitness objectives
- `HealthAlert` - System-generated health notifications

### Services
- `AuthenticationService` - User registration and login
- `BMIService` - BMI calculations and health classification
- `DashboardService` - Analytics and reporting
- `FitnessChatbot` - AI-powered fitness assistant

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- BCrypt for secure password hashing
- JUnit 5 for testing framework
- MySQL for reliable data storage
