# Task Management System

A desktop **Task Management System** built with **Java 23** and **JavaFX 17**.

The application allows users to manage employees and tasks through a tabbed graphical interface — create employees, define simple or complex tasks, assign tasks to employees, toggle completion status, and view work-duration analytics.

---

## Features

- **Employee Management** — Add employees with a unique ID and name; view all employees in a table alongside their assigned tasks.
- **Task Creation** — Create **Simple Tasks** (with start/end hours) or **Complex Tasks** (composed of multiple sub-tasks via the Composite pattern).
- **Task Assignment Wizard** — A step-by-step wizard to assign an unassigned task to a chosen employee.
- **Status Toggle Wizard** — Select an employee and toggle the completion status of any of their tasks.
- **Work Duration Analytics** — View each employee's total estimated work duration and filter employees exceeding 40 hours (sorted ascending).
- **Completed / Uncompleted Stats** — Per-employee breakdown of completed vs. uncompleted tasks.
- **Persistent Storage** — All data is automatically saved/loaded via Java Serialization (`.ser` files), so progress is retained between sessions.

---

## Architecture

The project follows a **layered architecture** organized into four packages:

```
src/main/java/com/example/.../
├── dataModel/          # Domain entities
│   ├── Task.java           # Sealed abstract base class
│   ├── SimpleTask.java     # Leaf task with start/end hours
│   ├── ComplexTask.java    # Composite task containing sub-tasks
│   └── Employee.java       # Employee entity
├── businessLogic/      # Core logic
│   ├── TasksManager.java   # CRUD, assignment, and duration calculations
│   └── Utility.java        # Analytics (>40h filter, completed/uncompleted stats)
├── dataAccess/         # Persistence
│   └── SerializationUtility.java   # Save/load via Java ObjectStreams
└── userInterface/      # JavaFX GUI
    ├── MainApp.java         # Application entry point (TabPane)
    └── tabs/
        ├── HomeTab.java     # Dashboard: assign tasks, modify status, stats, save
        ├── EmployeeTab.java # Add & view employees
        ├── TaskTab.java     # Create simple/complex tasks
        └── UtilityTab.java  # Work duration table & >40h sorted list
```


---

## Design Patterns

| Pattern | Usage |
|---|---|
| **Composite** | `ComplexTask` contains a list of `Task` objects, enabling recursive duration calculation. |
| **Sealed Classes** | `Task` is a `sealed` class permitting only `SimpleTask` and `ComplexTask` as subclasses. |
| **Layered Architecture** | Clear separation into Data Model, Business Logic, Data Access, and User Interface layers. |

---

## Prerequisites

- **Java 23** (or compatible JDK)
- **Maven 3.8+** (or use the included Maven Wrapper)

---

## Getting Started

### Clone the repository

```bash
git clone https://github.com/<your-username>/PT2025_30422_Buda_Cornel_Assignment1.git
cd PT2025_30422_Buda_Cornel_Assignment1
```

### Build the project

```bash
./mvnw clean compile        # Linux / macOS
mvnw.cmd clean compile      # Windows
```

### Run the application

```bash
./mvnw javafx:run           # Linux / macOS
mvnw.cmd javafx:run         # Windows
```

The application window will open with four tabs: **Home**, **Employee**, **Task**, and **Utility**.

---

## Usage Guide

1. **Employee Tab** — Enter an ID and name, then click *Add Employee*.
2. **Task Tab** — Click *Create Simple Task* (provide ID, start hour, end hour) or *Create Complex Task* (select existing tasks as sub-tasks).
3. **Home Tab** — Use *Assign Task* wizard to assign an unassigned task to an employee. Use *Modify Task Status* to toggle a task's completion. Click *Save* to persist data.
4. **Utility Tab** — View each employee's estimated work duration and see which employees exceed 40 hours.

---

## Project Structure

```
PT2025_30422_Buda_Cornel_Assignment1/
├── pom.xml                 # Maven configuration (Java 23, JavaFX 17, JUnit 5)
├── mvnw / mvnw.cmd         # Maven Wrapper scripts
├── manager1.ser            # Serialized data file (auto-generated)
├── src/
│   └── main/
│       ├── java/           # Source code (see Architecture above)
│       └── resources/      # Application resources
└── target/                 # Build output (generated)
```

---

## Technologies

- **Java 23**
- **JavaFX 17.0.6** (Controls + FXML)
- **Maven** with JavaFX Maven Plugin
- **JUnit 5.10.2** (test framework)
- **Java Serialization** for persistence

---

## Author

**Cornel Buda**

---

## License

This project is available for personal and educational use.
