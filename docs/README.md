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

## Exiting

Format: `bye`
