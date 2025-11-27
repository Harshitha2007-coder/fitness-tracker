# Fitness Tracker

A comprehensive Java-based fitness tracker with MySQL backend for individuals to log steps, calories, workouts, and duration, while trainers monitor progress, analyze trends, and suggest personalized plans.

## Features

### For Individuals
- 📊 **Dynamic Dashboard** - Real-time view of daily, weekly, and monthly fitness stats
- 👟 **Steps Logging** - Track daily step counts with progress visualization
- 🔥 **Calories Tracking** - Log calories consumed and burned
- 🏋️ **Workout Logging** - Record workouts with type, duration, intensity, and notes
- ⚖️ **BMI Calculation** - Automatic BMI calculation with health classification
- 🎯 **Goal Tracking** - Monitor progress towards fitness goals
- 🔔 **Health Alerts** - Receive personalized health alerts based on BMI and activity
- 📝 **Trainer Plans** - View workout and diet plans from assigned trainers
- 🤖 **AI Chatbot** - Get personalized fitness tips, workout suggestions, and motivation

### For Trainers
- 👥 **Client Management** - Assign and manage multiple clients
- 📈 **Progress Monitoring** - View detailed client fitness progress
- 📉 **Trend Analysis** - Analyze weekly trends for steps, workouts, and more
- 📝 **Plan Creation** - Create personalized workout and diet plans
- 🎯 **Goal Setting** - Set fitness goals for clients
- ⚠️ **Attention Alerts** - Identify clients needing attention

### Security
- 🔐 **Secure Authentication** - BCrypt password hashing
- 👤 **Role-Based Access** - Separate interfaces for individuals and trainers
- 🔒 **Password Validation** - Strong password requirements

### AI Chatbot Features
- 💬 Personalized fitness tips based on user data
- 🏃 Workout suggestions tailored to BMI and activity level
- 🥗 Diet and nutrition advice
- 💪 Motivational messages and encouragement
- 📊 Progress summaries on demand
- ❓ Help with fitness-related questions

## Technology Stack

- **Language**: Java 17
- **Database**: MySQL
- **Build Tool**: Maven
- **Security**: BCrypt for password hashing
- **Testing**: JUnit 5

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher

## Installation

### 1. Clone the repository
```bash
git clone https://github.com/Harshitha2007-coder/fitness-tracker.git
cd fitness-tracker
```

### 2. Set up MySQL Database
```bash
# Login to MySQL
mysql -u root -p

# Run the schema script
source src/main/resources/schema.sql
```

### 3. Configure Database Connection
Edit `src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/fitness_tracker
db.username=your_username
db.password=your_password
db.driver=com.mysql.cj.jdbc.Driver
```

### 4. Build the Project
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

## Usage

### Registration and Login
1. Start the application
2. Choose "Register as Individual" or "Register as Trainer"
3. Enter your details (username, password, email, full name)
4. Login with your credentials

### Individual User Flow
1. Update your profile with height and weight
2. Log daily steps, calories, and workouts
3. View your dashboard for progress summary
4. Set goals and track progress
5. Chat with the AI fitness assistant for tips

### Trainer Flow
1. Assign clients to manage
2. View client progress and trends
3. Create workout and diet plans
4. Set goals for clients
5. Monitor clients needing attention

## Project Structure

```
fitness-tracker/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/fitnesstracker/
    │   │   ├── FitnessTrackerApp.java      # Main entry point
    │   │   ├── model/                       # Data models
    │   │   │   ├── User.java
    │   │   │   ├── StepsLog.java
    │   │   │   ├── CaloriesLog.java
    │   │   │   ├── Workout.java
    │   │   │   ├── HealthMetrics.java
    │   │   │   ├── Goal.java
    │   │   │   ├── TrainerPlan.java
    │   │   │   ├── Alert.java
    │   │   │   ├── ChatMessage.java
    │   │   │   └── [Enums]
    │   │   ├── dao/                         # Data Access Objects
    │   │   │   ├── UserDAO.java
    │   │   │   ├── StepsLogDAO.java
    │   │   │   ├── CaloriesLogDAO.java
    │   │   │   ├── WorkoutDAO.java
    │   │   │   ├── HealthMetricsDAO.java
    │   │   │   ├── GoalDAO.java
    │   │   │   ├── TrainerPlanDAO.java
    │   │   │   ├── AlertDAO.java
    │   │   │   └── ChatMessageDAO.java
    │   │   ├── service/                     # Business logic
    │   │   │   ├── AuthService.java
    │   │   │   ├── IndividualService.java
    │   │   │   ├── TrainerService.java
    │   │   │   └── DashboardService.java
    │   │   ├── chatbot/                     # AI Chatbot
    │   │   │   └── FitnessChatbot.java
    │   │   ├── ui/                          # User Interface
    │   │   │   └── ConsoleUI.java
    │   │   └── util/                        # Utilities
    │   │       ├── DatabaseConnection.java
    │   │       ├── PasswordUtil.java
    │   │       └── BMICalculator.java
    │   └── resources/
    │       ├── db.properties               # Database configuration
    │       └── schema.sql                  # Database schema
    └── test/
        └── java/com/fitnesstracker/
            └── FitnessTrackerTest.java     # Unit tests
```

## Database Schema

The application uses the following main tables:
- `users` - User accounts with role-based access
- `trainer_clients` - Trainer-client relationships
- `steps_log` - Daily step counts
- `calories_log` - Daily calorie intake/burn
- `workouts` - Workout sessions
- `health_metrics` - BMI and health data
- `goals` - Fitness goals
- `trainer_plans` - Plans created by trainers
- `alerts` - Health and fitness alerts
- `chat_history` - AI chatbot conversations

## BMI Classification

The application uses WHO BMI classification:
| BMI Range | Classification |
|-----------|----------------|
| < 18.5 | Underweight |
| 18.5 - 24.9 | Normal |
| 25.0 - 29.9 | Overweight |
| 30.0 - 34.9 | Obese Class I |
| 35.0 - 39.9 | Obese Class II |
| ≥ 40.0 | Obese Class III |

## Testing

Run the test suite:
```bash
mvn test
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests
5. Submit a pull request

## License

This project is open source and available under the MIT License.

## Contact

For questions or support, please open an issue on GitHub.
