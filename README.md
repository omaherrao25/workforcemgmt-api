# 🚀 Workforce Management API

This is my submission for the **Backend Developer Evaluation Assignment** by Railse.  
It is a Spring Boot (Java 17) based REST API that simulates a workforce task management system with in-memory data storage.

---

## 📦 Tech Stack

- Java 17  
- Spring Boot 3.0.4  
- Gradle  
- MapStruct  
- Lombok  

---

## 🧠 Features Implemented

### ✅ Bug Fixes
1. **Task Reassignment Bug**  
   Fixed duplication issue when reassigning a task by reference — now previous tasks are marked as `CANCELLED`.

2. **Cancelled Tasks in Fetch API**  
   Modified fetch logic to exclude `CANCELLED` tasks from the response.

---

### ✨ New Features

1. **Smart Daily Task View**  
   Returns:
   - All active tasks within a selected date range.
   - All open tasks started *before* the range but not yet completed.

2. **Task Priority Management**  
   - Add priority (HIGH, MEDIUM, LOW) when creating a task  
   - Update task priority via PATCH  
   - Filter tasks by priority

3. **Comments & Activity Logs**  
   - Add comments to any task  
   - Automatically logs key events (e.g., priority changes, comments)  
   - View a full task history with comments and activity timeline

---

## 📂 Project Structure

```bash
src/main/java/com/yourcompany/workforcemgmt/
├── controller/           # REST endpoints
├── service/              # Business logic
├── service/impl/         # Service implementations
├── dto/                  # Data Transfer Objects
├── model/                # Task model & enums
├── repository/           # In-memory repository
├── mapper/               # MapStruct mappers
├── common/
│   ├── exception/        # Global exception handling
│   ├── model/
│   │   ├── enums/        # Shared enums
│   │   └── response/     # Response wrapper classes
└── WorkforcemgmtApplication.java
