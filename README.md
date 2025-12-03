# 📘 Quick_Zee - Quiz Management System

## Complete Project README

---

## 📋 Table of Contents

1. [Project Overview](#project-overview)
2. [Features](#features)
3. [Technology Stack](#technology-stack)
4. [System Requirements](#system-requirements)
5. [Installation & Setup](#installation--setup)
6. [Database Schema](#database-schema)
7. [Project Structure](#project-structure)
8. [User Roles & Capabilities](#user-roles--capabilities)
9. [Usage Guide](#usage-guide)
10. [Security Features](#security-features)
11. [Advanced Features](#advanced-features)
12. [Troubleshooting](#troubleshooting)
13. [Known Issues](#known-issues)
14. [Future Enhancements](#future-enhancements)
15. [Contributing](#contributing)
16. [License](#license)
17. [Contact](#contact)

---

## 🎯 Project Overview

**Quick_Zee** is a comprehensive offline quiz management system designed for educational institutions. It provides a complete solution for creating, managing, and taking quizzes with real-time scoring and performance tracking.

### Key Highlights:
- 🎓 **Dual Interface**: Command-Line Interface (CLI) and JavaFX Graphical User Interface (GUI)
- 👥 **Role-Based Access**: Separate admin and student functionalities
- ⏱️ **Timed Quizzes**: Real-time countdown timer with auto-submission
- 🔒 **Secure**: Password hashing with SHA-256
- 📊 **Analytics**: Comprehensive performance tracking and grading
- 🎲 **Randomization**: Optional question and option shuffling to prevent cheating

**Version:** 1.0 (Offline Edition)  
**Status:** Production Ready  
**License:** MIT

---

## ✨ Features

### For Administrators:
- ✅ Create, edit, and delete quizzes
- ✅ Add multiple-choice questions with 4 options
- ✅ Set quiz duration (1-180 minutes)
- ✅ Semester-based quiz restrictions (or "All Semesters")
- ✅ View detailed quiz statistics
- ✅ Edit existing quizzes and questions
- ✅ Delete questions with undo functionality
- ✅ Visual indicators for new/edited/deleted items

### For Students:
- ✅ Browse available quizzes by semester
- ✅ Take timed quizzes with countdown timer
- ✅ Real-time timer with color-coded warnings:
    - 🟢 Green (normal)
    - 🟠 Orange (< 5 minutes)
    - 🔴 Red (< 1 minute)
- ✅ Auto-submit when time expires
- ✅ View quiz results immediately
- ✅ Track performance history
- ✅ View statistics (average score, best score, pass/fail ratio)

### Technical Features:
- 🎲 **Optional Question Randomization**: Questions and options can be shuffled
- 🔐 **Password Security**: SHA-256 hashing for password storage
- 💾 **Data Persistence**: MySQL database with proper foreign key constraints
- 🖥️ **Dual UI**: Both CLI and JavaFX GUI available
- 📱 **Responsive Design**: Clean, modern UI with hover effects
- 🎨 **Color-Coded UI**: Visual feedback for different states

---

## 🛠️ Technology Stack

### Backend:
- **Language:** Java 24
- **Build Tool:** Maven
- **Database:** MySQL 8.0+
- **JDBC Driver:** MySQL Connector/J 8.2.0

### Frontend:
- **CLI:** Java Console
- **GUI:** JavaFX 21.0.1
- **UI Components:** Custom styled controls

### Architecture:
- **Pattern:** 3-Layer Architecture (Presentation → Service → DAO)
- **Database Connection:** JDBC with connection pooling
- **Security:** SHA-256 password hashing

---

## 💻 System Requirements

### Minimum Requirements:
- **OS:** Windows 10/11, macOS 10.14+, or Linux (Ubuntu 18.04+)
- **RAM:** 2 GB
- **Disk Space:** 500 MB
- **Java:** JDK 17 or higher (recommended: JDK 24)
- **MySQL:** Version 8.0 or higher

---

## 🚀 Installation & Setup

### Step 1: Prerequisites

#### Install Java JDK 24:
```bash
# Verify Java installation
java -version

# Should show: java version "24" or higher
```

**Download:** [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://jdk.java.net/)

#### Install MySQL:
```bash
# Verify MySQL installation
mysql --version

# Should show: mysql Ver 8.0.x or higher
```

**Download:** [MySQL Community Server](https://dev.mysql.com/downloads/mysql/)

#### Install Maven:
```bash
# Verify Maven installation
mvn -version

# Should show: Apache Maven 3.x.x
```

**Download:** [Apache Maven](https://maven.apache.org/download.cgi)

---

### Step 2: Clone or Download Project

```bash
# Clone repository (if using Git)
git clone https://github.com/yourusername/Quick_Zee.git
cd Quick_Zee

# Or download and extract ZIP file
```

---

### Step 3: Database Setup

#### 1. Start MySQL Server:
```bash
# Windows
net start MySQL80

# macOS/Linux
sudo systemctl start mysql
```

#### 2. Create Database:
```bash
# Login to MySQL
mysql -u root -p

# Enter your MySQL root password
```

#### 3. Run Database Script:
```sql
-- Create database
CREATE DATABASE Quick_Zee;
USE Quick_Zee;

-- Run the complete schema
SOURCE /path/to/Quick_Zee/database/schema.sql;

-- Or copy-paste the SQL from schema.sql
```

#### 4. Verify Database:
```sql
-- Check tables
SHOW TABLES;

-- Should show:
-- users
-- quizzes
-- questions
-- options
-- quiz_results
-- quiz_result_answers
```

---

### Step 4: Configure Database Connection

Edit `src/main/java/com/quickzee/common/util/DBConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/quick_zee";
private static final String USERNAME = "root";
private static final String PASSWORD = "YOUR_MYSQL_PASSWORD"; // ← Change this!
```

**⚠️ Important:** Replace `YOUR_MYSQL_PASSWORD` with your actual MySQL password.

---

### Step 5: Build Project

```bash
# Navigate to project root
cd Quick_Zee

# Clean and compile
mvn clean compile

# Run tests (optional)
mvn test

# Package application
mvn package
```

---

### Step 6: Run Application

#### Option A: Run GUI (Recommended)
```bash
# Using Maven
mvn javafx:run

# Or run directly from IDE (IntelliJ IDEA / Eclipse)
# Run: com.quickzee.gui.QuickZeeApp
```

#### Option B: Run CLI
```bash
# Using Maven
mvn exec:java -Dexec.mainClass="com.quickzee.cli.MainCLI"

# Or run directly from IDE
# Run: com.quickzee.cli.MainCLI
```

---

## 🗄️ Database Schema

### Entity Relationship Diagram (ERD)

```
┌─────────────┐
│    users    │
├─────────────┤
│ id (PK)     │
│ name        │
│ email       │
│ password    │
│ semester    │
│ role        │
└─────────────┘
       │
       │ (created_by)
       ↓
┌─────────────┐
│   quizzes   │
├─────────────┤
│ id (PK)     │
│ title       │
│ semester    │ ← 0 = All semesters
│ duration    │
└─────────────┘
       │
       │ (1:N)
       ↓
┌─────────────┐
│  questions  │
├─────────────┤
│ id (PK)     │
│ quiz_id(FK) │
│ ordinal     │
│ text        │
└─────────────┘
       │
       │ (1:N)
       ↓
┌─────────────┐
│   options   │
├─────────────┤
│ id (PK)     │
│ question_id │
│ ordinal     │
│ text        │
│ is_correct  │
└─────────────┘

┌─────────────────┐
│  quiz_results   │
├─────────────────┤
│ id (PK)         │
│ user_id (FK)    │
│ quiz_id (FK)    │
│ score           │
│ total_questions │
│ submitted_at    │
└─────────────────┘
       │
       │ (1:N)
       ↓
┌──────────────────────┐
│ quiz_result_answers  │
├──────────────────────┤
│ id (PK)              │
│ result_id (FK)       │
│ question_id (FK)     │
│ selected_option_id   │
└──────────────────────┘
```

### Table Descriptions:

#### `users`
Stores user accounts (admin and students)
- **Primary Key:** `id`
- **Unique:** `email`
- **Roles:** `admin`, `student`

#### `quizzes`
Stores quiz metadata
- **Primary Key:** `id`
- **Semester:** `0` = All semesters, `1-8` = Specific semester

#### `questions`
Stores quiz questions
- **Primary Key:** `id`
- **Foreign Key:** `quiz_id` → `quizzes(id)` ON DELETE CASCADE

#### `options`
Stores answer options for questions
- **Primary Key:** `id`
- **Foreign Key:** `question_id` → `questions(id)` ON DELETE CASCADE
- **is_correct:** `0` = incorrect, `1` = correct

#### `quiz_results`
Stores quiz attempt summary
- **Primary Key:** `id`
- **Foreign Keys:** `user_id`, `quiz_id`

#### `quiz_result_answers`
Stores individual answers per question
- **Primary Key:** `id`
- **Foreign Keys:** `result_id`, `question_id`, `selected_option_id`

---

## 📁 Project Structure

```
Quick_Zee/
│
├── pom.xml                          # Maven configuration
├── README.md                        # This file
│
├── database/
│   └── schema.sql                   # Database creation script
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/quickzee/
│   │   │       │
│   │   │       │
│   │   │       ├── common/
│   │   │       │   │
│   │   │       │   ├── cli/             # Command-Line Interface
│   │   │       │   │  ├── MainCLI.java
│   │   │       │   │  ├── AuthCLI.java
│   │   │       │   │  ├── AdminCLI.java
│   │   │       │   │  ├── StudentCLI.java
│   │   │       │   │  └── CLIHelper.java
│   │   │       │   │
│   │   │       │   ├── gui/             # JavaFX GUI (preferred)
│   │   │       │   │   ├── QuickZeeApp.java
│   │   │       │   │   ├── LoginView.java
│   │   │       │   │   ├── RegisterView.java
│   │   │       │   │   ├── AdminDashboard.java
│   │   │       │   │   ├── StudentDashboard.java
│   │   │       │   │   ├── QuizTakingView.java
│   │   │       │   │   ├── ResultView.java
│   │   │       │   │   └── UIHelper.java
│   │   │       │   │
│   │   │       │   ├── model/       # Data Models
│   │   │       │   │   ├── User.java
│   │   │       │   │   ├── Quiz.java
│   │   │       │   │   ├── Question.java
│   │   │       │   │   ├── Option.java
│   │   │       │   │   ├── QuizResult.java
│   │   │       │   │   └── QuizResultAnswer.java
│   │   │       │   │
│   │   │       │   ├── dao/         # Data Access Objects
│   │   │       │   │   ├── UserDao.java
│   │   │       │   │   ├── QuizDao.java
│   │   │       │   │   ├── QuestionDao.java
│   │   │       │   │   ├── OptionDao.java
│   │   │       │   │   └── ResultDao.java
│   │   │       │   │
│   │   │       │   ├── service/     # Business Logic
│   │   │       │   │   ├── AuthService.java
│   │   │       │   │   ├── QuizService.java
│   │   │       │   │   └── AttemptService.java
│   │   │       │   │
│   │   │       │   └── util/        # Utilities
│   │   │       │       ├── DBConnection.java
│   │   │       │       ├── SessionManager.java
│   │   │       │       ├── InputValidator.java
│   │   │       │       └── PasswordHasher.java
│   │   │       │
│   │   └── resources/
│   │       └── sql
│   │           └── schema.sql
│   │
│   └── test/
│       └── java/                    # Unit tests (optional)
│
└── target/                          # Compiled files (generated)
```

---

## 👥 User Roles & Capabilities

### 🔴 Admin

#### Capabilities:
- ✅ Create quizzes with custom titles, semesters, and durations
- ✅ Add/edit/delete questions (4 options per question)
- ✅ Mark correct answers
- ✅ View all quizzes in the system
- ✅ Edit existing quizzes (title, semester, duration)
- ✅ Edit questions and options
- ✅ Delete quizzes (with confirmation)
- ✅ View detailed quiz information
- ✅ Access dashboard with statistics

#### Admin Dashboard Features:
- **Statistics Cards:** Total quizzes, active quizzes, total questions
- **Tabbed Edit Interface:** Separate tabs for basic info and questions
- **Visual Indicators:** Color-coded borders for new/existing items
- **Undo Delete:** Can undo question deletion before saving

---

### 🔵 Student
**Registration:** Open registration for students

#### Capabilities:
- ✅ Browse available quizzes (semester-specific + "All Semesters")
- ✅ Take timed quizzes with countdown timer
- ✅ View immediate results after submission
- ✅ Track quiz history
- ✅ View performance statistics:
    - Average score
    - Best score
    - Grade distribution

#### Student Dashboard Features:
- **Available Quizzes Table:** Shows quizzes for student's semester + all semesters
- **Quiz History Table:** All past attempts with scores and grades
- **Statistics Panel:** Average and best scores
- **Profile Info:** View personal information

---

## 📖 Usage Guide

### First-Time Setup

#### 1. Register Admin Account
```
1. Run application (GUI or CLI)
2. Click "Register"
3. Select "Admin" role
5. Fill in details:
   - Name: John Doe
   - Email: admin@quickzee.com
   - Password: (minimum 6 characters)
6. Click "Register"
```

#### 2. Register Student Account
```
1. Click "Register"
2. Select "Student" role
3. Fill in details:
   - Name: Jane Smith
   - Email: student@quickzee.com
   - Password: (minimum 6 characters)
   - Semester: 3 (or any 1-8)
4. Click "Register"
```

---

### Admin Workflow

#### Creating a Quiz:
```
1. Login as Admin
2. Click "Create Quiz" (sidebar or dashboard)
3. Enter quiz details:
   ├─ Title: "Java Fundamentals Quiz"
   ├─ Semester: Check box for specific semester (1-8)
   │            or leave unchecked for "All Semesters"
   └─ Duration: 30 minutes (1-180)
4. Click "Create Quiz & Add Questions"
```

#### Adding Questions:
```
5. In the dialog that appears:
   ├─ Question Text: "What is polymorphism?"
   ├─ Option 1: "Many forms" ← Select as correct
   ├─ Option 2: "One form"
   ├─ Option 3: "No form"
   └─ Option 4: "Two forms"
6. Click "+ Add Question" for more questions
7. Click "OK" to save all questions
```

#### Editing a Quiz:
```
1. Go to "View All Quizzes"
2. Click "Edit" button on any quiz
3. Tab 1 - Basic Information:
   └─ Edit title, semester, duration
4. Tab 2 - Questions:
   ├─ Edit existing questions/options
   ├─ Add new questions
   ├─ Delete questions (with undo)
   └─ Change correct answers
5. Click "OK" to save changes
```

---

### Student Workflow

#### Taking a Quiz:
```
1. Login as Student
2. Browse "Available Quizzes" section
3. Click "Take Quiz" button
4. Confirm start (shows duration)
5. Answer questions:
   ├─ Timer counts down at top (color-coded)
   ├─ Select one option per question
   ├─ Navigate: Previous/Next buttons
   └─ Can skip questions
6. Click "Review & Submit" when done
7. Confirm submission
8. View results immediately:
   ├─ Score: X/Y
   ├─ Percentage: XX.XX%
   ├─ Grade: A+, A, B+, etc.
   └─ Status: PASSED/FAILED
```

#### Timer Behavior:
- 🟢 **Green:** Normal time remaining
- 🟠 **Orange:** Less than 5 minutes
- 🔴 **Red:** Less than 1 minute (starts flashing at 30 seconds)
- ⏰ **Time Expires:** Options grayed out, auto-submit after 3 seconds

#### Viewing History:
```
1. Check "My Quiz History" section on dashboard
2. See all past attempts:
   ├─ Quiz ID
   ├─ Score
   ├─ Percentage
   └─ Grade
3. Statistics shown below:
   ├─ Average Score
   └─ Best Score
```

---

## 🔒 Security Features

### Password Security:
- ✅ **SHA-256 Hashing:** All passwords hashed before storage
- ✅ **No Plain Text:** Passwords never stored in readable form
- ✅ **Secure Comparison:** Hash-based password verification

### Session Management:
- ✅ **In-Memory Sessions:** No persistent session storage
- ✅ **Role Verification:** Every sensitive action checks user role
- ✅ **Automatic Logout:** Session cleared on logout

### Database Security:
- ✅ **Prepared Statements:** Protection against SQL injection
- ✅ **Foreign Key Constraints:** Data integrity enforcement
- ✅ **Cascade Deletes:** Automatic cleanup of related data

### Input Validation:
- ✅ **Email Validation:** Proper email format required
- ✅ **Password Strength:** Minimum 6 characters
- ✅ **Data Type Checks:** Integer/string validation
- ✅ **Range Validation:** Semester (1-8), duration (1-180)

---

## 🎯 Advanced Features

### 1. Question and Option Randomization (Optional)

**Purpose:** Prevent cheating by shuffling questions and options

**Implementation:**
```java
// In QuizTakingView.java constructor, add:
Collections.shuffle(quiz.getQuestions());

for (Question question : quiz.getQuestions()) {
    if (question.getOptions() != null) {
        Collections.shuffle(question.getOptions());
    }
}
```

**Benefits:**
- Questions appear in different order each time
- Options shuffled independently
- Scoring still accurate (tracks by ID, not position)

---

### 2. Grading System

**Grading Scale:**
```
A+  → 80-100%
A   → 75-79%
A-  → 70-74%
B+  → 65-70%
B   → 60-64%
B-  → 55-59%
C+  → 50-54%
C   → 45-49%
D   → 40-44%
F   → 0-39%
```

**Pass/Fail Threshold:** 40%

---

### 3. Semester System

**Semester Values:**
- `0` = All Semesters (visible to all students)
- `1-8` = Specific semester (visible only to students in that semester)

**Student View Logic:**
- Students see quizzes for their semester + semester 0
- Example: Semester 3 student sees:
    - All quizzes with semester = 3
    - All quizzes with semester = 0

---

### 4. Timer Features

**Color Coding:**
```java
Normal   (> 5 min):  Green (#4CAF50)
Warning  (≤ 5 min):  Orange (#FF9800)
Critical (≤ 1 min):  Red (#F44336)
```

**Flash Effect:**
- Activates at 30 seconds remaining
- Toggles opacity every second

**Auto-Submit:**
- Disables all options at time = 0
- Shows warning dialog
- Auto-submits after 3 seconds

---

## 🐛 Troubleshooting

### Common Issues:

#### 1. "Database connection failed"
**Cause:** MySQL not running or wrong credentials

**Solution:**
```bash
# Check MySQL status
sudo systemctl status mysql

# Start MySQL
sudo systemctl start mysql

# Verify credentials in DBConnection.java
```

---

#### 2. "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
**Cause:** MySQL JDBC driver not in classpath

**Solution:**
```bash
# Verify pom.xml has MySQL dependency
# Run: mvn clean install
# Reimport Maven dependencies in IDE
```

---

#### 3. "JavaFX components not found"
**Cause:** JavaFX not properly configured

**Solution:**
```bash
# Verify pom.xml has JavaFX dependencies
# Run: mvn javafx:run
# Or configure VM options in IDE:
# --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
```

---

#### 4. GUI doesn't start / blank window
**Cause:** JavaFX runtime issues

**Solution:**
```bash
# Ensure Java 17+ is installed
java -version

# Clean and rebuild
mvn clean compile

# Try running from command line
mvn javafx:run
```

---

#### 5. "Timer not working correctly"
**Cause:** Thread scheduling issues

**Solution:**
- Restart application
- Check system time is correct
- Ensure no other heavy processes running

---

#### 6. Questions not appearing after quiz creation
**Cause:** Transaction not committed or validation error

**Solution:**
- Check all 4 options are filled
- Ensure one option is marked correct
- Check console for error messages

---

## ⚠️ Known Issues

### Minor Issues:

1. **Timer precision:** May be off by 1-2 seconds due to Java Timer implementation
    - **Impact:** Minimal, auto-submit still works correctly
    - **Workaround:** None needed

2. **Edit dialog scroll:** Very long quizzes may require scrolling in edit mode
    - **Impact:** UI only
    - **Workaround:** Create quizzes with fewer questions

3. **Font rendering:** May vary slightly across different operating systems
    - **Impact:** Visual only
    - **Workaround:** None needed

---

## 🚀 Future Enhancements

### Planned Features (v2.0):

1. **Online Mode:**
    - Multi-user concurrent access
    - Real-time quiz taking
    - Cloud database support

2. **Enhanced Analytics:**
    - Per-question statistics
    - Class averages
    - Performance trends over time

3. **Question Types:**
    - True/False questions
    - Multiple correct answers
    - Fill-in-the-blank

4. **Export Features:**
    - Export quiz as PDF
    - Export results as Excel
    - Print certificates

5. **Multimedia Support:**
    - Images in questions
    - Code snippets with syntax highlighting
    - Video questions

6. **Advanced Admin Features:**
    - Question bank/library
    - Quiz templates
    - Duplicate quiz feature
    - Bulk import from CSV/Excel

7. **Student Features:**
    - Account info edit options
    - Detailed answer review
    - Explanation for correct answers
    - Practice mode (untimed)
    - Bookmarking questions

---

## 🤝 Contributing

We welcome contributions! Here's how you can help:

### Reporting Bugs:
1. Check if issue already exists
2. Create new issue with:
    - Clear title
    - Steps to reproduce
    - Expected vs actual behavior
    - Screenshots (if applicable)
    - System information

### Suggesting Features:
1. Open an issue with "Feature Request" tag
2. Describe the feature and use case
3. Explain why it would be useful

### Submitting Code:
1. Fork the repository
2. Create feature branch: `git checkout -b feature/AmazingFeature`
3. Commit changes: `git commit -m 'Add AmazingFeature'`
4. Push to branch: `git push origin feature/AmazingFeature`
5. Open Pull Request

### Code Style Guidelines:
- Follow Java naming conventions
- Add JavaDoc comments for public methods
- Include inline comments for complex logic
- Write unit tests for new features

---

## 📄 License

This project is licensed under the **MIT License**.



## 🙏 Acknowledgments

- **Java Community:** For excellent documentation and support
- **JavaFX Team:** For the powerful UI framework
- **MySQL:** For the reliable database system
- **Maven:** For dependency management
- **Contributors:** Everyone who has contributed to this project

---

## 📊 Project Statistics

- **Total Lines of Code:** ~5,000+
- **Number of Classes:** 30+
- **Database Tables:** 6
- **Features Implemented:** 25+
- **Development Time:** 9 weeks
- **Last Updated:** December 2024

---

## 🎓 Educational Use

This project is ideal for:
- ✅ Learning Java programming
- ✅ Understanding MVC/3-tier architecture
- ✅ Database design and SQL
- ✅ JavaFX GUI development
- ✅ Software engineering principles
- ✅ Project management

**Perfect for:**
- University projects
- Coding bootcamps
- Portfolio projects
- Learning resources

---

## ⭐ Show Your Support

If you find this project useful, please consider:
- ⭐ Starring the repository
- 🍴 Forking for your own use
- 📢 Sharing with others
- 🐛 Reporting bugs
- 💡 Suggesting features

---

## 🔄 Version History

### v1.0.0 (Current - December 2024)
- ✅ Complete CLI implementation
- ✅ Full JavaFX GUI
- ✅ Timed quizzes with countdown
- ✅ Question randomization
- ✅ Edit quiz functionality
- ✅ Comprehensive statistics
- ✅ Password hashing
- ✅ Role-based access

### v0.9.0 (Beta)
- ✅ Basic CRUD operations
- ✅ Simple GUI
- ✅ Quiz taking without timer

### v0.5.0 (Alpha)
- ✅ CLI only
- ✅ Basic features

---

**🎉 Thank you for using Quick_Zee! Happy quizzing! 🎉**

---

**Last Updated:** December 4, 2024  
**Version:** 1.0.0  
**Status:** ✅ Production Ready