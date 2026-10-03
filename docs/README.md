# Eric User Guide

Eric is a command-line chatbot that keeps track of your tasks. Type a command and press Enter; type `bye` to exit.

## Adding a todo

Adds a task with no date.

Format: `todo DESCRIPTION`

Example: `todo read book`

```
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
```

## Adding a deadline

Adds a task that must be done by a given time.

Format: `deadline DESCRIPTION /by DATE`

Example: `deadline return book /by Sunday`

```
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 2 tasks in the list.
```

## Adding an event

Adds a task that spans a time range.

Format: `event DESCRIPTION /from START /to END`

Example: `event project meeting /from Mon 2pm /to 4pm`

```
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 3 tasks in the list.
```

## Listing tasks

Format: `list`

## Marking and unmarking tasks

Marks a task as done, or as not done yet, using its number in the list.

Format: `mark INDEX` / `unmark INDEX`

Example: `mark 1`

```
 Nice! I've marked this task as done:
   [T][X] read book
```

## Finding tasks

Shows the tasks whose description contains a keyword. Upper and lower case are treated as the same, only the description is searched (not the dates), and everything after `find` is the keyword, so it can have several words. The results are numbered from 1 in the order of the results, not by their number in the full list.

Format: `find KEYWORD`

Example: `find book`

```
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: June 6th)
```

If nothing matches, Eric says so.

## Deleting a task

Removes a task from the list, using its number in the list. The remaining tasks are renumbered.

Format: `delete INDEX`

Example: `delete 3`

```
 Noted. I've removed this task:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 Now you have 4 tasks in the list.
```

## Saving and loading

Eric saves your tasks to `data/duke.txt` (next to where you run Eric) every time the list changes, and loads them again when it starts. The `data` folder and file are created automatically, so nothing is needed on a first run.

Each line is one task: `T | 1 | read book` (todo), `D | 0 | return book | June 6th` (deadline) or `E | 0 | project meeting | Mon 2pm | 4pm` (event). The second column is `1` for done and `0` for not done.

Task text cannot contain a `|` with spaces on both sides (` | `), because that separates the columns. A `|` without spaces, such as `a|b`, is fine.

If the file cannot be used, for example it has a line in the wrong format, is not text, or is a folder, Eric does not load any of it. It tells you why (for a wrong format, every bad line and its reason), and starts with an empty list. The file is replaced the next time your tasks change, so to keep it, close Eric and fix or move the file first.

## Exiting

Format: `bye`
