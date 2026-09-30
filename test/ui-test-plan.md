# UI test plan

Tests the Eric command-line UI end to end. Run with the `test-ui` skill
(`.claude/skills/test-ui/SKILL.md`), which executes `test/run-ui-tests.py`.

## How the tests work

- Each test case starts a fresh `eric.Eric` process and feeds it the **Input** lines on standard input.
- The startup banner and greeting are stripped from the output before comparing, so **Expected output** starts right after the banner.
- The comparison is exact, line by line, ignoring trailing whitespace.
- The session stops at the first failing test case and reports the actual and expected output.
- Every input block ends with `bye` so the program exits normally.
- To add a test case, append a section with the same shape: `## TCn: title`, `**Aim:**`, `**Input:**` code block, `**Expected output:**` code block.

## TC1: Add a todo

**Aim:** Check that `todo` adds a Todo task and reports the new task count.

**Input:**
```
todo read book
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC2: Add a deadline

**Aim:** Check that `deadline` adds a Deadline showing its `/by` date.

**Input:**
```
deadline return book /by Sunday
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC3: Add an event

**Aim:** Check that `event` adds an Event showing its `/from` and `/to` times.

**Input:**
```
event meeting /from Mon 2pm /to 4pm
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [E][ ] meeting (from: Mon 2pm to: 4pm)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC4: List an empty list

**Aim:** Check that `list` with no tasks prints an empty list without errors.

**Input:**
```
list
bye
```

**Expected output:**
```
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC5: List mixed task types

**Aim:** Check that `list` shows all task types, numbered, in insertion order (polymorphism).

**Input:**
```
todo read book
deadline return book /by Sunday
event meeting /from Mon 2pm /to 4pm
list
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] meeting (from: Mon 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Sunday)
 3.[E][ ] meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC6: Mark and unmark a task

**Aim:** Check that `mark` and `unmark` change a task's done status.

**Input:**
```
todo read book
mark 1
unmark 1
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC7: Invalid task numbers

**Aim:** Check that `mark` rejects non-numeric, zero and out-of-range task numbers and says what is valid.

**Input:**
```
todo read book
mark 9
mark abc
mark 0
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 9 doesn't exist.
 Choose a number from 1 to 1. Type list to see the tasks.
____________________________________________________________
____________________________________________________________
 OOPS!!! "abc" is not a valid task number.
 Use a whole number, e.g. mark 2. Type list to see the task numbers.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 0 doesn't exist.
 Choose a number from 1 to 1. Type list to see the tasks.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC8: Malformed task commands

**Aim:** Check that an empty `todo` and incomplete `deadline` and `event` commands print an error and add nothing.

**Input:**
```
todo
deadline return book
event meeting /from Mon 2pm
list
bye
```

**Expected output:**
```
____________________________________________________________
 OOPS!!! The description of a todo is empty.
 Type a description after "todo", e.g. todo read book
____________________________________________________________
____________________________________________________________
 OOPS!!! A deadline needs a /by date, but I couldn't find one.
 Use the format: deadline DESCRIPTION /by DATE, e.g. deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
 OOPS!!! An event needs a /to time, but I couldn't find one.
 Use the format: event DESCRIPTION /from START /to END, e.g. event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC9: Unknown command

**Aim:** Check that an unrecognised command is named in the error and the valid commands are listed.

**Input:**
```
blah
bye
```

**Expected output:**
```
____________________________________________________________
 OOPS!!! I don't know the command "blah".
 Available commands: todo, deadline, event, list, mark, unmark, bye.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC10: Missing task number

**Aim:** Check that `mark` and `unmark` without a number explain what to type.

**Input:**
```
mark
unmark
bye
```

**Expected output:**
```
____________________________________________________________
 OOPS!!! The task number is missing.
 Type the number of a task after "mark", e.g. mark 2
____________________________________________________________
____________________________________________________________
 OOPS!!! The task number is missing.
 Type the number of a task after "unmark", e.g. unmark 2
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC11: Mark with no tasks

**Aim:** Check that marking when the list is empty says there is nothing to mark.

**Input:**
```
mark 1
unmark 1
bye
```

**Expected output:**
```
____________________________________________________________
 OOPS!!! There are no tasks to mark yet.
 Add a task first, e.g. todo read book
____________________________________________________________
____________________________________________________________
 OOPS!!! There are no tasks to unmark yet.
 Add a task first, e.g. todo read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC12: Deadline errors

**Aim:** Check each way a `deadline` can be invalid gets its own specific message.

**Input:**
```
deadline
deadline /by Sunday
deadline return book /by
bye
```

**Expected output:**
```
____________________________________________________________
 OOPS!!! A deadline needs a /by date, but I couldn't find one.
 Use the format: deadline DESCRIPTION /by DATE, e.g. deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
 OOPS!!! The description of a deadline is empty.
 Type a description before /by, e.g. deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
 OOPS!!! The date after /by is empty.
 Type when the task is due after /by, e.g. deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC13: Event errors

**Aim:** Check each way an `event` can be invalid gets its own specific message.

**Input:**
```
event
event meeting /to 4pm
event meeting /to 4pm /from Mon 2pm
event /from Mon 2pm /to 4pm
event meeting /from /to 4pm
event meeting /from Mon 2pm /to
bye
```

**Expected output:**
```
____________________________________________________________
 OOPS!!! An event needs a /from time and a /to time, but I couldn't find either.
 Use the format: event DESCRIPTION /from START /to END, e.g. event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
 OOPS!!! An event needs a /from time, but I couldn't find one.
 Use the format: event DESCRIPTION /from START /to END, e.g. event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
 OOPS!!! /to comes before /from.
 Put /from first, then /to. Use the format: event DESCRIPTION /from START /to END, e.g. event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
 OOPS!!! The description of an event is empty.
 Type a description before /from, e.g. event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
 OOPS!!! The start time after /from is empty.
 Type when the event starts after /from, e.g. event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
 OOPS!!! The end time after /to is empty.
 Type when the event ends after /to, e.g. event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC14: Blank and padded input

**Aim:** Check that a blank line is reported and that spaces around a command are ignored.

**Input:**
```

   list  
bye
```

**Expected output:**
```
____________________________________________________________
 OOPS!!! You didn't type a command.
 Available commands: todo, deadline, event, list, mark, unmark, bye.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC15: Keeps running after errors

**Aim:** Check that the chatbot stays up after errors and still accepts valid commands.

**Input:**
```
todo
blah
mark 3
todo read book
list
bye
```

**Expected output:**
```
____________________________________________________________
 OOPS!!! The description of a todo is empty.
 Type a description after "todo", e.g. todo read book
____________________________________________________________
____________________________________________________________
 OOPS!!! I don't know the command "blah".
 Available commands: todo, deadline, event, list, mark, unmark, bye.
____________________________________________________________
____________________________________________________________
 OOPS!!! There are no tasks to mark yet.
 Add a task first, e.g. todo read book
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
