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

**Aim:** Check that `mark` rejects out-of-range and non-numeric task numbers.

**Input:**
```
todo read book
mark 9
mark abc
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
 OOPS!!! I couldn't find task 9.
____________________________________________________________
____________________________________________________________
 OOPS!!! That doesn't look like a valid task number.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC8: Malformed task commands

**Aim:** Check that incomplete `todo`, `deadline` and `event` commands print an error and add nothing.

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
 OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
 OOPS!!! A deadline needs a description and a /by date, e.g. deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
 OOPS!!! An event needs a description, a /from time, and a /to time, e.g. event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC9: Unknown command

**Aim:** Check that an unrecognised command prints the "don't know" message.

**Input:**
```
blah
bye
```

**Expected output:**
```
____________________________________________________________
 OOPS!!! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
