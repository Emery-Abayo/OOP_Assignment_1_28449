# AUCA_ID - School Management System

OOP practical assignment. Console program in Java for managing departments, students, teachers,
administrators, courses, classrooms, enrollments, assessments, fee payments and the library.

## How to run

Works with any JDK (Java 8 or newer).

In VS Code (with the Java Extension Pack):

1. Open the folder that directly contains `src` and `.vscode`.
2. Open `src/com/auca/main/Main.java` and click **Run** (or use the Run and Debug panel).
3. Type the inputs in the terminal.

If VS Code still shows old errors: Ctrl+Shift+P, "Java: Clean Java Language Server Workspace", Restart.

On Windows the easiest way is `run.bat` (double-click it, or type `.\run.bat` in the terminal).
It finds the JDK by itself, deletes the old `bin` folder and compiles everything again.

Manual commands (inside the project folder):

```
javac -d bin -sourcepath src src/com/auca/main/Main.java
java -cp bin com.auca.main.Main
```

## Packages

- `com.auca.interfaces` - Identifiable, Registrable
- `com.auca.abstracts` - Person, AcademicMember
- `com.auca.student`, `com.auca.teacher`, `com.auca.administrator`
- `com.auca.department`, `com.auca.course`, `com.auca.classroom`
- `com.auca.enrollment`, `com.auca.assessment`, `com.auca.feepayment`
- `com.auca.librarybook`, `com.auca.bookloan`
- `com.auca.exceptions` - the 5 custom exceptions
- `com.auca.main` - Main (menu and forms)

## Notes

- The program starts with some AUCA example records (see `loadSampleData` in Main).
  To start with no records, run it with the argument `empty`:
  `run.bat empty`, or `java -cp bin com.auca.main.Main empty`, or choose "AUCA_ID (no records)" in VS Code.
- Records are kept in ArrayLists, so they are lost when the program is closed.
- In a form, type `BACK` to go to the previous field and `CANCEL` to cancel. In a list, `0` cancels.
- When a needed record does not exist yet (for example no department), the form lets you register it
  and then continues where you stopped.
- Study year is 1-6 and the institutional email must end with `@auca.ac.rw`.
