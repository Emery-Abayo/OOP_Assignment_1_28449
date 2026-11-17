package com.auca.main;

import com.auca.abstracts.AcademicMember;
import com.auca.abstracts.Person;
import com.auca.administrator.Administrator;
import com.auca.assessment.Assessment;
import com.auca.bookloan.BookLoan;
import com.auca.classroom.Classroom;
import com.auca.course.Course;
import com.auca.department.Department;
import com.auca.enrollment.Enrollment;
import com.auca.exceptions.BookUnavailableException;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.PrerequisiteMissingException;
import com.auca.exceptions.RecordNotFoundException;
import com.auca.exceptions.ValidationException;
import com.auca.feepayment.FeePayment;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.teacher.Teacher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

// main class: menu, forms for all records and the reports
public class Main {
    // one scanner for the whole program + the lists where the records are kept
    private static final Scanner SCANNER = new Scanner(System.in);

    private static final List<Department> departments = new ArrayList<>();
    private static final List<Student> students = new ArrayList<>();
    private static final List<Teacher> teachers = new ArrayList<>();
    private static final List<Administrator> administrators = new ArrayList<>();
    private static final List<Course> courses = new ArrayList<>();
    private static final List<Classroom> classrooms = new ArrayList<>();
    private static final List<Enrollment> enrollments = new ArrayList<>();
    private static final List<Assessment> assessments = new ArrayList<>();
    private static final List<FeePayment> payments = new ArrayList<>();
    private static final List<LibraryBook> books = new ArrayList<>();
    private static final List<BookLoan> loans = new ArrayList<>();

    // returned when the user types BACK or CANCEL in a field
    private static final Object BACK = new Object();
    private static final Object CANCEL = new Object();
    // when no head teacher is chosen
    private static final Object NONE = new Object();
    private static final String NOT_RETURNED = "(not returned yet)";

    // names of the forms that are open (when one form opens another one)
    private static final Deque<String> openForms = new ArrayDeque<>();

    private static final String LINE = "+------------------------------------------------------------+";
    private static final int BOX_WIDTH = 60;

    private static final String[] TYPE_NAMES = {"", "Departments", "Students", "Teachers", "Administrators",
        "Courses", "Classrooms", "Enrollments", "Assessments", "Fee payments", "Library books", "Book loans"};

    // main menu
    public static void main(String[] args) {
        System.out.println(LINE);
        boxLine("Welcome to AUCA_ID - School Management System");
        boxLine("All records are kept in memory while the program runs.");
        System.out.println(LINE);

        // load the sample records, unless the program is started with the argument "empty"
        if (!(args.length > 0 && args[0].equalsIgnoreCase("empty"))) {
            loadSampleData();
        }

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readChoice("Select an option: ", 0, 19);
            try {
                switch (choice) {
                    case 1: departmentForm(null, null); break;
                    case 2: studentForm(null, null); break;
                    case 3: teacherForm(null, null); break;
                    case 4: administratorForm(null, null); break;
                    case 5: courseForm(null, null); break;
                    case 6: classroomForm(null, null); break;
                    case 7: enrollmentForm(null, null); break;
                    case 8: assessmentForm(null, null); break;
                    case 9: feePaymentForm(null, null); break;
                    case 10: libraryBookForm(null, null); break;
                    case 11: bookLoanForm(null, null); break;
                    case 12: returnBook(); break;
                    case 13: viewAllRecords(); break;
                    case 14: searchById(); break;
                    case 15: updateRecord(); break;
                    case 16: showAcademicSummary(); break;
                    case 17: showPaymentHistory(); break;
                    case 18: showAvailableBooks(); break;
                    case 19: demonstratePolymorphism(); break;
                    case 0: running = !confirm("Exit AUCA_ID? Records are kept in memory only and will be lost."); break;
                    default:
                        printError("Invalid selection.");
                }
            } catch (PrerequisiteMissingException | RecordNotFoundException e) {
                // show the message and go back to the menu
                printError(e.getMessage());
            } catch (RuntimeException e) {
                // any other error, the program must not stop
                printError("Unexpected problem (" + e.getClass().getSimpleName() + "): " + e.getMessage());
                printInfo("The operation was stopped safely. Returning to the main menu.");
            }
        }
        System.out.println("Goodbye from AUCA_ID.");
    }

    private static void printMainMenu() {
        String[][] items = {
            {"Register Department", "Borrow Library Book"},
            {"Register Student", "Return Library Book"},
            {"Register Teacher", "View All Records"},
            {"Register Administrator", "Search Record by ID"},
            {"Register Course", "Update Existing Record"},
            {"Register Classroom", "Student Academic Summary"},
            {"Enroll Student", "Student Payment History"},
            {"Record Assessment", "View Available Books"},
            {"Record Fee Payment", "Demonstrate Polymorphism"},
            {"Register Library Book", "Exit"}};
        System.out.println();
        System.out.println("============== AUCA_ID SCHOOL MANAGEMENT ==============");
        for (int i = 0; i < items.length; i++) {
            int right = i == items.length - 1 ? 0 : i + 11;
            System.out.printf("%2d. %-25s %2d. %s%n", i + 1, items[i][0], right, items[i][1]);
        }
    }

    // reading input (always nextLine so no input is skipped)

    private static String readLine() {
        if (!SCANNER.hasNextLine()) {
            System.out.println();
            System.out.println("Input ended. Closing AUCA_ID.");
            System.exit(0);
        }
        return SCANNER.nextLine().trim();
    }

    private static String ask(String prompt) {
        System.out.print(prompt);
        return readLine();
    }

    private static int readChoice(String prompt, int min, int max) {
        while (true) {
            String text = ask(prompt);
            try {
                int value = Integer.parseInt(text);
                if (value >= min && value <= max) {
                    return value;
                }
                printError("Invalid selection: choose a number from " + min + " to " + max + ".");
            } catch (NumberFormatException e) {
                printError("Invalid selection: '" + text + "' is not a number. Choose " + min + "-" + max + ".");
            }
        }
    }

    private static boolean confirm(String question) {
        System.out.println(question);
        System.out.println("  [1] Yes   [2] No");
        return readChoice("Select option: ", 1, 2) == 1;
    }

    private static void pause() {
        ask("Press ENTER to return...");
    }

    private static void printError(String message) {
        System.out.println("! " + message);
    }

    private static void printSuccess(String message) {
        System.out.println("+ " + message);
    }

    private static void printInfo(String message) {
        System.out.println("  " + message);
    }

    private static void boxLine(String text) {
        int padding = Math.max(0, BOX_WIDTH - 2 - text.length());
        System.out.println("| " + text + repeat(' ', padding) + " |");
    }

    private static void boxTitle(String title) {
        String text = " " + title + " ";
        int left = Math.max(1, (BOX_WIDTH - text.length()) / 2);
        int right = Math.max(1, BOX_WIDTH - text.length() - left);
        System.out.println();
        System.out.println("+" + repeat('-', left) + text + repeat('-', right) + "+");
    }

    private static String repeat(char c, int times) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < times; i++) {
            text.append(c);
        }
        return text.toString();
    }

    private static String two(int number) {
        return String.format("%02d", number);
    }

    private static String display(Object value) {
        if (value == null) {
            return "(not entered)";
        }
        if (value == NONE) {
            return "(none)";
        }
        if (value instanceof Identifiable) {
            return Identifiable.labelOf((Identifiable) value);
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? "YES" : "NO";
        }
        if (value instanceof LocalDate) {
            return Registrable.formatDate((LocalDate) value);
        }
        if (value instanceof BigDecimal) {
            return Registrable.formatMoney((BigDecimal) value);
        }
        if (value instanceof Double) {
            return BigDecimal.valueOf((Double) value).stripTrailingZeros().toPlainString();
        }
        return value.toString();
    }

    private static String article(String word) {
        return "AEIOUaeiou".indexOf(word.charAt(0)) >= 0 ? "an" : "a";
    }

    // the form: steps, review, edit, save

    private static boolean runForm(String entity, String[] steps, int[] fieldStep, String[] labels, Object[] values,
                                   boolean[] locked, boolean updateMode, boolean startAtReview,
                                   IntFunction<Object> reader) {
        openForms.push(entity.toLowerCase() + (updateMode ? " update" : " registration"));
        try {
            int fieldCount = labels.length - 1;
            int dataSteps = steps.length - 1;
            int field = startAtReview ? fieldCount + 1 : 1;
            int shownStep = 0;

            while (field <= fieldCount) {
                int step = fieldStep[field];
                if (step != shownStep) {
                    printFormHeader(entity, steps, step, updateMode);
                    shownStep = step;
                }
                Object result = reader.apply(field);
                if (result == CANCEL) {
                    if (confirmDiscard(entity, updateMode)) {
                        return false;
                    }
                    continue;
                }
                if (result == BACK) {
                    if (field > 1) {
                        field--;
                    } else {
                        printInfo("You are already at the first field.");
                    }
                    continue;
                }
                values[field] = result;
                boolean lastFieldOfStep = field == fieldCount || fieldStep[field + 1] != step;
                if (lastFieldOfStep) {
                    Object navigation = stepNavigation(step == dataSteps, entity, updateMode);
                    if (navigation == CANCEL) {
                        return false;
                    }
                    if (navigation == BACK) {
                        field = firstFieldOfStep(fieldStep, Math.max(1, step - 1));
                        shownStep = 0;
                        continue;
                    }
                }
                field++;
            }

            while (true) {
                printReview(entity, steps, fieldStep, labels, values, locked, updateMode);
                int choice = readChoice("Select option: ", 0, 2);
                if (choice == 1) {
                    return true;
                }
                if (choice == 0) {
                    if (confirmDiscard(entity, updateMode)) {
                        return false;
                    }
                    continue;
                }
                int edit = chooseEditableField(labels, locked);
                if (edit == 0) {
                    continue;
                }
                System.out.println("Old value of field " + two(edit) + " (" + labels[edit] + "): " + display(values[edit]));
                Object result = reader.apply(edit);
                if (result == CANCEL) {
                    if (confirmDiscard(entity, updateMode)) {
                        return false;
                    }
                } else if (result == BACK) {
                    printInfo("Field " + two(edit) + " was not changed.");
                } else {
                    values[edit] = result;
                    printSuccess("Field " + two(edit) + " updated.");
                }
            }
        } finally {
            openForms.pop();
        }
    }

    private static int firstFieldOfStep(int[] fieldStep, int step) {
        for (int f = 1; f < fieldStep.length; f++) {
            if (fieldStep[f] == step) {
                return f;
            }
        }
        return 1;
    }

    private static Object stepNavigation(boolean lastDataStep, String entity, boolean updateMode) {
        while (true) {
            String next = lastDataStep ? "Review record" : "Next step";
            String in = ask("[ENTER] " + next + "     [BACK] Previous     [CANCEL] Exit : ");
            if (in.isEmpty()) {
                return null;
            }
            if (in.equalsIgnoreCase("BACK")) {
                return BACK;
            }
            if (in.equalsIgnoreCase("CANCEL")) {
                if (confirmDiscard(entity, updateMode)) {
                    return CANCEL;
                }
                continue;
            }
            printError("Press ENTER to continue, or type BACK or CANCEL.");
        }
    }

    private static boolean confirmDiscard(String entity, boolean updateMode) {
        String what = updateMode ? "your changes to this " + entity.toLowerCase()
                : "the unsaved " + entity.toLowerCase();
        return confirm("Discard " + what + "? Nothing will be saved.");
    }

    private static void printFormHeader(String entity, String[] steps, int step, boolean updateMode) {
        String title = entity + (updateMode ? " UPDATE" : " REGISTRATION");
        System.out.println();
        System.out.println(LINE);
        boxLine("AUCA_ID / SCHOOL MANAGEMENT");
        boxLine(String.format("%-41s%s", title, "Step " + step + " of " + steps.length));
        System.out.println(LINE);
        for (int i = 0; i < steps.length; i += 2) {
            String left = "[" + (i + 1) + "] " + steps[i];
            String right = i + 1 < steps.length ? "[" + (i + 2) + "] " + steps[i + 1] : "";
            boxLine(String.format("%-24s%s", left, right));
        }
        System.out.println(LINE);
        boxLine("Enter the requested value. Type BACK to return;");
        boxLine(updateMode ? "CANCEL to discard your changes." : "CANCEL to discard the unsaved " + entity.toLowerCase() + ".");
        System.out.println(LINE);
        System.out.println();
        System.out.println("[STEP " + step + "/" + steps.length + "] " + steps[step - 1].toUpperCase());
    }

    private static void printReview(String entity, String[] steps, int[] fieldStep, String[] labels, Object[] values,
                                    boolean[] locked, boolean updateMode) {
        boxTitle("REVIEW " + entity);
        int group = 0;
        for (int f = 1; f < labels.length; f++) {
            if (fieldStep[f] != group) {
                group = fieldStep[f];
                boxLine(steps[group - 1].toUpperCase());
            }
            String mark = locked != null && locked[f] ? "  (locked)" : "";
            boxLine(String.format("%s %-19s: %s%s", two(f), labels[f], display(values[f]), mark));
        }
        System.out.println(LINE);
        String save = updateMode ? "Save changes" : "Save " + entity.toLowerCase();
        boxLine("[1] " + save + "   [2] Edit field   [0] Cancel");
        System.out.println(LINE);
    }

    private static int chooseEditableField(String[] labels, boolean[] locked) {
        int last = labels.length - 1;
        while (true) {
            String in = ask("Field number to edit (01-" + two(last) + ", 0 = back to review): ");
            try {
                int number = Integer.parseInt(in);
                if (number == 0) {
                    return 0;
                }
                if (number < 1 || number > last) {
                    printError("Field number must be between 01 and " + two(last) + ".");
                } else if (locked != null && locked[number]) {
                    printError("Field " + two(number) + " (" + labels[number] + ") cannot be changed here.");
                } else {
                    return number;
                }
            } catch (NumberFormatException e) {
                printError("Invalid field number: enter a number from 01 to " + two(last) + ".");
            }
        }
    }

    // reading one field, returns the value or BACK / CANCEL
    // rule = a check that returns null if ok, otherwise the error message

    private static String fieldPrompt(int no, String label, String hint, Object current) {
        String keep = current == null ? "" : " (now: " + display(current) + ", ENTER keeps)";
        return String.format("%s %-20s[%s]%s : ", two(no), label, hint, keep);
    }

    private static String retryPrompt(int no, String label) {
        return "  Please re-enter field " + two(no) + " (" + label + "): ";
    }

    private static Object readFieldInput(String prompt) {
        String in = ask(prompt);
        if (in.equalsIgnoreCase("BACK")) {
            return BACK;
        }
        if (in.equalsIgnoreCase("CANCEL")) {
            return CANCEL;
        }
        return in;
    }

    private static Object askText(int no, String label, String hint, Object current, Function<String, String> rule) {
        String prompt = fieldPrompt(no, label, hint, current);
        while (true) {
            Object in = readFieldInput(prompt);
            if (in == BACK || in == CANCEL) {
                return in;
            }
            String text = (String) in;
            if (text.isEmpty() && current != null) {
                return current;
            }
            String error = text.isEmpty() ? label + " is required and cannot be blank." : rule.apply(text);
            if (error == null) {
                return text;
            }
            printError(error);
            prompt = retryPrompt(no, label);
        }
    }

    private static Object askInt(int no, String label, String hint, Object current, Function<Integer, String> rule) {
        String prompt = fieldPrompt(no, label, hint, current);
        while (true) {
            Object in = readFieldInput(prompt);
            if (in == BACK || in == CANCEL) {
                return in;
            }
            String text = (String) in;
            if (text.isEmpty() && current != null) {
                return current;
            }
            try {
                int number = Integer.parseInt(text);
                String error = rule.apply(number);
                if (error == null) {
                    return number;
                }
                printError(error);
            } catch (NumberFormatException e) {
                printError("Invalid number: '" + text + "' is not a whole number (example: 3).");
            }
            prompt = retryPrompt(no, label);
        }
    }

    private static Object askDecimal(int no, String label, String hint, Object current, Function<Double, String> rule) {
        String prompt = fieldPrompt(no, label, hint, current);
        while (true) {
            Object in = readFieldInput(prompt);
            if (in == BACK || in == CANCEL) {
                return in;
            }
            String text = (String) in;
            if (text.isEmpty() && current != null) {
                return current;
            }
            try {
                double number = new BigDecimal(text).doubleValue();
                String error = rule.apply(number);
                if (error == null) {
                    return number;
                }
                printError(error);
            } catch (NumberFormatException e) {
                printError("Invalid number: '" + text + "' is not a number (examples: 20, 17.5).");
            }
            prompt = retryPrompt(no, label);
        }
    }

    private static Object askMoney(int no, String label, String hint, Object current, Function<BigDecimal, String> rule) {
        String prompt = fieldPrompt(no, label, hint, current);
        while (true) {
            Object in = readFieldInput(prompt);
            if (in == BACK || in == CANCEL) {
                return in;
            }
            String text = (String) in;
            if (text.isEmpty() && current != null) {
                return current;
            }
            try {
                BigDecimal amount = new BigDecimal(text.replace(",", ""));
                String error = rule.apply(amount);
                if (error == null) {
                    return amount.setScale(2);
                }
                printError(error);
            } catch (NumberFormatException e) {
                printError("Invalid amount: '" + text + "' is not a number (examples: 250000, 150000.50).");
            }
            prompt = retryPrompt(no, label);
        }
    }

    private static Object askDate(int no, String label, String hint, Object current, Function<LocalDate, String> rule) {
        String prompt = fieldPrompt(no, label, hint, current);
        while (true) {
            Object in = readFieldInput(prompt);
            if (in == BACK || in == CANCEL) {
                return in;
            }
            String text = (String) in;
            if (text.isEmpty() && current != null) {
                return current;
            }
            try {
                LocalDate date = Registrable.parseDate(text);
                String error = rule.apply(date);
                if (error == null) {
                    return date;
                }
                printError(error);
            } catch (DateTimeParseException e) {
                printError("Invalid date: use YYYY-MM-DD with a real calendar date (example: 2004-05-16).");
            }
            prompt = retryPrompt(no, label);
        }
    }

    private static Object askChoice(int no, String label, String[] options, Object current, Function<String, String> rule) {
        StringBuilder hint = new StringBuilder();
        for (int i = 0; i < options.length; i++) {
            hint.append(i == 0 ? "" : " / ").append(i + 1).append(' ').append(options[i]);
        }
        String prompt = fieldPrompt(no, label, hint.toString(), current);
        while (true) {
            Object in = readFieldInput(prompt);
            if (in == BACK || in == CANCEL) {
                return in;
            }
            String text = (String) in;
            if (text.isEmpty() && current != null) {
                return current;
            }
            String chosen = null;
            for (int i = 0; i < options.length; i++) {
                if (text.equals(String.valueOf(i + 1)) || text.equalsIgnoreCase(options[i])) {
                    chosen = options[i];
                }
            }
            String error;
            if (chosen == null) {
                error = label + ": choose 1-" + options.length + " (" + String.join(", ", options) + ").";
            } else {
                error = rule == null ? null : rule.apply(chosen);
            }
            if (error == null) {
                return chosen;
            }
            printError(error);
            prompt = retryPrompt(no, label);
        }
    }

    private static Object askYesNo(int no, String label, Object current) {
        String prompt = fieldPrompt(no, label, "1 YES / 2 NO", current);
        while (true) {
            Object in = readFieldInput(prompt);
            if (in == BACK || in == CANCEL) {
                return in;
            }
            String text = ((String) in).toUpperCase();
            if (text.isEmpty() && current != null) {
                return current;
            }
            if (text.equals("1") || text.equals("YES") || text.equals("Y")) {
                return Boolean.TRUE;
            }
            if (text.equals("2") || text.equals("NO") || text.equals("N")) {
                return Boolean.FALSE;
            }
            printError(label + ": enter 1 for YES or 2 for NO.");
            prompt = retryPrompt(no, label);
        }
    }

    private static <T extends Identifiable> Object askRecord(int no, String label, String entity, List<T> items,
                                                             Object current, Function<T, String> describe,
                                                             Function<T, String> rejectReason,
                                                             Supplier<T> registerNew, String noneOption) {
        while (true) {
            System.out.println(String.format("%s %-20s[%s | select]", two(no), label, entity));
            boxTitle("CHOOSE " + entity.toUpperCase());
            if (items.isEmpty()) {
                boxLine("No " + entity + " is registered yet.");
                if (registerNew != null) {
                    boxLine("Register one now - your other entries are kept.");
                }
            }
            int option = 0;
            for (T item : items) {
                option++;
                boxLine(option + " " + (describe == null ? Identifiable.labelOf(item) : describe.apply(item)));
            }
            int registerOption = -1;
            int noneChoice = -1;
            if (registerNew != null) {
                registerOption = ++option;
                boxLine(registerOption + " [+] Register a new " + entity);
            }
            if (noneOption != null) {
                noneChoice = ++option;
                boxLine(noneChoice + " [-] " + noneOption);
            }
            boxLine("0 Cancel " + openForms.peek());
            System.out.println(LINE);
            String keep = current == null ? "" : ", ENTER = keep " + display(current);
            Object in = readFieldInput("Select option (BACK = previous field" + keep + "): ");
            if (in == BACK || in == CANCEL) {
                return in;
            }
            String text = (String) in;
            if (text.isEmpty()) {
                if (current != null) {
                    return current;
                }
                printError("Please choose an option from the list.");
                continue;
            }
            int pick;
            try {
                pick = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                printError("Invalid selection: '" + text + "' is not a number from the list.");
                continue;
            }
            if (pick == 0) {
                return CANCEL;
            } else if (pick >= 1 && pick <= items.size()) {
                T chosen = items.get(pick - 1);
                String reason = rejectReason == null ? null : rejectReason.apply(chosen);
                if (reason == null) {
                    return chosen;
                }
                printError(reason);
            } else if (pick == registerOption) {
                T created = registerNew.get(); // opens the other form, this one keeps its values
                if (created != null) {
                    printInfo("Back in " + openForms.peek() + " at field " + two(no) + ". Choose the " + entity + " below.");
                }
            } else if (pick == noneChoice) {
                return NONE;
            } else {
                printError("Invalid selection: choose a number from 0 to " + option + ".");
            }
        }
    }

    // checks used by the forms (unique ids, missing records ...)

    private static String firstError(String... errors) {
        for (String error : errors) {
            if (error != null) {
                return error;
            }
        }
        return null;
    }

    private static String after(LocalDate date, Object other, String label, String otherLabel) {
        if (other instanceof LocalDate && !date.isAfter((LocalDate) other)) {
            return label + " must be after " + otherLabel + " (" + Registrable.formatDate((LocalDate) other) + ").";
        }
        return null;
    }

    private static String notBefore(LocalDate date, LocalDate other, String label, String otherLabel) {
        if (other != null && date.isBefore(other)) {
            return label + " cannot be before " + otherLabel + " (" + Registrable.formatDate(other) + ").";
        }
        return null;
    }

    private static List<Person> allPeople() {
        List<Person> people = new ArrayList<>();
        people.addAll(students);
        people.addAll(teachers);
        people.addAll(administrators);
        return people;
    }

    private static List<AcademicMember> academicMembers() {
        List<AcademicMember> members = new ArrayList<>();
        members.addAll(students);
        members.addAll(teachers);
        return members;
    }

    private static List<Identifiable> allRecords() {
        List<Identifiable> all = new ArrayList<>();
        for (int type = 1; type < TYPE_NAMES.length; type++) {
            all.addAll(recordsOfType(type));
        }
        return all;
    }

    private static List<? extends Identifiable> recordsOfType(int type) {
        switch (type) {
            case 1: return departments;
            case 2: return students;
            case 3: return teachers;
            case 4: return administrators;
            case 5: return courses;
            case 6: return classrooms;
            case 7: return enrollments;
            case 8: return assessments;
            case 9: return payments;
            case 10: return books;
            case 11: return loans;
            default: return new ArrayList<Identifiable>();
        }
    }

    private static void ensureIdAvailable(String id, Object self) throws DuplicateRecordException {
        for (Identifiable record : allRecords()) {
            if (record != self && record.getId() != null && record.getId().equalsIgnoreCase(id.trim())) {
                throw new DuplicateRecordException("ID " + id.trim() + " already exists (used by "
                        + record.getClass().getSimpleName() + " " + Identifiable.labelOf(record) + ").");
            }
        }
    }

    private static <T> void ensureUnique(String label, String value, Object self, List<T> list,
                                         Function<T, String> keyOf) throws DuplicateRecordException {
        for (T item : list) {
            String key = keyOf.apply(item);
            if (item != self && key != null && key.trim().equalsIgnoreCase(value.trim())) {
                throw new DuplicateRecordException(label + " " + value.trim() + " already exists.");
            }
        }
    }

    private static void ensureEmailAvailable(String label, String email, Object self) throws DuplicateRecordException {
        for (Person person : allPeople()) {
            if (person == self) {
                continue;
            }
            boolean used = email.trim().equalsIgnoreCase(person.getEmail())
                    || person instanceof AcademicMember
                    && email.trim().equalsIgnoreCase(((AcademicMember) person).getInstitutionalEmail());
            if (used) {
                throw new DuplicateRecordException(label + " " + email.trim() + " is already used by "
                        + Identifiable.labelOf(person) + ".");
            }
        }
    }

    // same checks but they return the message instead of throwing, so the field is asked again
    private static String idError(String id, Object self) {
        try {
            ensureIdAvailable(id, self);
            return null;
        } catch (DuplicateRecordException e) {
            return e.getMessage();
        }
    }

    private static <T> String uniqueError(String label, String value, Object self, List<T> list, Function<T, String> keyOf) {
        try {
            ensureUnique(label, value, self, list, keyOf);
            return null;
        } catch (DuplicateRecordException e) {
            return e.getMessage();
        }
    }

    private static String emailError(String label, String email, Object self) {
        try {
            ensureEmailAvailable(label, email, self);
            return null;
        } catch (DuplicateRecordException e) {
            return e.getMessage();
        }
    }

    private static void ensureRegistered(Object record, List<?> list, String entity) throws PrerequisiteMissingException {
        if (record == null || !list.contains(record)) {
            throw new PrerequisiteMissingException(entity + " is missing or not registered. Choose an existing "
                    + entity + ".");
        }
    }

    private static void checkPrerequisite(boolean exists, String message) throws PrerequisiteMissingException {
        if (!exists) {
            throw new PrerequisiteMissingException(message);
        }
    }

    private static String missing(String entity, String task) {
        String lower = entity.toLowerCase();
        return "No " + lower + " exists. Register " + article(lower) + " " + lower + " before " + task + ".";
    }

    private static boolean ensurePrerequisite(BooleanSupplier exists, String message, String entity,
                                              String returnTo, Supplier<?> register) {
        while (true) {
            try {
                checkPrerequisite(exists.getAsBoolean(), message);
                return true;
            } catch (PrerequisiteMissingException e) {
                printError(e.getMessage());
                System.out.println("  1. Register " + entity + " now");
                System.out.println("  2. " + (returnTo == null ? "Return to menu" : "Go back to " + returnTo));
                if (readChoice("Select: ", 1, 2) != 1) {
                    return false;
                }
                register.get();
            }
        }
    }

    private static void ensureBookAvailable(LibraryBook book) throws BookUnavailableException {
        if (book.getAvailableCopies() <= 0) {
            throw new BookUnavailableException("No copy of \"" + book.getTitle() + "\" is available (0 of "
                    + book.getTotalCopies() + " copies left). Choose another book or wait for a return.");
        }
    }

    private static <T> T formCancelled(String entity, Identifiable existing, String returnTo) {
        if (existing != null) {
            printInfo("Update cancelled. " + entity + " " + existing.getId() + " was not changed.");
        } else {
            printInfo(entity + " registration cancelled. Nothing was saved.");
        }
        if (returnTo != null) {
            printInfo("Returning to " + returnTo + "...");
        }
        return null;
    }

    private static void saveFailed(Exception e) {
        printError("Cannot save: " + e.getMessage());
        printInfo("Returning to the review screen. Use [2] Edit field to correct it.");
    }

    private static boolean registeredSuccessfully(Identifiable saved, String entity, String returnTo) {
        Registrable record = (Registrable) saved;
        printSuccess(entity + " " + saved.getId() + " registered successfully.");
        printInfo(record.getRegistrationSummary());
        if (returnTo != null) {
            printInfo(entity + " saved. Returning to " + returnTo + "...");
            return false;
        }
        while (true) {
            System.out.println("  [1] Register another   [2] View " + entity.toLowerCase() + "   [0] Main menu");
            int choice = readChoice("Select option: ", 0, 2);
            if (choice == 1) {
                return true;
            }
            if (choice == 0) {
                return false;
            }
            record.printDetails();
        }
    }

    private static int confirmChanges(String entity, Identifiable existing, String[] labels, Object[] before, Object[] after) {
        List<String> changes = new ArrayList<>();
        for (int f = 1; f < labels.length; f++) {
            if (!Objects.equals(before[f], after[f])) {
                changes.add(two(f) + " " + labels[f] + ": " + display(before[f]) + " -> " + display(after[f]));
            }
        }
        if (changes.isEmpty()) {
            printInfo("No field was changed.");
            return 0;
        }
        boxTitle("CONFIRM CHANGES");
        boxLine(entity + " " + Identifiable.labelOf(existing));
        for (String change : changes) {
            boxLine(change);
        }
        System.out.println(LINE);
        System.out.println("  [1] Apply changes   [2] Keep editing   [0] Discard changes");
        return readChoice("Select option: ", 0, 2);
    }

    private static void updated(String entity, Identifiable record) {
        printSuccess(entity + " " + record.getId() + " updated successfully.");
        printInfo(((Registrable) record).getRegistrationSummary());
    }

    // text shown in the selection lists
    private static String teacherLabel(Teacher t) {
        String dept = t.getDepartment() == null ? "-" : t.getDepartment().getDepartmentCode();
        return Identifiable.labelOf(t) + " - " + dept + (t.isActive() ? "" : " (INACTIVE)");
    }

    private static String studentLabel(Student s) {
        return Identifiable.labelOf(s) + " - " + s.getStudentNumber() + ", year " + s.getStudyYear();
    }

    private static String enrollmentLabel(Enrollment e) {
        return Identifiable.labelOf(e) + " - " + e.getEnrollmentStatus();
    }

    private static String bookLabel(LibraryBook b) {
        return Identifiable.labelOf(b) + " (" + b.getAvailableCopies() + "/" + b.getTotalCopies() + " available)";
    }

    private static String loanLabel(BookLoan l) {
        return Identifiable.labelOf(l) + " - due " + Registrable.formatDate(l.getDueDate());
    }

    // fields 01-06 (person) and 07-12 (academic member) are the same for student and teacher
    private static final String[] PERSON_LABELS = {"Person ID", "First name", "Last name", "Email", "Phone",
        "Date of birth"};
    private static final String[] ACADEMIC_LABELS = {"Member number", "Department", "Join date", "Academic status",
        "Institutional email", "Advisor name"};
    private static final int[] MEMBER_FIELD_STEPS = {0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3};

    private static String[] labels(String[]... groups) {
        List<String> all = new ArrayList<>();
        all.add("");
        for (String[] group : groups) {
            all.addAll(Arrays.asList(group));
        }
        return all.toArray(new String[0]);
    }

    private static Object readPersonField(int f, Object[] v, Person self) {
        switch (f) {
            case 1: return askText(1, "Person ID", "String | unique | 4-30 chars", v[1],
                    s -> firstError(Identifiable.checkId(s, "Person ID"), idError(s, self)));
            case 2: return askText(2, "First name", "String | 2-60 chars", v[2], s -> Registrable.checkName(s, "First name"));
            case 3: return askText(3, "Last name", "String | 2-60 chars", v[3], s -> Registrable.checkName(s, "Last name"));
            case 4: return askText(4, "Email", "String | unique | email", v[4],
                    s -> firstError(Registrable.checkEmail(s, "Email"), emailError("Email", s, self)));
            case 5: return askText(5, "Phone", "String | 10-15 digits", v[5], s -> Registrable.checkPhone(s, "Phone"));
            case 6: return askDate(6, "Date of birth", "LocalDate | YYYY-MM-DD", v[6],
                    d -> Registrable.checkPastDate(d, "Date of birth"));
            default: return NONE;
        }
    }

    private static Object readAcademicField(int f, Object[] v, AcademicMember self) {
        switch (f) {
            case 7: return askText(7, "Member number", "String | unique | 4-30", v[7],
                    s -> firstError(Identifiable.checkId(s, "Member number"),
                            uniqueError("Member number", s, self, academicMembers(), AcademicMember::getMemberNumber)));
            case 8: return askRecord(8, "Department", "Department", departments, v[8], null, null,
                    () -> departmentForm(null, openForms.peek()), null);
            case 9: return askDate(9, "Join date", "LocalDate | YYYY-MM-DD", v[9],
                    d -> firstError(Registrable.checkNotFuture(d, "Join date"), after(d, v[6], "Join date", "the date of birth")));
            case 10: return askChoice(10, "Academic status", Registrable.allowedValues("academicStatus"), v[10], null);
            case 11: return askText(11, "Institutional email", "String | unique | @auca.ac.rw", v[11],
                    s -> firstError(Registrable.checkInstitutionalEmail(s, "Institutional email"),
                            s.equalsIgnoreCase(String.valueOf(v[4])) ? "Institutional email must be different from the personal email." : null,
                            emailError("Institutional email", s, self)));
            case 12: return askText(12, "Advisor name", "String | 2-120 chars", v[12],
                    s -> Registrable.checkText(s, "Advisor name", 2, 120));
            default: return NONE;
        }
    }

    private static void putPersonValues(Person p, Object[] v) {
        v[1] = p.getPersonId();
        v[2] = p.getFirstName();
        v[3] = p.getLastName();
        v[4] = p.getEmail();
        v[5] = p.getPhoneNumber();
        v[6] = p.getDateOfBirth();
    }

    private static void putAcademicValues(AcademicMember m, Object[] v) {
        v[7] = m.getMemberNumber();
        v[8] = m.getDepartment();
        v[9] = m.getJoinDate();
        v[10] = m.getAcademicStatus();
        v[11] = m.getInstitutionalEmail();
        v[12] = m.getAdvisorName();
    }

    private static void applyPersonValues(Person p, Object[] v) {
        p.setPersonId((String) v[1]);
        p.setFirstName((String) v[2]);
        p.setLastName((String) v[3]);
        p.setEmail((String) v[4]);
        p.setPhoneNumber((String) v[5]);
        p.setDateOfBirth((LocalDate) v[6]);
    }

    private static void applyAcademicValues(AcademicMember m, Object[] v) {
        m.setMemberNumber((String) v[7]);
        m.setDepartment((Department) v[8]);
        m.setJoinDate((LocalDate) v[9]);
        m.setAcademicStatus((String) v[10]);
        m.setInstitutionalEmail((String) v[11]);
        m.setAdvisorName((String) v[12]);
    }

    private static void checkAcademicRules(AcademicMember m, Object self)
            throws DuplicateRecordException, PrerequisiteMissingException {
        ensureIdAvailable(m.getPersonId(), self);
        ensureEmailAvailable("Email", m.getEmail(), self);
        ensureEmailAvailable("Institutional email", m.getInstitutionalEmail(), self);
        ensureUnique("Member number", m.getMemberNumber(), self, academicMembers(), AcademicMember::getMemberNumber);
        ensureRegistered(m.getDepartment(), departments, "Department");
    }

    // department
    private static final String[] DEPARTMENT_STEPS = {"Identification", "Contact details", "Leadership", "Review & confirm"};
    private static final String[] DEPARTMENT_LABELS = labels(new String[] {"Department ID", "Department name",
        "Department code", "Office location", "Contact email", "Head teacher"});
    private static final int[] DEPARTMENT_FIELD_STEPS = {0, 1, 1, 1, 2, 2, 3};

    private static Object readDepartmentField(int f, Object[] v, Department self) {
        switch (f) {
            case 1: return askText(1, "Department ID", "String | unique | 4-30 chars", v[1],
                    s -> firstError(Identifiable.checkId(s, "Department ID"), idError(s, self)));
            case 2: return askText(2, "Department name", "String | unique | 2-60 chars", v[2],
                    s -> firstError(Registrable.checkText(s, "Department name", 2, 60),
                            uniqueError("Department name", s, self, departments, Department::getDepartmentName)));
            case 3: return askText(3, "Department code", "String | unique | 2-10 chars", v[3],
                    s -> firstError(Registrable.checkCode(s, "Department code", 2, 10),
                            uniqueError("Department code", s, self, departments, Department::getDepartmentCode)));
            case 4: return askText(4, "Office location", "String | 2-60 chars", v[4],
                    s -> Registrable.checkText(s, "Office location", 2, 60));
            case 5: return askText(5, "Contact email", "String | email", v[5], s -> Registrable.checkEmail(s, "Contact email"));
            // no "register new" here: a teacher needs a department, it would go in a circle
            case 6: return askRecord(6, "Head teacher", "Teacher", teachers, v[6], Main::teacherLabel, null, null,
                    "No head teacher yet (assign later)");
            default: return NONE;
        }
    }

    private static Department buildDepartment(Object[] v) {
        if (v[6] instanceof Teacher) {
            return new Department((String) v[1], (String) v[2], (String) v[3], (String) v[4], (String) v[5], (Teacher) v[6]);
        }
        return new Department((String) v[1], (String) v[2], (String) v[3], (String) v[4], (String) v[5]);
    }

    private static Object[] departmentValues(Department d) {
        return new Object[] {null, d.getDepartmentId(), d.getDepartmentName(), d.getDepartmentCode(),
            d.getOfficeLocation(), d.getContactEmail(), d.getHeadTeacher() == null ? NONE : d.getHeadTeacher()};
    }

    private static void checkDepartmentRules(Department d, Department self)
            throws DuplicateRecordException, PrerequisiteMissingException {
        ensureIdAvailable(d.getDepartmentId(), self);
        ensureUnique("Department name", d.getDepartmentName(), self, departments, Department::getDepartmentName);
        ensureUnique("Department code", d.getDepartmentCode(), self, departments, Department::getDepartmentCode);
        if (d.getHeadTeacher() != null) {
            ensureRegistered(d.getHeadTeacher(), teachers, "Head teacher");
        }
    }

    private static Department departmentForm(Department existing, String returnTo) {
        boolean updating = existing != null;
        Object[] v = updating ? departmentValues(existing) : new Object[DEPARTMENT_LABELS.length];
        boolean review = updating;
        while (true) {
            if (!runForm("DEPARTMENT", DEPARTMENT_STEPS, DEPARTMENT_FIELD_STEPS, DEPARTMENT_LABELS, v, null,
                    updating, review, f -> readDepartmentField(f, v, existing))) {
                return formCancelled("Department", existing, returnTo);
            }
            try {
                Department d = buildDepartment(v);
                d.validate();
                checkDepartmentRules(d, existing);
                if (updating) {
                    int decision = confirmChanges("Department", existing, DEPARTMENT_LABELS, departmentValues(existing), v);
                    if (decision == 2) {
                        continue;
                    }
                    if (decision == 0) {
                        return formCancelled("Department", existing, returnTo);
                    }
                    existing.setDepartmentId(d.getDepartmentId());
                    existing.setDepartmentName(d.getDepartmentName());
                    existing.setDepartmentCode(d.getDepartmentCode());
                    existing.setOfficeLocation(d.getOfficeLocation());
                    existing.setContactEmail(d.getContactEmail());
                    existing.setHeadTeacher(d.getHeadTeacher());
                    updated("Department", existing);
                    return existing;
                }
                departments.add(d);
                if (!registeredSuccessfully(d, "Department", returnTo)) {
                    return d;
                }
                Arrays.fill(v, null);
                review = false;
            } catch (ValidationException | DuplicateRecordException | PrerequisiteMissingException e) {
                saveFailed(e);
                review = true;
            }
        }
    }

    // student
    private static final String[] STUDENT_STEPS = {"Personal details", "Academic details", "Student details",
        "Review & confirm"};
    private static final String[] STUDENT_LABELS = labels(PERSON_LABELS, ACADEMIC_LABELS, new String[] {
        "Student number", "Program name", "Admission date", "Study year", "Guardian phone", "Scholarship"});

    private static Object readStudentField(int f, Object[] v, Student self) {
        if (f <= 6) {
            return readPersonField(f, v, self);
        }
        if (f <= 12) {
            return readAcademicField(f, v, self);
        }
        switch (f) {
            case 13: return askText(13, "Student number", "String | unique | 4-30", v[13],
                    s -> firstError(Identifiable.checkId(s, "Student number"),
                            uniqueError("Student number", s, self, students, Student::getStudentNumber)));
            case 14: return askText(14, "Program name", "String | 2-120 chars", v[14],
                    s -> Registrable.checkText(s, "Program name", 2, 120));
            case 15: return askDate(15, "Admission date", "LocalDate | YYYY-MM-DD", v[15],
                    d -> firstError(Registrable.checkNotFuture(d, "Admission date"),
                            after(d, v[6], "Admission date", "the date of birth")));
            case 16: return askInt(16, "Study year", "int | 1-6", v[16], n -> Registrable.checkRange(n, "Study year", 1, 6));
            case 17: return askText(17, "Guardian phone", "String | 10-15 digits", v[17],
                    s -> Registrable.checkPhone(s, "Guardian phone"));
            case 18: return askYesNo(18, "Scholarship", v[18]);
            default: return NONE;
        }
    }

    private static Student buildStudent(Object[] v) {
        return new Student((String) v[1], (String) v[2], (String) v[3], (String) v[4], (String) v[5], (LocalDate) v[6],
                (String) v[7], (Department) v[8], (LocalDate) v[9], (String) v[10], (String) v[11], (String) v[12],
                (String) v[13], (String) v[14], (LocalDate) v[15], (Integer) v[16], (String) v[17], (Boolean) v[18]);
    }

    private static Object[] studentValues(Student s) {
        Object[] v = new Object[STUDENT_LABELS.length];
        putPersonValues(s, v);
        putAcademicValues(s, v);
        v[13] = s.getStudentNumber();
        v[14] = s.getProgramName();
        v[15] = s.getAdmissionDate();
        v[16] = s.getStudyYear();
        v[17] = s.getGuardianPhone();
        v[18] = s.isScholarshipStatus();
        return v;
    }

    private static Student studentForm(Student existing, String returnTo) {
        boolean updating = existing != null;
        Object[] v = updating ? studentValues(existing) : new Object[STUDENT_LABELS.length];
        if (!updating && departments.isEmpty()) {
            printInfo("Note: no Department exists yet. At field 08 you can register one without losing your entries.");
        }
        boolean review = updating;
        while (true) {
            if (!runForm("STUDENT", STUDENT_STEPS, MEMBER_FIELD_STEPS, STUDENT_LABELS, v, null, updating, review,
                    f -> readStudentField(f, v, existing))) {
                return formCancelled("Student", existing, returnTo);
            }
            try {
                Student s = buildStudent(v);
                s.validate();
                checkAcademicRules(s, existing);
                ensureUnique("Student number", s.getStudentNumber(), existing, students, Student::getStudentNumber);
                if (updating) {
                    int decision = confirmChanges("Student", existing, STUDENT_LABELS, studentValues(existing), v);
                    if (decision == 2) {
                        continue;
                    }
                    if (decision == 0) {
                        return formCancelled("Student", existing, returnTo);
                    }
                    applyPersonValues(existing, v);
                    applyAcademicValues(existing, v);
                    existing.setStudentNumber(s.getStudentNumber());
                    existing.setProgramName(s.getProgramName());
                    existing.setAdmissionDate(s.getAdmissionDate());
                    existing.setStudyYear(s.getStudyYear());
                    existing.setGuardianPhone(s.getGuardianPhone());
                    existing.setScholarshipStatus(s.isScholarshipStatus());
                    updated("Student", existing);
                    return existing;
                }
                students.add(s);
                if (!registeredSuccessfully(s, "Student", returnTo)) {
                    return s;
                }
                Arrays.fill(v, null);
                review = false;
            } catch (ValidationException | DuplicateRecordException | PrerequisiteMissingException e) {
                saveFailed(e);
                review = true;
            }
        }
    }

    // teacher
    private static final String[] TEACHER_STEPS = {"Personal details", "Academic details", "Teacher details",
        "Review & confirm"};
    private static final String[] TEACHER_LABELS = labels(PERSON_LABELS, ACADEMIC_LABELS, new String[] {
        "Employee number", "Specialization", "Hire date", "Salary", "Office number", "Employment type"});

    private static Object readTeacherField(int f, Object[] v, Teacher self) {
        if (f <= 6) {
            return readPersonField(f, v, self);
        }
        if (f <= 12) {
            return readAcademicField(f, v, self);
        }
        switch (f) {
            case 13: return askText(13, "Employee number", "String | unique | 4-30", v[13],
                    s -> firstError(Identifiable.checkId(s, "Employee number"),
                            uniqueError("Employee number", s, self, teachers, Teacher::getEmployeeNumber)));
            case 14: return askText(14, "Specialization", "String | 2-60 chars", v[14],
                    s -> Registrable.checkText(s, "Specialization", 2, 60));
            case 15: return askDate(15, "Hire date", "LocalDate | YYYY-MM-DD", v[15],
                    d -> firstError(Registrable.checkNotFuture(d, "Hire date"), after(d, v[6], "Hire date", "the date of birth")));
            case 16: return askMoney(16, "Salary", "BigDecimal | > 0 | RWF", v[16],
                    a -> Registrable.checkPositiveAmount(a, "Salary"));
            case 17: return askText(17, "Office number", "String | 1-20 chars", v[17],
                    s -> Registrable.checkText(s, "Office number", 1, 20));
            case 18: return askChoice(18, "Employment type", Registrable.allowedValues("employmentType"), v[18], null);
            default: return NONE;
        }
    }

    private static Teacher buildTeacher(Object[] v) {
        return new Teacher((String) v[1], (String) v[2], (String) v[3], (String) v[4], (String) v[5], (LocalDate) v[6],
                (String) v[7], (Department) v[8], (LocalDate) v[9], (String) v[10], (String) v[11], (String) v[12],
                (String) v[13], (String) v[14], (LocalDate) v[15], (BigDecimal) v[16], (String) v[17], (String) v[18]);
    }

    private static Object[] teacherValues(Teacher t) {
        Object[] v = new Object[TEACHER_LABELS.length];
        putPersonValues(t, v);
        putAcademicValues(t, v);
        v[13] = t.getEmployeeNumber();
        v[14] = t.getSpecialization();
        v[15] = t.getHireDate();
        v[16] = t.getSalary();
        v[17] = t.getOfficeNumber();
        v[18] = t.getEmploymentType();
        return v;
    }

    private static Teacher teacherForm(Teacher existing, String returnTo) {
        boolean updating = existing != null;
        Object[] v = updating ? teacherValues(existing) : new Object[TEACHER_LABELS.length];
        if (!updating && departments.isEmpty()) {
            printInfo("Note: no Department exists yet. At field 08 you can register one without losing your entries.");
        }
        boolean review = updating;
        while (true) {
            if (!runForm("TEACHER", TEACHER_STEPS, MEMBER_FIELD_STEPS, TEACHER_LABELS, v, null, updating, review,
                    f -> readTeacherField(f, v, existing))) {
                return formCancelled("Teacher", existing, returnTo);
            }
            try {
                Teacher t = buildTeacher(v);
                t.validate();
                checkAcademicRules(t, existing);
                ensureUnique("Employee number", t.getEmployeeNumber(), existing, teachers, Teacher::getEmployeeNumber);
                if (updating) {
                    int decision = confirmChanges("Teacher", existing, TEACHER_LABELS, teacherValues(existing), v);
                    if (decision == 2) {
                        continue;
                    }
                    if (decision == 0) {
                        return formCancelled("Teacher", existing, returnTo);
                    }
                    applyPersonValues(existing, v);
                    applyAcademicValues(existing, v);
                    existing.setEmployeeNumber(t.getEmployeeNumber());
                    existing.setSpecialization(t.getSpecialization());
                    existing.setHireDate(t.getHireDate());
                    existing.setSalary(t.getSalary());
                    existing.setOfficeNumber(t.getOfficeNumber());
                    existing.setEmploymentType(t.getEmploymentType());
                    updated("Teacher", existing);
                    return existing;
                }
                teachers.add(t);
                offerHeadOfDepartment(t);
                if (!registeredSuccessfully(t, "Teacher", returnTo)) {
                    return t;
                }
                Arrays.fill(v, null);
                review = false;
            } catch (ValidationException | DuplicateRecordException | PrerequisiteMissingException e) {
                saveFailed(e);
                review = true;
            }
        }
    }

    private static void offerHeadOfDepartment(Teacher teacher) {
        Department department = teacher.getDepartment();
        if (department != null && department.getHeadTeacher() == null
                && confirm("Department " + Identifiable.labelOf(department) + " has no head teacher. Make "
                        + teacher.getDisplayName() + " the head teacher?")) {
            department.setHeadTeacher(teacher);
            printSuccess(teacher.getDisplayName() + " is now head teacher of " + department.getDepartmentName() + ".");
        }
    }

    // administrator
    private static final String[] ADMINISTRATOR_STEPS = {"Personal details", "Job details", "Employment status",
        "Review & confirm"};
    private static final String[] ADMINISTRATOR_LABELS = labels(PERSON_LABELS, new String[] {
        "Staff number", "Job title", "Assigned office", "Employment date", "Access level", "Active"});
    private static final int[] ADMINISTRATOR_FIELD_STEPS = {0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 3, 3, 3};

    private static Object readAdministratorField(int f, Object[] v, Administrator self) {
        if (f <= 6) {
            return readPersonField(f, v, self);
        }
        switch (f) {
            case 7: return askText(7, "Staff number", "String | unique | 4-30", v[7],
                    s -> firstError(Identifiable.checkId(s, "Staff number"),
                            uniqueError("Staff number", s, self, administrators, Administrator::getStaffNumber)));
            case 8: return askText(8, "Job title", "String | 2-60 chars", v[8], s -> Registrable.checkText(s, "Job title", 2, 60));
            case 9: return askText(9, "Assigned office", "String | 2-60 chars", v[9],
                    s -> Registrable.checkText(s, "Assigned office", 2, 60));
            case 10: return askDate(10, "Employment date", "LocalDate | YYYY-MM-DD", v[10],
                    d -> firstError(Registrable.checkNotFuture(d, "Employment date"),
                            after(d, v[6], "Employment date", "the date of birth")));
            case 11: return askInt(11, "Access level", "int | 1-5", v[11], n -> Registrable.checkRange(n, "Access level", 1, 5));
            case 12: return askYesNo(12, "Active", v[12]);
            default: return NONE;
        }
    }

    private static Administrator buildAdministrator(Object[] v) {
        return new Administrator((String) v[1], (String) v[2], (String) v[3], (String) v[4], (String) v[5],
                (LocalDate) v[6], (String) v[7], (String) v[8], (String) v[9], (LocalDate) v[10], (Integer) v[11],
                (Boolean) v[12]);
    }

    private static Object[] administratorValues(Administrator a) {
        Object[] v = new Object[ADMINISTRATOR_LABELS.length];
        putPersonValues(a, v);
        v[7] = a.getStaffNumber();
        v[8] = a.getJobTitle();
        v[9] = a.getAssignedOffice();
        v[10] = a.getEmploymentDate();
        v[11] = a.getAccessLevel();
        v[12] = a.isActive();
        return v;
    }

    private static Administrator administratorForm(Administrator existing, String returnTo) {
        boolean updating = existing != null;
        Object[] v = updating ? administratorValues(existing) : new Object[ADMINISTRATOR_LABELS.length];
        boolean review = updating;
        while (true) {
            if (!runForm("ADMINISTRATOR", ADMINISTRATOR_STEPS, ADMINISTRATOR_FIELD_STEPS, ADMINISTRATOR_LABELS, v, null,
                    updating, review, f -> readAdministratorField(f, v, existing))) {
                return formCancelled("Administrator", existing, returnTo);
            }
            try {
                Administrator a = buildAdministrator(v);
                a.validate();
                ensureIdAvailable(a.getPersonId(), existing);
                ensureEmailAvailable("Email", a.getEmail(), existing);
                ensureUnique("Staff number", a.getStaffNumber(), existing, administrators, Administrator::getStaffNumber);
                if (updating) {
                    int decision = confirmChanges("Administrator", existing, ADMINISTRATOR_LABELS,
                            administratorValues(existing), v);
                    if (decision == 2) {
                        continue;
                    }
                    if (decision == 0) {
                        return formCancelled("Administrator", existing, returnTo);
                    }
                    applyPersonValues(existing, v);
                    existing.setStaffNumber(a.getStaffNumber());
                    existing.setJobTitle(a.getJobTitle());
                    existing.setAssignedOffice(a.getAssignedOffice());
                    existing.setEmploymentDate(a.getEmploymentDate());
                    existing.setAccessLevel(a.getAccessLevel());
                    existing.setActive(a.isActive());
                    updated("Administrator", existing);
                    return existing;
                }
                administrators.add(a);
                if (!registeredSuccessfully(a, "Administrator", returnTo)) {
                    return a;
                }
                Arrays.fill(v, null);
                review = false;
            } catch (ValidationException | DuplicateRecordException e) {
                saveFailed(e);
                review = true;
            }
        }
    }

    // course
    private static final String[] COURSE_STEPS = {"Identification", "Course details", "Department & teacher",
        "Review & confirm"};
    private static final String[] COURSE_LABELS = labels(new String[] {"Course ID", "Course code", "Course title",
        "Credit hours", "Department", "Assigned teacher"});
    private static final int[] SIX_FIELD_STEPS = {0, 1, 1, 2, 2, 3, 3};

    private static Object readCourseField(int f, Object[] v, Course self) {
        switch (f) {
            case 1: return askText(1, "Course ID", "String | unique | 4-30 chars", v[1],
                    s -> firstError(Identifiable.checkId(s, "Course ID"), idError(s, self)));
            case 2: return askText(2, "Course code", "String | unique | 3-15 chars", v[2],
                    s -> firstError(Registrable.checkCode(s, "Course code", 3, 15),
                            uniqueError("Course code", s, self, courses, Course::getCourseCode)));
            case 3: return askText(3, "Course title", "String | 2-120 chars", v[3],
                    s -> Registrable.checkText(s, "Course title", 2, 120));
            case 4: return askInt(4, "Credit hours", "int | 1-6", v[4], n -> Registrable.checkRange(n, "Credit hours", 1, 6));
            case 5: return askRecord(5, "Department", "Department", departments, v[5], null, null,
                    () -> departmentForm(null, openForms.peek()), null);
            case 6: return askRecord(6, "Assigned teacher", "Teacher", teachers, v[6], Main::teacherLabel, null,
                    () -> teacherForm(null, openForms.peek()), null);
            default: return NONE;
        }
    }

    private static Course buildCourse(Object[] v) {
        return new Course((String) v[1], (String) v[2], (String) v[3], (Integer) v[4], (Department) v[5], (Teacher) v[6]);
    }

    private static Object[] courseValues(Course c) {
        return new Object[] {null, c.getCourseId(), c.getCourseCode(), c.getCourseTitle(), c.getCreditHours(),
            c.getDepartment(), c.getAssignedTeacher()};
    }

    private static Course courseForm(Course existing, String returnTo) {
        boolean updating = existing != null;
        if (!updating) {
            if (departments.isEmpty() || teachers.isEmpty()) {
                printInfo("A Course requires a Department and a Teacher.");
            }
            if (!ensurePrerequisite(() -> !departments.isEmpty(), missing("Department", "registering a course"),
                    "Department", returnTo, () -> departmentForm(null, "course registration"))
                || !ensurePrerequisite(() -> !teachers.isEmpty(), missing("Teacher", "registering a course"),
                    "Teacher", returnTo, () -> teacherForm(null, "course registration"))) {
                return formCancelled("Course", null, returnTo);
            }
        }
        Object[] v = updating ? courseValues(existing) : new Object[COURSE_LABELS.length];
        boolean review = updating;
        while (true) {
            if (!runForm("COURSE", COURSE_STEPS, SIX_FIELD_STEPS, COURSE_LABELS, v, null, updating, review,
                    f -> readCourseField(f, v, existing))) {
                return formCancelled("Course", existing, returnTo);
            }
            try {
                Course c = buildCourse(v);
                c.validate();
                ensureIdAvailable(c.getCourseId(), existing);
                ensureUnique("Course code", c.getCourseCode(), existing, courses, Course::getCourseCode);
                ensureRegistered(c.getDepartment(), departments, "Department");
                ensureRegistered(c.getAssignedTeacher(), teachers, "Teacher");
                if (updating) {
                    int decision = confirmChanges("Course", existing, COURSE_LABELS, courseValues(existing), v);
                    if (decision == 2) {
                        continue;
                    }
                    if (decision == 0) {
                        return formCancelled("Course", existing, returnTo);
                    }
                    existing.setCourseId(c.getCourseId());
                    existing.setCourseCode(c.getCourseCode());
                    existing.setCourseTitle(c.getCourseTitle());
                    existing.setCreditHours(c.getCreditHours());
                    existing.setDepartment(c.getDepartment());
                    existing.setAssignedTeacher(c.getAssignedTeacher());
                    updated("Course", existing);
                    return existing;
                }
                courses.add(c);
                if (!registeredSuccessfully(c, "Course", returnTo)) {
                    return c;
                }
                Arrays.fill(v, null);
                review = false;
            } catch (ValidationException | DuplicateRecordException | PrerequisiteMissingException e) {
                saveFailed(e);
                review = true;
            }
        }
    }

    // classroom
    private static final String[] CLASSROOM_STEPS = {"Identification", "Capacity & location", "Availability",
        "Review & confirm"};
    private static final String[] CLASSROOM_LABELS = labels(new String[] {"Classroom ID", "Building name",
        "Room number", "Capacity", "Floor number", "Available"});
    private static final int[] CLASSROOM_FIELD_STEPS = {0, 1, 1, 1, 2, 2, 3};

    private static void ensureRoomAvailable(String building, String room, Classroom self) throws DuplicateRecordException {
        for (Classroom c : classrooms) {
            if (c != self && c.getBuildingName().equalsIgnoreCase(building.trim())
                    && c.getRoomNumber().equalsIgnoreCase(room.trim())) {
                throw new DuplicateRecordException("Room " + room.trim() + " already exists in " + c.getBuildingName()
                        + " (" + c.getId() + ").");
            }
        }
    }

    private static String roomError(Object building, String room, Classroom self) {
        if (!(building instanceof String)) {
            return null;
        }
        try {
            ensureRoomAvailable((String) building, room, self);
            return null;
        } catch (DuplicateRecordException e) {
            return e.getMessage();
        }
    }

    private static Object readClassroomField(int f, Object[] v, Classroom self) {
        switch (f) {
            case 1: return askText(1, "Classroom ID", "String | unique | 4-30 chars", v[1],
                    s -> firstError(Identifiable.checkId(s, "Classroom ID"), idError(s, self)));
            case 2: return askText(2, "Building name", "String | 2-60 chars", v[2],
                    s -> Registrable.checkText(s, "Building name", 2, 60));
            case 3: return askText(3, "Room number", "String | 1-20 | unique in building", v[3],
                    s -> firstError(Registrable.checkCode(s, "Room number", 1, 20), roomError(v[2], s, self)));
            case 4: return askInt(4, "Capacity", "int | 1-1000 seats", v[4], n -> Registrable.checkRange(n, "Capacity", 1, 1000));
            case 5: return askInt(5, "Floor number", "int | 0-100", v[5], n -> Registrable.checkRange(n, "Floor number", 0, 100));
            case 6: return askYesNo(6, "Available", v[6]);
            default: return NONE;
        }
    }

    private static Classroom buildClassroom(Object[] v) {
        return new Classroom((String) v[1], (String) v[2], (String) v[3], (Integer) v[4], (Integer) v[5], (Boolean) v[6]);
    }

    private static Object[] classroomValues(Classroom c) {
        return new Object[] {null, c.getClassroomId(), c.getBuildingName(), c.getRoomNumber(), c.getCapacity(),
            c.getFloorNumber(), c.isAvailable()};
    }

    private static Classroom classroomForm(Classroom existing, String returnTo) {
        boolean updating = existing != null;
        Object[] v = updating ? classroomValues(existing) : new Object[CLASSROOM_LABELS.length];
        boolean review = updating;
        while (true) {
            if (!runForm("CLASSROOM", CLASSROOM_STEPS, CLASSROOM_FIELD_STEPS, CLASSROOM_LABELS, v, null, updating, review,
                    f -> readClassroomField(f, v, existing))) {
                return formCancelled("Classroom", existing, returnTo);
            }
            try {
                Classroom c = buildClassroom(v);
                c.validate();
                ensureIdAvailable(c.getClassroomId(), existing);
                ensureRoomAvailable(c.getBuildingName(), c.getRoomNumber(), existing);
                if (updating) {
                    int decision = confirmChanges("Classroom", existing, CLASSROOM_LABELS, classroomValues(existing), v);
                    if (decision == 2) {
                        continue;
                    }
                    if (decision == 0) {
                        return formCancelled("Classroom", existing, returnTo);
                    }
                    existing.setClassroomId(c.getClassroomId());
                    existing.setBuildingName(c.getBuildingName());
                    existing.setRoomNumber(c.getRoomNumber());
                    existing.setCapacity(c.getCapacity());
                    existing.setFloorNumber(c.getFloorNumber());
                    existing.setAvailable(c.isAvailable());
                    updated("Classroom", existing);
                    return existing;
                }
                classrooms.add(c);
                if (!registeredSuccessfully(c, "Classroom", returnTo)) {
                    return c;
                }
                Arrays.fill(v, null);
                review = false;
            } catch (ValidationException | DuplicateRecordException e) {
                saveFailed(e);
                review = true;
            }
        }
    }

    // enrollment
    private static final String[] ENROLLMENT_STEPS = {"Student & course", "Enrollment date", "Semester & status",
        "Review & confirm"};
    private static final String[] ENROLLMENT_LABELS = labels(new String[] {"Enrollment ID", "Student", "Course",
        "Enrollment date", "Semester", "Enrollment status"});
    private static final int[] ENROLLMENT_FIELD_STEPS = {0, 1, 1, 1, 2, 3, 3};
    private static final boolean[] ENROLLMENT_LOCKED = {false, false, true, true, false, false, false};

    private static void ensureNoDuplicateEnrollment(Object student, Object course, Object semester, String status,
                                                    Enrollment self) throws DuplicateRecordException {
        if (!Identifiable.DEFAULT_STATUS.equalsIgnoreCase(status)) {
            return;
        }
        for (Enrollment e : enrollments) {
            if (e != self && e.getStudent() == student && e.getCourse() == course && e.isActive()
                    && e.getSemester().equalsIgnoreCase(String.valueOf(semester))) {
                throw new DuplicateRecordException(e.getStudent().getDisplayName() + " already has an ACTIVE enrollment in "
                        + e.getCourse().getCourseCode() + " for " + e.getSemester() + " (" + e.getId() + ").");
            }
        }
    }

    private static String duplicateEnrollmentError(Object student, Object course, Object semester, String status,
                                                   Enrollment self) {
        try {
            ensureNoDuplicateEnrollment(student, course, semester, status, self);
            return null;
        } catch (DuplicateRecordException e) {
            return e.getMessage();
        }
    }

    private static Object readEnrollmentField(int f, Object[] v, Enrollment self) {
        switch (f) {
            case 1: return askText(1, "Enrollment ID", "String | unique | 4-30 chars", v[1],
                    s -> firstError(Identifiable.checkId(s, "Enrollment ID"), idError(s, self)));
            case 2: return askRecord(2, "Student", "Student", students, v[2], Main::studentLabel, null,
                    () -> studentForm(null, openForms.peek()), null);
            case 3: return askRecord(3, "Course", "Course", courses, v[3], null, null,
                    () -> courseForm(null, openForms.peek()), null);
            case 4: return askDate(4, "Enrollment date", "LocalDate | YYYY-MM-DD", v[4],
                    d -> firstError(Registrable.checkNotFuture(d, "Enrollment date"),
                            notBefore(d, v[2] instanceof Student ? ((Student) v[2]).getAdmissionDate() : null,
                                    "Enrollment date", "the student's admission date")));
            case 5: return askChoice(5, "Semester", Registrable.allowedValues("semester"), v[5], null);
            case 6: return askChoice(6, "Enrollment status", Registrable.allowedValues("enrollmentStatus"), v[6],
                    status -> duplicateEnrollmentError(v[2], v[3], v[5], status, self));
            default: return NONE;
        }
    }

    private static Enrollment buildEnrollment(Object[] v) {
        return new Enrollment((String) v[1], (Student) v[2], (Course) v[3], (LocalDate) v[4], (String) v[5], (String) v[6]);
    }

    private static Object[] enrollmentValues(Enrollment e) {
        return new Object[] {null, e.getEnrollmentId(), e.getStudent(), e.getCourse(), e.getEnrollmentDate(),
            e.getSemester(), e.getEnrollmentStatus()};
    }

    private static Enrollment enrollmentForm(Enrollment existing, String returnTo) {
        boolean updating = existing != null;
        if (!updating && (!ensurePrerequisite(() -> !students.isEmpty(), missing("Student", "enrolling a student"),
                "Student", returnTo, () -> studentForm(null, "enrollment"))
                || !ensurePrerequisite(() -> !courses.isEmpty(), missing("Course", "enrolling a student"),
                "Course", returnTo, () -> courseForm(null, "enrollment")))) {
            return formCancelled("Enrollment", null, returnTo);
        }
        Object[] v = updating ? enrollmentValues(existing) : new Object[ENROLLMENT_LABELS.length];
        boolean review = updating;
        while (true) {
            if (!runForm("ENROLLMENT", ENROLLMENT_STEPS, ENROLLMENT_FIELD_STEPS, ENROLLMENT_LABELS, v,
                    updating ? ENROLLMENT_LOCKED : null, updating, review, f -> readEnrollmentField(f, v, existing))) {
                return formCancelled("Enrollment", existing, returnTo);
            }
            try {
                Enrollment e = buildEnrollment(v);
                e.validate();
                ensureIdAvailable(e.getEnrollmentId(), existing);
                ensureRegistered(e.getStudent(), students, "Student");
                ensureRegistered(e.getCourse(), courses, "Course");
                ensureNoDuplicateEnrollment(e.getStudent(), e.getCourse(), e.getSemester(), e.getEnrollmentStatus(), existing);
                if (updating) {
                    int decision = confirmChanges("Enrollment", existing, ENROLLMENT_LABELS, enrollmentValues(existing), v);
                    if (decision == 2) {
                        continue;
                    }
                    if (decision == 0) {
                        return formCancelled("Enrollment", existing, returnTo);
                    }
                    existing.setEnrollmentId(e.getEnrollmentId());
                    existing.setEnrollmentDate(e.getEnrollmentDate());
                    existing.setSemester(e.getSemester());
                    existing.setEnrollmentStatus(e.getEnrollmentStatus());
                    updated("Enrollment", existing);
                    return existing;
                }
                enrollments.add(e);
                if (!registeredSuccessfully(e, "Enrollment", returnTo)) {
                    return e;
                }
                Arrays.fill(v, null);
                review = false;
            } catch (ValidationException | DuplicateRecordException | PrerequisiteMissingException e) {
                saveFailed(e);
                review = true;
            }
        }
    }

    // assessment
    private static final String[] ASSESSMENT_STEPS = {"Enrollment", "Assessment details", "Scores", "Review & confirm"};
    private static final String[] ASSESSMENT_LABELS = labels(new String[] {"Assessment ID", "Enrollment",
        "Assessment type", "Assessment date", "Maximum score", "Obtained score"});
    private static final boolean[] ASSESSMENT_LOCKED = {false, false, true, false, false, false, false};

    private static boolean hasActiveEnrollment() {
        for (Enrollment e : enrollments) {
            if (e.isActive()) {
                return true;
            }
        }
        return false;
    }

    private static String inactiveEnrollmentReason(Enrollment e) {
        return e.isActive() ? null : "Enrollment " + e.getId() + " is " + e.getEnrollmentStatus()
                + ". Only ACTIVE enrollments can be assessed.";
    }

    private static Object readAssessmentField(int f, Object[] v, Assessment self) {
        switch (f) {
            case 1: return askText(1, "Assessment ID", "String | unique | 4-30 chars", v[1],
                    s -> firstError(Identifiable.checkId(s, "Assessment ID"), idError(s, self)));
            case 2: return askRecord(2, "Enrollment", "Enrollment", enrollments, v[2], Main::enrollmentLabel,
                    Main::inactiveEnrollmentReason, () -> enrollmentForm(null, openForms.peek()), null);
            case 3: return askChoice(3, "Assessment type", Registrable.allowedValues("assessmentType"), v[3], null);
            case 4: return askDate(4, "Assessment date", "LocalDate | YYYY-MM-DD", v[4],
                    d -> firstError(Registrable.checkNotFuture(d, "Assessment date"),
                            notBefore(d, v[2] instanceof Enrollment ? ((Enrollment) v[2]).getEnrollmentDate() : null,
                                    "Assessment date", "the enrollment date")));
            case 5: return askDecimal(5, "Maximum score", "double | > 0", v[5],
                    x -> x <= 0 ? "Maximum score must be greater than zero."
                            : Registrable.checkRange(x, "Maximum score", 0.01, 1000));
            case 6: return askDecimal(6, "Obtained score", "double | 0 to maximum", v[6],
                    x -> v[5] instanceof Double && (x < 0 || x > (Double) v[5])
                            ? "Obtained score must be between 0 and the maximum score (" + display(v[5]) + ")." : null);
            default: return NONE;
        }
    }

    private static Assessment buildAssessment(Object[] v) {
        return new Assessment((String) v[1], (Enrollment) v[2], (String) v[3], (Double) v[5], (Double) v[6],
                (LocalDate) v[4]);
    }

    private static Object[] assessmentValues(Assessment a) {
        return new Object[] {null, a.getAssessmentId(), a.getEnrollment(), a.getAssessmentType(), a.getAssessmentDate(),
            a.getMaximumScore(), a.getObtainedScore()};
    }

    private static Assessment assessmentForm(Assessment existing, String returnTo) {
        boolean updating = existing != null;
        if (!updating && (!ensurePrerequisite(() -> !enrollments.isEmpty(), missing("Enrollment", "recording an assessment"),
                "Enrollment", returnTo, () -> enrollmentForm(null, "assessment recording"))
                || !ensurePrerequisite(() -> hasActiveEnrollment(),
                "No ACTIVE enrollment exists (all are COMPLETED or DROPPED). Only an active enrollment can be assessed.",
                "Enrollment", returnTo, () -> enrollmentForm(null, "assessment recording")))) {
            return formCancelled("Assessment", null, returnTo);
        }
        Object[] v = updating ? assessmentValues(existing) : new Object[ASSESSMENT_LABELS.length];
        boolean review = updating;
        while (true) {
            if (!runForm("ASSESSMENT", ASSESSMENT_STEPS, SIX_FIELD_STEPS, ASSESSMENT_LABELS, v,
                    updating ? ASSESSMENT_LOCKED : null, updating, review, f -> readAssessmentField(f, v, existing))) {
                return formCancelled("Assessment", existing, returnTo);
            }
            try {
                Assessment a = buildAssessment(v);
                a.validate();
                ensureIdAvailable(a.getAssessmentId(), existing);
                ensureRegistered(a.getEnrollment(), enrollments, "Enrollment");
                if (!updating && !a.getEnrollment().isActive()) {
                    throw new ValidationException(inactiveEnrollmentReason(a.getEnrollment()));
                }
                if (updating) {
                    int decision = confirmChanges("Assessment", existing, ASSESSMENT_LABELS, assessmentValues(existing), v);
                    if (decision == 2) {
                        continue;
                    }
                    if (decision == 0) {
                        return formCancelled("Assessment", existing, returnTo);
                    }
                    existing.setAssessmentId(a.getAssessmentId());
                    existing.setAssessmentType(a.getAssessmentType());
                    existing.setAssessmentDate(a.getAssessmentDate());
                    existing.setMaximumScore(a.getMaximumScore());
                    existing.setObtainedScore(a.getObtainedScore());
                    updated("Assessment", existing);
                    return existing;
                }
                assessments.add(a);
                if (!registeredSuccessfully(a, "Assessment", returnTo)) {
                    return a;
                }
                Arrays.fill(v, null);
                review = false;
            } catch (ValidationException | DuplicateRecordException | PrerequisiteMissingException e) {
                saveFailed(e);
                review = true;
            }
        }
    }

    // fee payment
    private static final String[] PAYMENT_STEPS = {"Payer", "Amount & date", "Method & receipt", "Review & confirm"};
    private static final String[] PAYMENT_LABELS = labels(new String[] {"Payment ID", "Student", "Amount",
        "Payment date", "Payment method", "Receipt number"});
    private static final boolean[] PAYMENT_LOCKED = {false, false, true, false, false, false, false};

    private static Object readPaymentField(int f, Object[] v, FeePayment self) {
        switch (f) {
            case 1: return askText(1, "Payment ID", "String | unique | 4-30 chars", v[1],
                    s -> firstError(Identifiable.checkId(s, "Payment ID"), idError(s, self)));
            case 2: return askRecord(2, "Student", "Student", students, v[2], Main::studentLabel, null,
                    () -> studentForm(null, openForms.peek()), null);
            case 3: return askMoney(3, "Amount", "BigDecimal | > 0 | RWF", v[3], a -> Registrable.checkPositiveAmount(a, "Amount"));
            case 4: return askDate(4, "Payment date", "LocalDate | YYYY-MM-DD", v[4],
                    d -> Registrable.checkNotFuture(d, "Payment date"));
            case 5: return askChoice(5, "Payment method", Registrable.allowedValues("paymentMethod"), v[5], null);
            case 6: return askText(6, "Receipt number", "String | unique | 4-30 chars", v[6],
                    s -> firstError(Identifiable.checkId(s, "Receipt number"),
                            uniqueError("Receipt number", s, self, payments, FeePayment::getReceiptNumber)));
            default: return NONE;
        }
    }

    private static FeePayment buildPayment(Object[] v) {
        return new FeePayment((String) v[1], (Student) v[2], (BigDecimal) v[3], (LocalDate) v[4], (String) v[5], (String) v[6]);
    }

    private static Object[] paymentValues(FeePayment p) {
        return new Object[] {null, p.getPaymentId(), p.getStudent(), p.getAmount(), p.getPaymentDate(),
            p.getPaymentMethod(), p.getReceiptNumber()};
    }

    private static FeePayment feePaymentForm(FeePayment existing, String returnTo) {
        boolean updating = existing != null;
        if (!updating && !ensurePrerequisite(() -> !students.isEmpty(), missing("Student", "recording a fee payment"),
                "Student", returnTo, () -> studentForm(null, "fee payment"))) {
            return formCancelled("Fee payment", null, returnTo);
        }
        Object[] v = updating ? paymentValues(existing) : new Object[PAYMENT_LABELS.length];
        boolean review = updating;
        while (true) {
            if (!runForm("FEE PAYMENT", PAYMENT_STEPS, SIX_FIELD_STEPS, PAYMENT_LABELS, v,
                    updating ? PAYMENT_LOCKED : null, updating, review, f -> readPaymentField(f, v, existing))) {
                return formCancelled("Fee payment", existing, returnTo);
            }
            try {
                FeePayment p = buildPayment(v);
                p.validate();
                ensureIdAvailable(p.getPaymentId(), existing);
                ensureRegistered(p.getStudent(), students, "Student");
                ensureUnique("Receipt number", p.getReceiptNumber(), existing, payments, FeePayment::getReceiptNumber);
                if (updating) {
                    int decision = confirmChanges("Fee payment", existing, PAYMENT_LABELS, paymentValues(existing), v);
                    if (decision == 2) {
                        continue;
                    }
                    if (decision == 0) {
                        return formCancelled("Fee payment", existing, returnTo);
                    }
                    existing.setPaymentId(p.getPaymentId());
                    existing.setAmount(p.getAmount());
                    existing.setPaymentDate(p.getPaymentDate());
                    existing.setPaymentMethod(p.getPaymentMethod());
                    existing.setReceiptNumber(p.getReceiptNumber());
                    updated("Fee payment", existing);
                    return existing;
                }
                payments.add(p);
                if (!registeredSuccessfully(p, "Fee payment", returnTo)) {
                    return p;
                }
                Arrays.fill(v, null);
                review = false;
            } catch (ValidationException | DuplicateRecordException | PrerequisiteMissingException e) {
                saveFailed(e);
                review = true;
            }
        }
    }

    // library book
    private static final String[] BOOK_STEPS = {"Identification", "Title & author", "Stock", "Review & confirm"};
    private static final String[] BOOK_LABELS = labels(new String[] {"Book ID", "ISBN", "Title", "Author",
        "Total copies", "Available copies"});

    private static int copiesOnLoan(LibraryBook book) {
        int count = 0;
        for (BookLoan loan : loans) {
            if (loan.getBook() == book && loan.isActive()) {
                count++;
            }
        }
        return count;
    }

    private static String isbnKey(String isbn) {
        return isbn == null ? null : isbn.trim().replace("-", "");
    }

    private static String stockError(int available, Object total, LibraryBook self) {
        if (!(total instanceof Integer)) {
            return null;
        }
        int totalCopies = (Integer) total;
        int onLoan = copiesOnLoan(self);
        if (available + onLoan > totalCopies) {
            return onLoan == 0
                    ? "Available copies (" + available + ") cannot exceed total copies (" + totalCopies + ")."
                    : onLoan + " copies are on loan, so available copies can be at most " + (totalCopies - onLoan) + ".";
        }
        return null;
    }

    private static Object readBookField(int f, Object[] v, LibraryBook self) {
        switch (f) {
            case 1: return askText(1, "Book ID", "String | unique | 4-30 chars", v[1],
                    s -> firstError(Identifiable.checkId(s, "Book ID"), idError(s, self)));
            case 2: return askText(2, "ISBN", "String | unique | 10 or 13 digits", v[2],
                    s -> firstError(Registrable.checkIsbn(s, "ISBN"),
                            uniqueError("ISBN", isbnKey(s), self, books, b -> isbnKey(b.getIsbn()))));
            case 3: return askText(3, "Title", "String | 2-120 chars", v[3], s -> Registrable.checkText(s, "Title", 2, 120));
            case 4: return askText(4, "Author", "String | 2-60 chars", v[4], s -> Registrable.checkName(s, "Author"));
            case 5: return askInt(5, "Total copies", "int | 0-1000", v[5],
                    n -> firstError(Registrable.checkRange(n, "Total copies", 0, 1000),
                            n < copiesOnLoan(self) ? "Total copies cannot be less than the " + copiesOnLoan(self)
                                    + " copies currently on loan." : null));
            case 6: return askInt(6, "Available copies", "int | 0 to total copies", v[6],
                    n -> firstError(Registrable.checkRange(n, "Available copies", 0, 1000), stockError(n, v[5], self)));
            default: return NONE;
        }
    }

    private static LibraryBook buildBook(Object[] v) {
        return new LibraryBook((String) v[1], (String) v[2], (String) v[3], (String) v[4], (Integer) v[5], (Integer) v[6]);
    }

    private static Object[] bookValues(LibraryBook b) {
        return new Object[] {null, b.getBookId(), b.getIsbn(), b.getTitle(), b.getAuthor(), b.getTotalCopies(),
            b.getAvailableCopies()};
    }

    private static LibraryBook libraryBookForm(LibraryBook existing, String returnTo) {
        boolean updating = existing != null;
        Object[] v = updating ? bookValues(existing) : new Object[BOOK_LABELS.length];
        boolean review = updating;
        while (true) {
            if (!runForm("LIBRARY BOOK", BOOK_STEPS, SIX_FIELD_STEPS, BOOK_LABELS, v, null, updating, review,
                    f -> readBookField(f, v, existing))) {
                return formCancelled("Library book", existing, returnTo);
            }
            try {
                LibraryBook b = buildBook(v);
                b.validate();
                ensureIdAvailable(b.getBookId(), existing);
                ensureUnique("ISBN", isbnKey(b.getIsbn()), existing, books, book -> isbnKey(book.getIsbn()));
                String stock = stockError(b.getAvailableCopies(), b.getTotalCopies(), existing);
                if (stock != null) {
                    throw new ValidationException(stock);
                }
                if (updating) {
                    int decision = confirmChanges("Library book", existing, BOOK_LABELS, bookValues(existing), v);
                    if (decision == 2) {
                        continue;
                    }
                    if (decision == 0) {
                        return formCancelled("Library book", existing, returnTo);
                    }
                    existing.setBookId(b.getBookId());
                    existing.setIsbn(b.getIsbn());
                    existing.setTitle(b.getTitle());
                    existing.setAuthor(b.getAuthor());
                    existing.setTotalCopies(b.getTotalCopies());
                    existing.setAvailableCopies(b.getAvailableCopies());
                    updated("Library book", existing);
                    return existing;
                }
                books.add(b);
                if (!registeredSuccessfully(b, "Library book", returnTo)) {
                    return b;
                }
                Arrays.fill(v, null);
                review = false;
            } catch (ValidationException | DuplicateRecordException e) {
                saveFailed(e);
                review = true;
            }
        }
    }

    // borrow and return books
    private static final String[] LOAN_STEPS = {"Borrower", "Book & borrowed date", "Due & return", "Review & confirm"};
    private static final String[] LOAN_LABELS = labels(new String[] {"Loan ID", "Student", "Library book",
        "Borrowed date", "Due date", "Returned date"});
    private static final boolean[] LOAN_LOCKED_NEW = {false, false, false, false, false, false, true};
    private static final boolean[] LOAN_LOCKED_UPDATE = {false, false, true, true, true, false, true};

    private static BookLoan openLoan(Student student, LibraryBook book) {
        for (BookLoan loan : loans) {
            if (loan.getStudent() == student && loan.getBook() == book && loan.isActive()) {
                return loan;
            }
        }
        return null;
    }

    private static String loanBookError(LibraryBook book, Object student) {
        try {
            ensureBookAvailable(book);
        } catch (BookUnavailableException e) {
            return e.getMessage();
        }
        BookLoan open = student instanceof Student ? openLoan((Student) student, book) : null;
        if (open == null) {
            return null;
        }
        return open.getStudent().getDisplayName() + " already has \"" + book.getTitle() + "\" on loan ("
                + open.getId() + ").";
    }

    private static Object readLoanField(int f, Object[] v, BookLoan self) {
        switch (f) {
            case 1: return askText(1, "Loan ID", "String | unique | 4-30 chars", v[1],
                    s -> firstError(Identifiable.checkId(s, "Loan ID"), idError(s, self)));
            case 2: return askRecord(2, "Student", "Student", students, v[2], Main::studentLabel, null,
                    () -> studentForm(null, openForms.peek()), null);
            case 3: return askRecord(3, "Library book", "Library Book", books, v[3], Main::bookLabel,
                    b -> loanBookError(b, v[2]), () -> libraryBookForm(null, openForms.peek()), null);
            case 4: return askDate(4, "Borrowed date", "LocalDate | YYYY-MM-DD", v[4],
                    d -> Registrable.checkNotFuture(d, "Borrowed date"));
            case 5: return askDate(5, "Due date", "LocalDate | after borrowed date", v[5],
                    d -> after(d, v[4], "Due date", "the borrowed date"));
            case 6: {
                // not asked, a new loan is not returned yet
                System.out.println(String.format("%s %-20s[%s] : %s", two(6), "Returned date",
                        "LocalDate | set by Return Library Book", NOT_RETURNED));
                return v[6] == null ? NOT_RETURNED : v[6];
            }
            default: return NONE;
        }
    }

    private static BookLoan buildLoan(Object[] v) {
        if (v[6] instanceof LocalDate) {
            return new BookLoan((String) v[1], (Student) v[2], (LibraryBook) v[3], (LocalDate) v[4], (LocalDate) v[5],
                    (LocalDate) v[6]);
        }
        return new BookLoan((String) v[1], (Student) v[2], (LibraryBook) v[3], (LocalDate) v[4], (LocalDate) v[5]);
    }

    private static Object[] loanValues(BookLoan l) {
        return new Object[] {null, l.getLoanId(), l.getStudent(), l.getBook(), l.getBorrowedDate(), l.getDueDate(),
            l.getReturnedDate() == null ? NOT_RETURNED : l.getReturnedDate()};
    }

    private static BookLoan bookLoanForm(BookLoan existing, String returnTo) {
        boolean updating = existing != null;
        if (!updating) {
            if (!ensurePrerequisite(() -> !students.isEmpty(), missing("Student", "borrowing a book"),
                    "Student", returnTo, () -> studentForm(null, "book borrowing"))
                || !ensurePrerequisite(() -> !books.isEmpty(), missing("Library book", "borrowing a book"),
                    "Library Book", returnTo, () -> libraryBookForm(null, "book borrowing"))
                || !ensureSomeCopyAvailable()) {
                return formCancelled("Book loan", null, returnTo);
            }
        }
        Object[] v = updating ? loanValues(existing) : new Object[LOAN_LABELS.length];
        boolean review = updating;
        while (true) {
            if (!runForm("BOOK LOAN", LOAN_STEPS, SIX_FIELD_STEPS, LOAN_LABELS, v,
                    updating ? LOAN_LOCKED_UPDATE : LOAN_LOCKED_NEW, updating, review, f -> readLoanField(f, v, existing))) {
                return formCancelled("Book loan", existing, returnTo);
            }
            try {
                BookLoan loan = buildLoan(v);
                loan.validate();
                ensureIdAvailable(loan.getLoanId(), existing);
                ensureRegistered(loan.getStudent(), students, "Student");
                ensureRegistered(loan.getBook(), books, "Library book");
                if (updating) {
                    int decision = confirmChanges("Book loan", existing, LOAN_LABELS, loanValues(existing), v);
                    if (decision == 2) {
                        continue;
                    }
                    if (decision == 0) {
                        return formCancelled("Book loan", existing, returnTo);
                    }
                    existing.setLoanId(loan.getLoanId());
                    existing.setDueDate(loan.getDueDate());
                    updated("Book loan", existing);
                    return existing;
                }
                LibraryBook book = loan.getBook();
                ensureBookAvailable(book);
                BookLoan open = openLoan(loan.getStudent(), book);
                if (open != null) {
                    throw new DuplicateRecordException(loan.getStudent().getDisplayName() + " already has \""
                            + book.getTitle() + "\" on loan (" + open.getId() + ").");
                }
                // save the loan and take one copy from the stock
                int before = book.getAvailableCopies();
                loans.add(loan);
                book.setAvailableCopies(before - 1);
                printInfo("Stock of \"" + book.getTitle() + "\": " + before + " -> " + book.getAvailableCopies()
                        + " available copies.");
                if (!registeredSuccessfully(loan, "Book loan", returnTo)) {
                    return loan;
                }
                if (!ensureSomeCopyAvailable()) {
                    return loan;
                }
                Arrays.fill(v, null);
                review = false;
            } catch (ValidationException | DuplicateRecordException | PrerequisiteMissingException
                     | BookUnavailableException e) {
                saveFailed(e);
                review = true;
            }
        }
    }

    private static boolean ensureSomeCopyAvailable() {
        while (true) {
            try {
                for (LibraryBook book : books) {
                    if (book.getAvailableCopies() > 0) {
                        return true;
                    }
                }
                throw new BookUnavailableException("No library book has an available copy right now (all copies are on loan).");
            } catch (BookUnavailableException e) {
                printError(e.getMessage());
                System.out.println("  1. Register a new Library Book now");
                System.out.println("  2. Return to menu");
                if (readChoice("Select: ", 1, 2) != 1) {
                    return false;
                }
                libraryBookForm(null, "book borrowing");
            }
        }
    }

    private static void returnBook() throws PrerequisiteMissingException {
        List<BookLoan> open = new ArrayList<>();
        for (BookLoan l : loans) {
            if (l.isActive()) {
                open.add(l);
            }
        }
        checkPrerequisite(!open.isEmpty(), "No book is currently on loan. Borrow a book before returning one.");
        BookLoan loan = pickRecord("RETURN LIBRARY BOOK - CHOOSE LOAN", open, Main::loanLabel);
        if (loan == null) {
            printInfo("Return cancelled. Nothing was changed.");
            return;
        }
        loan.printDetails();
        LibraryBook book = loan.getBook();
        LocalDate returned = null;
        while (true) {
            if (returned == null) {
                Object in = askDate(1, "Returned date", "LocalDate | YYYY-MM-DD", null,
                        d -> firstError(Registrable.checkNotFuture(d, "Returned date"),
                                notBefore(d, loan.getBorrowedDate(), "Returned date", "the borrowed date")));
                if (in == BACK || in == CANCEL) {
                    printInfo("Return cancelled. Nothing was changed.");
                    return;
                }
                returned = (LocalDate) in;
            }
            long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), returned);
            boxTitle("REVIEW RETURN");
            boxLine("Loan          : " + Identifiable.labelOf(loan));
            boxLine("Borrowed/due  : " + Registrable.formatDate(loan.getBorrowedDate()) + " / "
                    + Registrable.formatDate(loan.getDueDate()));
            boxLine("Returned date : " + Registrable.formatDate(returned));
            boxLine("Status        : " + (daysLate > 0 ? "LATE by " + daysLate + " day(s)" : "on time"));
            boxLine("Stock change  : " + book.getAvailableCopies() + " -> " + (book.getAvailableCopies() + 1) + " available");
            System.out.println(LINE);
            System.out.println("  [1] Confirm return   [2] Edit returned date   [0] Cancel");
            int choice = readChoice("Select option: ", 0, 2);
            if (choice == 0) {
                printInfo("Return cancelled. Nothing was changed.");
                return;
            }
            if (choice == 2) {
                returned = null;
                continue;
            }
            try {
                BookLoan check = new BookLoan(loan.getLoanId(), loan.getStudent(), book, loan.getBorrowedDate(),
                        loan.getDueDate(), returned);
                check.validate();
                if (book.getAvailableCopies() + 1 > book.getTotalCopies()) {
                    throw new ValidationException("Stock error: available copies would exceed total copies.");
                }
                loan.setReturnedDate(returned); // update the same loan
                book.setAvailableCopies(book.getAvailableCopies() + 1); // one copy back
                printSuccess("Book returned. Loan " + loan.getId() + " closed on " + Registrable.formatDate(returned) + ".");
                printInfo("\"" + book.getTitle() + "\" now has " + book.getAvailableCopies() + "/" + book.getTotalCopies()
                        + " copies available.");
                return;
            } catch (ValidationException e) {
                printError(e.getMessage());
                returned = null;
            }
        }
    }

    // view, search and update

    private static <T extends Identifiable> T pickRecord(String title, List<T> items, Function<T, String> describe) {
        boxTitle(title);
        if (items.isEmpty()) {
            boxLine("No records to show.");
        }
        for (int i = 0; i < items.size(); i++) {
            boxLine((i + 1) + " " + describe.apply(items.get(i)));
        }
        boxLine("0 Back");
        System.out.println(LINE);
        int choice = readChoice("Select option: ", 0, items.size());
        return choice == 0 ? null : items.get(choice - 1);
    }

    private static void printDetailsOf(Identifiable record) {
        if (record instanceof Registrable) {
            ((Registrable) record).printDetails();
        } else {
            System.out.println(record);
        }
    }

    private static void printRecordTypes(String title, String option12) {
        boxTitle(title);
        for (int type = 1; type < TYPE_NAMES.length; type++) {
            boxLine(String.format("%2d %-16s (%d)", type, TYPE_NAMES[type], recordsOfType(type).size()));
        }
        boxLine("12 " + option12);
        boxLine(" 0 Back to main menu");
        System.out.println(LINE);
    }

    private static void viewAllRecords() {
        while (true) {
            printRecordTypes("VIEW ALL RECORDS", "Show every record");
            int type = readChoice("Select option: ", 0, 12);
            if (type == 0) {
                return;
            }
            if (type == 12) {
                if (allRecords().isEmpty()) {
                    printInfo("No records have been registered yet.");
                }
                for (int t = 1; t < TYPE_NAMES.length; t++) {
                    List<? extends Identifiable> list = recordsOfType(t);
                    if (!list.isEmpty()) {
                        System.out.println();
                        System.out.println("== " + TYPE_NAMES[t].toUpperCase() + " (" + list.size() + ") ==");
                        for (Identifiable record : list) {
                            System.out.println("  - " + record);
                        }
                    }
                }
                pause();
                continue;
            }
            List<? extends Identifiable> list = recordsOfType(type);
            if (list.isEmpty()) {
                printInfo("No " + TYPE_NAMES[type].toLowerCase() + " registered yet.");
                pause();
                continue;
            }
            while (true) {
                System.out.println();
                System.out.println("== " + TYPE_NAMES[type].toUpperCase() + " (" + list.size() + ") ==");
                for (int i = 0; i < list.size(); i++) {
                    System.out.printf("%3d. %s%n", i + 1, list.get(i));
                }
                int pick = readChoice("Enter a number to view full details (0 = back): ", 0, list.size());
                if (pick == 0) {
                    break;
                }
                printDetailsOf(list.get(pick - 1));
                pause();
            }
        }
    }

    private static Identifiable findById(String key) throws RecordNotFoundException {
        String id = key.trim();
        for (Identifiable record : allRecords()) {
            if (id.equalsIgnoreCase(record.getId())) {
                return record;
            }
        }
        // also try student number, course code, receipt, isbn ...
        for (Identifiable record : allRecords()) {
            if (matchesOtherKey(record, id)) {
                return record;
            }
        }
        throw new RecordNotFoundException("No record with ID '" + id + "' was found. Use option 13 to list all records.");
    }

    private static boolean matchesOtherKey(Identifiable record, String key) {
        if (record instanceof Student) {
            Student s = (Student) record;
            return key.equalsIgnoreCase(s.getStudentNumber()) || key.equalsIgnoreCase(s.getMemberNumber());
        }
        if (record instanceof Teacher) {
            Teacher t = (Teacher) record;
            return key.equalsIgnoreCase(t.getEmployeeNumber()) || key.equalsIgnoreCase(t.getMemberNumber());
        }
        if (record instanceof Administrator) {
            return key.equalsIgnoreCase(((Administrator) record).getStaffNumber());
        }
        if (record instanceof Department) {
            return key.equalsIgnoreCase(((Department) record).getDepartmentCode());
        }
        if (record instanceof Course) {
            return key.equalsIgnoreCase(((Course) record).getCourseCode());
        }
        if (record instanceof FeePayment) {
            return key.equalsIgnoreCase(((FeePayment) record).getReceiptNumber());
        }
        if (record instanceof LibraryBook) {
            return isbnKey(key).equalsIgnoreCase(isbnKey(((LibraryBook) record).getIsbn()));
        }
        return false;
    }

    private static void searchById() {
        boxTitle("SEARCH RECORD BY ID");
        boxLine("Enter any record ID (student/employee numbers, course");
        boxLine("codes, receipt numbers and ISBNs are also accepted).");
        System.out.println(LINE);
        while (true) {
            String id = ask("Record ID (0 = back to menu): ");
            if (id.equals("0")) {
                return;
            }
            if (id.isEmpty()) {
                printError("Please type an ID, or 0 to go back.");
                continue;
            }
            try {
                Identifiable record = findById(id);
                printSuccess("Found " + record.getClass().getSimpleName() + " " + Identifiable.labelOf(record));
                printDetailsOf(record);
            } catch (RecordNotFoundException e) {
                printError(e.getMessage());
            }
        }
    }

    private static void updateRecord() throws RecordNotFoundException {
        printRecordTypes("UPDATE EXISTING RECORD", "Find the record by its ID");
        int type = readChoice("Select option: ", 0, 12);
        if (type == 0) {
            return;
        }
        Identifiable chosen;
        if (type == 12) {
            chosen = findById(ask("Record ID: "));
        } else {
            List<? extends Identifiable> list = recordsOfType(type);
            if (list.isEmpty()) {
                printInfo("No " + TYPE_NAMES[type].toLowerCase() + " to update yet. Register one first.");
                return;
            }
            chosen = pickRecord("UPDATE - CHOOSE FROM " + TYPE_NAMES[type].toUpperCase(), list, Identifiable::labelOf);
            if (chosen == null) {
                return;
            }
        }
        // same form as registration but in update mode
        if (chosen instanceof Department) {
            departmentForm((Department) chosen, null);
        } else if (chosen instanceof Student) {
            studentForm((Student) chosen, null);
        } else if (chosen instanceof Teacher) {
            teacherForm((Teacher) chosen, null);
        } else if (chosen instanceof Administrator) {
            administratorForm((Administrator) chosen, null);
        } else if (chosen instanceof Course) {
            courseForm((Course) chosen, null);
        } else if (chosen instanceof Classroom) {
            classroomForm((Classroom) chosen, null);
        } else if (chosen instanceof Enrollment) {
            enrollmentForm((Enrollment) chosen, null);
        } else if (chosen instanceof Assessment) {
            assessmentForm((Assessment) chosen, null);
        } else if (chosen instanceof FeePayment) {
            feePaymentForm((FeePayment) chosen, null);
        } else if (chosen instanceof LibraryBook) {
            libraryBookForm((LibraryBook) chosen, null);
        } else if (chosen instanceof BookLoan) {
            bookLoanForm((BookLoan) chosen, null);
        }
    }

    // reports

    private static void showAcademicSummary() throws PrerequisiteMissingException {
        checkPrerequisite(!students.isEmpty(), missing("Student", "viewing an academic summary"));
        Student student = pickRecord("ACADEMIC SUMMARY - CHOOSE STUDENT", students, Main::studentLabel);
        if (student == null) {
            return;
        }
        boxTitle("STUDENT ACADEMIC SUMMARY");
        boxLine("Student     : " + Identifiable.labelOf(student) + " (" + student.getStudentNumber() + ")");
        boxLine("Program     : " + student.getProgramName() + ", year " + student.getStudyYear());
        boxLine("Department  : " + Identifiable.labelOf(student.getDepartment()));
        boxLine("Status      : " + student.getAcademicStatus() + "   Scholarship: "
                + (student.isScholarshipStatus() ? "YES" : "NO"));
        boxLine("Category    : " + student.getAcademicCategory());
        System.out.println(LINE);

        double totalObtained = 0;
        double totalMaximum = 0;
        int activeCredits = 0;
        List<Enrollment> mine = new ArrayList<>();
        for (Enrollment e : enrollments) {
            if (e.getStudent() == student) {
                mine.add(e);
            }
        }
        if (mine.isEmpty()) {
            System.out.println("  No enrollments yet.");
        }
        for (Enrollment e : mine) {
            Course c = e.getCourse();
            System.out.println("  " + Identifiable.labelOf(c) + " | " + c.getCreditHours() + " credits | "
                    + e.getSemester() + " | " + e.getEnrollmentStatus() + " | enrollment " + e.getId());
            if (e.isActive()) {
                activeCredits += c.getCreditHours();
            }
            double obtained = 0;
            double maximum = 0;
            for (Assessment a : assessments) {
                if (a.getEnrollment() == e) {
                    System.out.printf("      - %-11s %s : %s / %s%n", a.getAssessmentType(),
                            Registrable.formatDate(a.getAssessmentDate()), display(a.getObtainedScore()),
                            display(a.getMaximumScore()));
                    obtained += a.getObtainedScore();
                    maximum += a.getMaximumScore();
                }
            }
            if (maximum == 0) {
                System.out.println("      No assessments recorded yet.");
            } else {
                System.out.printf("      Course result: %s / %s = %.1f%%%n", display(obtained), display(maximum),
                        obtained * 100 / maximum);
            }
            totalObtained += obtained;
            totalMaximum += maximum;
        }
        System.out.println(LINE);
        System.out.println("  Active credit hours : " + activeCredits);
        System.out.println("  Overall average     : "
                + (totalMaximum == 0 ? "n/a" : String.format("%.1f%%", totalObtained * 100 / totalMaximum)));
        int booksOut = 0;
        for (BookLoan l : loans) {
            if (l.getStudent() == student && l.isActive()) {
                booksOut++;
            }
        }
        BigDecimal paid = BigDecimal.ZERO;
        for (FeePayment p : payments) {
            if (p.getStudent() == student) {
                paid = paid.add(p.getAmount());
            }
        }
        System.out.println("  Books on loan       : " + booksOut);
        System.out.println("  Total fees paid     : " + Registrable.formatMoney(paid));
        pause();
    }

    private static void showPaymentHistory() throws PrerequisiteMissingException {
        checkPrerequisite(!students.isEmpty(), missing("Student", "viewing a payment history"));
        Student student = pickRecord("PAYMENT HISTORY - CHOOSE STUDENT", students, Main::studentLabel);
        if (student == null) {
            return;
        }
        List<FeePayment> history = new ArrayList<>();
        for (FeePayment p : payments) {
            if (p.getStudent() == student) {
                history.add(p);
            }
        }
        history.sort((a, b) -> a.getPaymentDate().compareTo(b.getPaymentDate()));
        boxTitle("PAYMENT HISTORY");
        boxLine(Identifiable.labelOf(student) + " (" + student.getStudentNumber() + ")");
        System.out.println(LINE);
        if (history.isEmpty()) {
            System.out.println("  No payments recorded for this student yet.");
        } else {
            System.out.printf("  %-10s  %-10s  %-14s  %-13s  %18s%n", "Date", "Payment", "Receipt", "Method", "Amount");
            BigDecimal total = BigDecimal.ZERO;
            for (FeePayment p : history) {
                System.out.printf("  %-10s  %-10s  %-14s  %-13s  %18s%n", Registrable.formatDate(p.getPaymentDate()),
                        p.getPaymentId(), p.getReceiptNumber(), p.getPaymentMethod(), Registrable.formatMoney(p.getAmount()));
                total = total.add(p.getAmount());
            }
            System.out.println(LINE);
            System.out.println("  " + history.size() + " payment(s), total paid: " + Registrable.formatMoney(total));
        }
        pause();
    }

    private static void showAvailableBooks() {
        List<LibraryBook> available = new ArrayList<>();
        for (LibraryBook b : books) {
            if (b.isActive()) {
                available.add(b);
            }
        }
        boxTitle("AVAILABLE LIBRARY BOOKS");
        if (books.isEmpty()) {
            boxLine("No library book is registered yet (use option 10).");
        } else if (available.isEmpty()) {
            boxLine("No copies are available right now - all are on loan.");
        }
        System.out.println(LINE);
        for (LibraryBook book : available) {
            System.out.println("  " + Identifiable.labelOf(book) + " | ISBN " + book.getIsbn() + " | "
                    + book.getAvailableCopies() + "/" + book.getTotalCopies() + " available");
        }
        pause();
    }

    // polymorphism demo

    private static String inheritanceChain(Object object) {
        StringBuilder chain = new StringBuilder(object.getClass().getSimpleName());
        for (Class<?> type = object.getClass().getSuperclass(); type != null; type = type.getSuperclass()) {
            chain.append(" -> ").append(type.getSimpleName());
        }
        return chain.toString();
    }

    private static void demonstratePolymorphism() {
        boxTitle("DEMONSTRATE POLYMORPHISM");

        // 1. list of Person (superclass reference)
        List<Person> people = allPeople();
        System.out.println("1) List<Person> - same method call, overridden behaviour chosen at runtime");
        if (people.isEmpty()) {
            System.out.println("   (No Student, Teacher or Administrator yet - register some to see this part.)");
        }
        for (Person person : people) {
            System.out.println("   Person reference -> real object: " + person.getClass().getSimpleName()
                    + " " + Identifiable.labelOf(person));
            System.out.println("      getRoleDescription() : " + person.getRoleDescription());
            System.out.println("      isActive()           : " + person.isActive());
            if (person instanceof AcademicMember) {
                System.out.println("      getAcademicCategory(): " + ((AcademicMember) person).getAcademicCategory());
            }
            System.out.println("      inheritance          : " + inheritanceChain(person));
        }

        // 2. list of Identifiable (interface reference)
        List<Identifiable> entities = new ArrayList<>();
        entities.addAll(departments);
        entities.addAll(courses);
        entities.addAll(classrooms);
        entities.addAll(enrollments);
        entities.addAll(assessments);
        entities.addAll(payments);
        entities.addAll(books);
        entities.addAll(loans);
        System.out.println();
        System.out.println("2) List<Identifiable> - unrelated classes behind one interface");
        if (entities.isEmpty()) {
            System.out.println("   (No department, course, classroom, enrollment, assessment, payment, book or loan yet.)");
        }
        for (Identifiable entity : entities) {
            System.out.printf("   %-12s getId()=%-10s isActive()=%-5s getDisplayName()=%s%n",
                    entity.getClass().getSimpleName(), entity.getId(), entity.isActive(), entity.getDisplayName());
        }

        // 3. same records as Registrable
        System.out.println();
        System.out.println("3) Registrable references - getRegistrationSummary()");
        for (Identifiable record : allRecords()) {
            if (record instanceof Registrable) {
                System.out.println("   " + ((Registrable) record).getRegistrationSummary());
            }
        }

        System.out.println();
        System.out.println("4) Inheritance used in this project");
        System.out.println("   Multilevel  : Person -> AcademicMember -> Student, Person -> AcademicMember -> Teacher");
        System.out.println("   Hierarchical: Student and Teacher share AcademicMember; AcademicMember and");
        System.out.println("                 Administrator share Person.");
        System.out.println("   Constructors: Student(...) calls super(...) in AcademicMember, which calls super(...)");
        System.out.println("                 in Person (constructor chaining). Department and BookLoan also use this(...).");
        System.out.println();
        System.out.println("5) Why Person and AcademicMember cannot be instantiated");
        System.out.println("   Both are declared abstract, so 'new Person(...)' does not compile. They describe");
        System.out.println("   shared state, and they declare abstract methods (getRoleDescription(),");
        System.out.println("   getAcademicCategory()) without a body. A real person in the school is always a");
        System.out.println("   Student, Teacher or Administrator, which implement those methods.");
        pause();
    }

    // sample data for the demo (AUCA faculties, real INSY/ACCT course codes and 2025-2026 IT fees)
    // the people, phone numbers and emails are made up
    private static void loadSampleData() {
        // departments (the head teachers are set after the teachers are created)
        Department seng = new Department("DEP-SENG", "Software Engineering", "SENG",
                "Gishushu Campus, Faculty of IT", "seng@auca.ac.rw");
        Department im = new Department("DEP-IM", "Information Management", "IM",
                "Gishushu Campus, Faculty of IT", "im@auca.ac.rw");
        Department ncs = new Department("DEP-NCS", "Networks and Communication Systems", "NCS",
                "Gishushu Campus, Faculty of IT", "ncs@auca.ac.rw");
        Department acct = new Department("DEP-ACCT", "Accounting", "ACCT",
                "Masoro Campus, Faculty of Business", "accounting@auca.ac.rw");

        // teachers
        Teacher jean = new Teacher("PER-1001", "Jean", "Habimana", "jean.habimana@example.com", "0788100101",
                date("1984-03-20"), "MEM-1001", seng, date("2015-01-12"), "ACTIVE", "jhabimana@auca.ac.rw",
                "Dr. Emmanuel Kayitare", "EMP-1001", "Object-Oriented Programming", date("2015-01-12"),
                new BigDecimal("1350000.00"), "G-12", "FULL_TIME");
        Teacher samuel = new Teacher("PER-1002", "Samuel", "Ndayisaba", "samuel.ndayisaba@example.com", "0788100102",
                date("1987-06-11"), "MEM-1002", ncs, date("2017-09-04"), "ACTIVE", "sndayisaba@auca.ac.rw",
                "Dr. Emmanuel Kayitare", "EMP-1002", "Computer Networks", date("2017-09-04"),
                new BigDecimal("1200000.00"), "G-07", "FULL_TIME");
        Teacher josiane = new Teacher("PER-1003", "Josiane", "Uwamahoro", "josiane.uwamahoro@example.com", "0788100103",
                date("1990-02-27"), "MEM-1003", im, date("2020-02-03"), "ACTIVE", "juwamahoro@auca.ac.rw",
                "Jean Habimana", "EMP-1003", "Database Systems", date("2020-02-03"),
                new BigDecimal("900000.00"), "G-15", "PART_TIME");
        Teacher claudine = new Teacher("PER-1004", "Claudine", "Mukamana", "claudine.mukamana@example.com", "0788100104",
                date("1985-08-02"), "MEM-1004", acct, date("2018-08-20"), "ACTIVE", "cmukamana@auca.ac.rw",
                "Dr. Alice Kayitesi", "EMP-1004", "Financial Accounting", date("2018-08-20"),
                new BigDecimal("1100000.00"), "M-210", "FULL_TIME");
        seng.setHeadTeacher(jean);
        ncs.setHeadTeacher(samuel);
        im.setHeadTeacher(josiane);
        acct.setHeadTeacher(claudine);

        // students (student numbers start with the year of admission)
        Student alice = new Student("PER-2001", "Alice", "Uwase", "alice.uwase@example.com", "0788200201",
                date("2004-05-16"), "MEM-25101", seng, date("2025-09-01"), "ACTIVE", "alice.uwase@auca.ac.rw",
                "Jean Habimana", "25101", "BSc in Software Engineering", date("2025-09-01"), 2, "0788300301", true);
        Student eric = new Student("PER-2002", "Eric", "Nkurunziza", "eric.nkurunziza@example.com", "0788200202",
                date("2006-01-23"), "MEM-26114", ncs, date("2026-09-01"), "ACTIVE", "eric.nkurunziza@auca.ac.rw",
                "Samuel Ndayisaba", "26114", "BSc in Network and Communication Systems", date("2026-09-01"), 1,
                "0788300302", false);
        Student grace = new Student("PER-2003", "Grace", "Ingabire", "grace.ingabire@example.com", "0788200203",
                date("2003-11-08"), "MEM-24087", acct, date("2024-09-02"), "ACTIVE", "grace.ingabire@auca.ac.rw",
                "Claudine Mukamana", "24087", "BBA in Accounting", date("2024-09-02"), 3, "0788300303", false);
        Student kevin = new Student("PER-2004", "Kevin", "Mugisha", "kevin.mugisha@example.com", "0788200204",
                date("2005-07-30"), "MEM-26052", im, date("2026-01-12"), "INACTIVE", "kevin.mugisha@auca.ac.rw",
                "Josiane Uwamahoro", "26052", "BSc in Information Management", date("2026-01-12"), 1,
                "0788300304", false);
        Student divine = new Student("PER-2005", "Divine", "Iradukunda", "divine.iradukunda@example.com", "0788200205",
                date("2003-02-14"), "MEM-24093", seng, date("2024-09-02"), "ACTIVE", "divine.iradukunda@auca.ac.rw",
                "Jean Habimana", "24093", "BSc in Software Engineering", date("2024-09-02"), 3, "0788300305", true);

        // administrators
        Administrator diane = new Administrator("PER-3001", "Diane", "Uwimana", "diane.uwimana@example.com",
                "0788400401", date("1983-04-14"), "STF-3001", "Registrar", "Registrar's Office, Masoro Campus",
                date("2012-06-01"), 5, true);
        Administrator patrick = new Administrator("PER-3002", "Patrick", "Niyonzima", "patrick.niyonzima@example.com",
                "0788400402", date("1986-12-30"), "STF-3002", "Finance Officer", "Finance Office, Masoro Campus",
                date("2016-03-15"), 4, true);
        Administrator esther = new Administrator("PER-3003", "Esther", "Mukeshimana", "esther.mukeshimana@example.com",
                "0788400403", date("1992-09-09"), "STF-3003", "Librarian", "Library, Gishushu Campus",
                date("2019-01-07"), 2, true);

        // courses (codes and credits from the AUCA academic bulletin)
        Course oop = new Course("CRS-INSY230", "INSY 230", "Object-Oriented Programming", 4, seng, jean);
        Course java = new Course("CRS-INSY324", "INSY 324", "Java Programming", 4, seng, jean);
        Course dbms = new Course("CRS-INSY217", "INSY 217", "Database Management Systems", 3, im, josiane);
        Course networks = new Course("CRS-INSY229", "INSY 229", "Computer Networks", 4, ncs, samuel);
        Course accounting = new Course("CRS-ACCT112", "ACCT 112", "Principles of Accounting I", 3, acct, claudine);

        // classrooms
        Classroom g101 = new Classroom("CLS-G101", "Gishushu Campus", "G101", 45, 1, true);
        Classroom lab2 = new Classroom("CLS-GLAB2", "Gishushu Campus", "LAB-2", 35, 0, true);
        Classroom m204 = new Classroom("CLS-M204", "Masoro Campus", "M204", 80, 2, true);
        Classroom m012 = new Classroom("CLS-M012", "Masoro Campus", "M012", 120, 0, false);

        // enrollments for Semester I 2026-2027 (Eric dropped OOP, so it can not get assessments)
        Enrollment e1 = new Enrollment("ENR-0001", alice, oop, date("2026-09-07"), "SEMESTER_1", "ACTIVE");
        Enrollment e2 = new Enrollment("ENR-0002", alice, dbms, date("2026-09-07"), "SEMESTER_1", "ACTIVE");
        Enrollment e3 = new Enrollment("ENR-0003", eric, networks, date("2026-09-08"), "SEMESTER_1", "ACTIVE");
        Enrollment e4 = new Enrollment("ENR-0004", eric, oop, date("2026-09-08"), "SEMESTER_1", "DROPPED");
        Enrollment e5 = new Enrollment("ENR-0005", grace, accounting, date("2026-09-07"), "SEMESTER_1", "ACTIVE");
        Enrollment e6 = new Enrollment("ENR-0006", divine, java, date("2026-09-07"), "SEMESTER_1", "ACTIVE");

        // assessments (AUCA marks out of 20, pass mark 10/20)
        Assessment a1 = new Assessment("ASM-0001", e1, "QUIZ", 10, 8.5, date("2026-09-25"));
        Assessment a2 = new Assessment("ASM-0002", e1, "MIDTERM", 20, 15, date("2026-10-23"));
        Assessment a3 = new Assessment("ASM-0003", e2, "ASSIGNMENT", 20, 17, date("2026-10-09"));
        Assessment a4 = new Assessment("ASM-0004", e3, "QUIZ", 10, 6, date("2026-09-30"));
        Assessment a5 = new Assessment("ASM-0005", e3, "MIDTERM", 20, 9.5, date("2026-10-22"));
        Assessment a6 = new Assessment("ASM-0006", e5, "MIDTERM", 20, 16, date("2026-10-21"));
        Assessment a7 = new Assessment("ASM-0007", e6, "PROJECT", 20, 18, date("2026-11-06"));

        // fee payments (IT semester fee 2025-2026: 385,326 tuition + 25,000 registration = 410,326 RWF)
        FeePayment p1 = new FeePayment("PAY-0001", alice, new BigDecimal("410326.00"), date("2026-09-02"),
                "MOBILE_MONEY", "RCP-2026-0001");
        FeePayment p2 = new FeePayment("PAY-0002", eric, new BigDecimal("45000.00"), date("2026-09-01"),
                "CASH", "RCP-2026-0002");
        FeePayment p3 = new FeePayment("PAY-0003", eric, new BigDecimal("205163.00"), date("2026-09-03"),
                "BANK_TRANSFER", "RCP-2026-0003");
        FeePayment p4 = new FeePayment("PAY-0004", divine, new BigDecimal("410326.00"), date("2026-09-04"),
                "BANK_TRANSFER", "RCP-2026-0004");
        FeePayment p5 = new FeePayment("PAY-0005", grace, new BigDecimal("300000.00"), date("2026-09-05"),
                "CARD", "RCP-2026-0005");
        FeePayment p6 = new FeePayment("PAY-0006", eric, new BigDecimal("205163.00"), date("2026-10-15"),
                "MOBILE_MONEY", "RCP-2026-0006");

        // library books (real textbooks and ISBNs), all copies on the shelf at first
        LibraryBook deitel = new LibraryBook("BK-0001", "978-0134743356", "Java How to Program, Early Objects",
                "Paul Deitel", 3, 3);
        LibraryBook headFirst = new LibraryBook("BK-0002", "978-1491910771", "Head First Java", "Kathy Sierra", 2, 2);
        LibraryBook dbConcepts = new LibraryBook("BK-0003", "978-0078022159", "Database System Concepts",
                "Abraham Silberschatz", 1, 1);
        LibraryBook kurose = new LibraryBook("BK-0004", "978-0136681557", "Computer Networking: A Top-Down Approach",
                "James Kurose", 2, 2);
        LibraryBook cleanCode = new LibraryBook("BK-0005", "978-0132350884", "Clean Code", "Robert C. Martin", 1, 1);

        // book loans: three still open, one already returned
        BookLoan l1 = new BookLoan("LN-0001", alice, dbConcepts, date("2026-11-02"), date("2026-11-16"));
        BookLoan l2 = new BookLoan("LN-0002", eric, kurose, date("2026-11-09"), date("2026-11-23"));
        BookLoan l3 = new BookLoan("LN-0003", divine, headFirst, date("2026-10-05"), date("2026-10-19"),
                date("2026-10-17"));
        BookLoan l4 = new BookLoan("LN-0004", alice, deitel, date("2026-11-10"), date("2026-11-24"));

        departments.addAll(Arrays.asList(seng, im, ncs, acct));
        teachers.addAll(Arrays.asList(jean, samuel, josiane, claudine));
        students.addAll(Arrays.asList(alice, eric, grace, kevin, divine));
        administrators.addAll(Arrays.asList(diane, patrick, esther));
        courses.addAll(Arrays.asList(oop, java, dbms, networks, accounting));
        classrooms.addAll(Arrays.asList(g101, lab2, m204, m012));
        enrollments.addAll(Arrays.asList(e1, e2, e3, e4, e5, e6));
        assessments.addAll(Arrays.asList(a1, a2, a3, a4, a5, a6, a7));
        payments.addAll(Arrays.asList(p1, p2, p3, p4, p5, p6));
        books.addAll(Arrays.asList(deitel, headFirst, dbConcepts, kurose, cleanCode));
        loans.addAll(Arrays.asList(l1, l2, l3, l4));

        // same as borrowing: one copy leaves the shelf for every open loan
        for (BookLoan loan : loans) {
            if (loan.isActive()) {
                loan.getBook().setAvailableCopies(loan.getBook().getAvailableCopies() - 1);
            }
        }

        // check every sample record with validate(), like a normal registration
        try {
            for (Identifiable record : allRecords()) {
                ((Registrable) record).validate();
            }
        } catch (ValidationException e) {
            clearAllRecords();
            printError("Sample data was not loaded: " + e.getMessage());
            return;
        }
    }

    private static LocalDate date(String text) {
        return Registrable.parseDate(text);
    }

    private static void clearAllRecords() {
        departments.clear();
        students.clear();
        teachers.clear();
        administrators.clear();
        courses.clear();
        classrooms.clear();
        enrollments.clear();
        assessments.clear();
        payments.clear();
        books.clear();
        loans.clear();
    }
}
