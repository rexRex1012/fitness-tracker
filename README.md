# SmartFit Tracker

A command-line Java fitness tracker for CS-242 (Team 8). Users can register, log workouts and body weight, track personal records, and undo or redo changes. All data is saved to a file, so it's still there the next time you run the program.

## Features

- **Multiple users** with register, login, and logout
- **Workout sessions** with a date, tags (like legs or push), and exercises (name, muscle group, sets, reps, weight)
- **Body weight log** with a progress summary showing the change from your earliest to latest entry
- **Personal records (PRs)** tracked per exercise, with a timeline of every time the PR went up
- **Workout history** you can sort by date or total volume (selection sort) and filter by tag or exercise name
- **Undo / redo** for adding workouts and body weight entries (using stacks)
- **Saves automatically** to `smartfit_data.ser` using Java serialization

## Project Structure

| File | What it does |
|------|--------------|
| `FitnessApp.java` | Main program, menus, and input handling |
| `User.java` | A user's workout history, body weight entries, and exercise stats |
| `WorkoutSession.java` | One workout: date, tags, and a list of exercises |
| `ExerciseEntry.java` | One exercise: sets, reps, weight, and volume |
| `ExerciseStats.java` | Total volume and PR timeline for one exercise |
| `BodyWeightEntry.java` | One body weight entry |
| `Action.java` | Abstract base class for undoable actions |
| `AddWorkoutAction.java` / `AddBodyWeightAction.java` | Undo/redo for each type of entry |
| `DataStore.java` | Saves and loads all users to a file |

## Data Structures Used

- `HashMap` for users and for exercise stats
- `LinkedList` for workout history
- `ArrayList` for exercises, body weight entries, and PR timelines
- `HashSet` for workout tags
- `Stack` for undo and redo

## How to Run

Requires Java 14 or newer (it uses the newer switch syntax).

```bash
javac *.java
java FitnessApp
```

## Team

CS-242-03, Fall 2025, Team 8
