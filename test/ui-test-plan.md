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
 Use a plain whole number (no + sign or leading zeros), e.g. mark 2. Type list to see the task numbers.
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

## TC16: Todo spacing and markers

**Aim:** Check that a todo keeps inner spaces, trims the ends and treats `/by` as plain text.

**Input:**
```
todo   read   book  
todo buy /by milk
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read   book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] buy /by milk
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC17: Deadline examples

**Aim:** Check deadlines with free-text, multi-word and slash-containing dates, and repeated `/by`.

**Input:**
```
deadline do homework /by no idea :-p
deadline submit report /by 11/10/2019 5pm
deadline a /by b /by c
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [D][ ] do homework (by: no idea :-p)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] submit report (by: 11/10/2019 5pm)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] a (by: b /by c)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC18: Event examples

**Aim:** Check events with date ranges and a description containing a marker look-alike.

**Input:**
```
event team project meeting /from 2/10/2019 2pm /to 4pm
event orientation week /from 4/10/2019 /to 11/10/2019
event a /tomorrow /from 1 /to 2
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [E][ ] team project meeting (from: 2/10/2019 2pm to: 4pm)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] a /tomorrow (from: 1 to: 2)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC19: Extra spaces inside commands

**Aim:** Check that extra spaces around and between command parts are ignored.

**Input:**
```
  todo   read book  
deadline  return book   /by   Sunday  
event  meeting  /from  Mon 2pm  /to  4pm  
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

## TC20: Mark each task type

**Aim:** Check that mark and unmark work on todos, deadlines and events, and that list shows the status.

**Input:**
```
todo read book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
mark 1
mark 3
list
unmark 3
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
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [E][X] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][ ] return book (by: Sunday)
 3.[E][X] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][ ] return book (by: Sunday)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC21: Repeated mark and unmark

**Aim:** Check that marking a done task or unmarking a not-done task is harmless.

**Input:**
```
todo read book
mark 1
mark 1
unmark 1
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
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] read book
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC22: Task number boundaries

**Aim:** Check the first and last task numbers work and numbers just outside the range are rejected.

**Input:**
```
todo a
todo b
todo c
mark 3
mark 4
mark -1
mark 1
list
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [T][ ] a
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] b
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] c
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] c
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 4 doesn't exist.
 Choose a number from 1 to 3. Type list to see the tasks.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task -1 doesn't exist.
 Choose a number from 1 to 3. Type list to see the tasks.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] a
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] a
 2.[T][ ] b
 3.[T][X] c
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC23: Invalid task number formats

**Aim:** Check that decimals, several numbers and overflowing numbers are rejected, and spaces before a number are fine.

**Input:**
```
todo read book
mark 1.5
mark 1 2
mark 99999999999
mark   
mark   1
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
 OOPS!!! "1.5" is not a valid task number.
 Use a plain whole number (no + sign or leading zeros), e.g. mark 2. Type list to see the task numbers.
____________________________________________________________
____________________________________________________________
 OOPS!!! "1 2" is not a valid task number.
 Use a plain whole number (no + sign or leading zeros), e.g. mark 2. Type list to see the task numbers.
____________________________________________________________
____________________________________________________________
 OOPS!!! "99999999999" is not a valid task number.
 Use a plain whole number (no + sign or leading zeros), e.g. mark 2. Type list to see the task numbers.
____________________________________________________________
____________________________________________________________
 OOPS!!! The task number is missing.
 Type the number of a task after "mark", e.g. mark 2
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC24: Unmark errors

**Aim:** Check that unmark reports bad, missing and out-of-range numbers using its own command name.

**Input:**
```
todo read book
unmark abc
unmark 0
unmark 9
unmark
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
 OOPS!!! "abc" is not a valid task number.
 Use a plain whole number (no + sign or leading zeros), e.g. unmark 2. Type list to see the task numbers.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 0 doesn't exist.
 Choose a number from 1 to 1. Type list to see the tasks.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 9 doesn't exist.
 Choose a number from 1 to 1. Type list to see the tasks.
____________________________________________________________
____________________________________________________________
 OOPS!!! The task number is missing.
 Type the number of a task after "unmark", e.g. unmark 2
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC25: Look-alike commands are unknown

**Aim:** Check that near-miss commands (wrong case, missing space, extra words) are rejected.

**Input:**
```
todoread book
TODO read book
listing
list extra
mark1
bye now
deadlines x /by y
events
bye
```

**Expected output:**
```
____________________________________________________________
 OOPS!!! I don't know the command "todoread book".
 Available commands: todo, deadline, event, list, mark, unmark, bye.
____________________________________________________________
____________________________________________________________
 OOPS!!! I don't know the command "TODO read book".
 Available commands: todo, deadline, event, list, mark, unmark, bye.
____________________________________________________________
____________________________________________________________
 OOPS!!! I don't know the command "listing".
 Available commands: todo, deadline, event, list, mark, unmark, bye.
____________________________________________________________
____________________________________________________________
 OOPS!!! I don't know the command "list extra".
 Available commands: todo, deadline, event, list, mark, unmark, bye.
____________________________________________________________
____________________________________________________________
 OOPS!!! I don't know the command "mark1".
 Available commands: todo, deadline, event, list, mark, unmark, bye.
____________________________________________________________
____________________________________________________________
 OOPS!!! I don't know the command "bye now".
 Available commands: todo, deadline, event, list, mark, unmark, bye.
____________________________________________________________
____________________________________________________________
 OOPS!!! I don't know the command "deadlines x /by y".
 Available commands: todo, deadline, event, list, mark, unmark, bye.
____________________________________________________________
____________________________________________________________
 OOPS!!! I don't know the command "events".
 Available commands: todo, deadline, event, list, mark, unmark, bye.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC26: Bye only

**Aim:** Check that `bye` alone exits with the goodbye message and nothing else.

**Input:**
```
bye
```

**Expected output:**
```
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC27: End of input without bye

**Aim:** Check that the chatbot exits normally when the input ends without `bye`.

**Input:**
```
todo read book
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

## TC28: Bye stops processing

**Aim:** Check that `bye` (even with spaces) exits at once and ignores later input.

**Input:**
```
todo a
  bye  
todo b
list
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [T][ ] a
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC29: Ten tasks numbering

**Aim:** Check that numbering and marking still work with two-digit task numbers.

**Input:**
```
todo task 1
todo task 2
todo task 3
todo task 4
todo task 5
todo task 6
todo task 7
todo task 8
todo task 9
todo task 10
mark 10
list
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 1
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 2
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 3
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 4
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 5
 Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 6
 Now you have 6 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 7
 Now you have 7 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 8
 Now you have 8 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 9
 Now you have 9 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] task 10
 Now you have 10 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] task 10
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] task 1
 2.[T][ ] task 2
 3.[T][ ] task 3
 4.[T][ ] task 4
 5.[T][ ] task 5
 6.[T][ ] task 6
 7.[T][ ] task 7
 8.[T][ ] task 8
 9.[T][ ] task 9
 10.[T][X] task 10
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC30: Errors do not change the task count

**Aim:** Check that failed adds leave the list and the task count unchanged.

**Input:**
```
todo a
deadline bad
event bad
todo
todo b
list
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [T][ ] a
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! A deadline needs a /by date, but I couldn't find one.
 Use the format: deadline DESCRIPTION /by DATE, e.g. deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
 OOPS!!! An event needs a /from time and a /to time, but I couldn't find either.
 Use the format: event DESCRIPTION /from START /to END, e.g. event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
 OOPS!!! The description of a todo is empty.
 Type a description after "todo", e.g. todo read book
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] b
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] a
 2.[T][ ] b
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC31: Repeated blank lines

**Aim:** Check that several blank or space-only lines each get an error and do not stop the program.

**Input:**
```


   
bye
```

**Expected output:**
```
____________________________________________________________
 OOPS!!! You didn't type a command.
 Available commands: todo, deadline, event, list, mark, unmark, bye.
____________________________________________________________
____________________________________________________________
 OOPS!!! You didn't type a command.
 Available commands: todo, deadline, event, list, mark, unmark, bye.
____________________________________________________________
____________________________________________________________
 OOPS!!! You didn't type a command.
 Available commands: todo, deadline, event, list, mark, unmark, bye.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC32: Deadline with only a marker

**Aim:** Check that a deadline with `/by` but no description reports the empty description.

**Input:**
```
deadline /by
deadline   /by   
bye
```

**Expected output:**
```
____________________________________________________________
 OOPS!!! The description of a deadline is empty.
 Type a description before /by, e.g. deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
 OOPS!!! The description of a deadline is empty.
 Type a description before /by, e.g. deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC33: Event with only markers

**Aim:** Check that an event with `/from` and `/to` but nothing else reports the empty description.

**Input:**
```
event /from /to
bye
```

**Expected output:**
```
____________________________________________________________
 OOPS!!! The description of an event is empty.
 Type a description before /from, e.g. event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC34: Markers must be whole words

**Aim:** Check that `/bypass` and `x/by` are not treated as `/by`, but a date such as `bypass` is fine.

**Input:**
```
deadline read /bypass book
deadline x/by y
deadline read /by bypass
bye
```

**Expected output:**
```
____________________________________________________________
 OOPS!!! A deadline needs a /by date, but I couldn't find one.
 Use the format: deadline DESCRIPTION /by DATE, e.g. deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
 OOPS!!! A deadline needs a /by date, but I couldn't find one.
 Use the format: deadline DESCRIPTION /by DATE, e.g. deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] read (by: bypass)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC35: All commands in one session

**Aim:** Check a realistic session that uses every command, with errors in between.

**Input:**
```
list
todo borrow book
deadline return book /by Sunday
blah
event project meeting /from Mon 2pm /to 4pm
mark 2
mark 5
unmark 2
mark
list
bye
```

**Expected output:**
```
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! I don't know the command "blah".
 Available commands: todo, deadline, event, list, mark, unmark, bye.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 5 doesn't exist.
 Choose a number from 1 to 3. Type list to see the tasks.
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
 OOPS!!! The task number is missing.
 Type the number of a task after "mark", e.g. mark 2
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] borrow book
 2.[D][ ] return book (by: Sunday)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC36: Interleaved valid and invalid adds

**Aim:** Check that failed adds between successful ones never change the count or the stored tasks.

**Input:**
```
todo a
todo
todo b
deadline
deadline c /by d
event
event e /from f /to g
list
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [T][ ] a
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! The description of a todo is empty.
 Type a description after "todo", e.g. todo read book
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] b
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! A deadline needs a /by date, but I couldn't find one.
 Use the format: deadline DESCRIPTION /by DATE, e.g. deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] c (by: d)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! An event needs a /from time and a /to time, but I couldn't find either.
 Use the format: event DESCRIPTION /from START /to END, e.g. event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] e (from: f to: g)
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] a
 2.[T][ ] b
 3.[D][ ] c (by: d)
 4.[E][ ] e (from: f to: g)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC37: Failed marks leave other tasks unchanged

**Aim:** Check that invalid mark and unmark commands between valid ones never change any task's status.

**Input:**
```
todo a
todo b
todo c
mark 2
mark 4
mark abc
mark 0
list
unmark 4
unmark x
list
unmark 2
list
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [T][ ] a
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] b
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] c
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] b
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 4 doesn't exist.
 Choose a number from 1 to 3. Type list to see the tasks.
____________________________________________________________
____________________________________________________________
 OOPS!!! "abc" is not a valid task number.
 Use a plain whole number (no + sign or leading zeros), e.g. mark 2. Type list to see the task numbers.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 0 doesn't exist.
 Choose a number from 1 to 3. Type list to see the tasks.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] a
 2.[T][X] b
 3.[T][ ] c
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 4 doesn't exist.
 Choose a number from 1 to 3. Type list to see the tasks.
____________________________________________________________
____________________________________________________________
 OOPS!!! "x" is not a valid task number.
 Use a plain whole number (no + sign or leading zeros), e.g. unmark 2. Type list to see the task numbers.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] a
 2.[T][X] b
 3.[T][ ] c
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] b
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] a
 2.[T][ ] b
 3.[T][ ] c
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC38: Errors between marks of one task

**Aim:** Check that an error between two marks of the same task leaves its status correct.

**Input:**
```
todo a
mark 1
unmark 9
mark 1
list
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [T][ ] a
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] a
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 9 doesn't exist.
 Choose a number from 1 to 1. Type list to see the tasks.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] a
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] a
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC39: No wrap-around of task numbers

**Aim:** Check that -1, 0 and count+1 do not select the last or first task.

**Input:**
```
todo a
todo b
todo c
mark -1
mark 0
mark 4
list
mark 3
list
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [T][ ] a
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] b
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] c
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task -1 doesn't exist.
 Choose a number from 1 to 3. Type list to see the tasks.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 0 doesn't exist.
 Choose a number from 1 to 3. Type list to see the tasks.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 4 doesn't exist.
 Choose a number from 1 to 3. Type list to see the tasks.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] a
 2.[T][ ] b
 3.[T][ ] c
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] c
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] a
 2.[T][ ] b
 3.[T][X] c
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC40: Failed event does not leak fields

**Aim:** Check that a rejected event does not affect the fields of later events.

**Input:**
```
event a /from 1 /to 2
event b /to 4 /from 3
event c /from 5 /to 6
list
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [E][ ] a (from: 1 to: 2)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! /to comes before /from.
 Put /from first, then /to. Use the format: event DESCRIPTION /from START /to END, e.g. event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] c (from: 5 to: 6)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[E][ ] a (from: 1 to: 2)
 2.[E][ ] c (from: 5 to: 6)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC41: Failed deadline does not leak fields

**Aim:** Check that rejected deadlines do not affect the fields of later deadlines.

**Input:**
```
deadline a /by 1
deadline b /by
deadline /by 2
deadline c /by 3
list
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [D][ ] a (by: 1)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! The date after /by is empty.
 Type when the task is due after /by, e.g. deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
 OOPS!!! The description of a deadline is empty.
 Type a description before /by, e.g. deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] c (by: 3)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] a (by: 1)
 2.[D][ ] c (by: 3)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC42: Command words as descriptions

**Aim:** Check that command words and markers inside a todo are stored as text and never executed.

**Input:**
```
todo list
todo bye
todo mark 1
todo event x /from a /to b
todo /by
list
mark 2
list
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [T][ ] list
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] bye
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] mark 1
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] event x /from a /to b
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] /by
 Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] list
 2.[T][ ] bye
 3.[T][ ] mark 1
 4.[T][ ] event x /from a /to b
 5.[T][ ] /by
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] bye
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] list
 2.[T][X] bye
 3.[T][ ] mark 1
 4.[T][ ] event x /from a /to b
 5.[T][ ] /by
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC43: Special characters

**Aim:** Check that accents, symbols, quotes, backslashes and percent signs are stored and printed unchanged.

**Input:**
```
todo café ☕ ñ
todo "quoted" 'text'
todo back\slash 100% {braces} $dollar
deadline pay $5 /by 50% off
list
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [T][ ] café ☕ ñ
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] "quoted" 'text'
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] back\slash 100% {braces} $dollar
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] pay $5 (by: 50% off)
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] café ☕ ñ
 2.[T][ ] "quoted" 'text'
 3.[T][ ] back\slash 100% {braces} $dollar
 4.[D][ ] pay $5 (by: 50% off)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC44: Very long descriptions

**Aim:** Check that 200-character descriptions are handled and an error in between changes nothing.

**Input:**
```
todo xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
todo
deadline xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx /by xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
list
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [T][ ] xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! The description of a todo is empty.
 Type a description after "todo", e.g. todo read book
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx (by: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
 2.[D][ ] xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx (by: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC45: Only plain integers are task numbers

**Aim:** Check that `01`, `+2`, `-0` and `00` are rejected as invalid numbers without changing any task, `0` is out of range, and a plain number then works.

**Input:**
```
todo a
todo b
mark 01
mark +2
mark -0
mark 00
unmark 01
mark 0
list
mark 2
list
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [T][ ] a
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] b
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! "01" is not a valid task number.
 Use a plain whole number (no + sign or leading zeros), e.g. mark 2. Type list to see the task numbers.
____________________________________________________________
____________________________________________________________
 OOPS!!! "+2" is not a valid task number.
 Use a plain whole number (no + sign or leading zeros), e.g. mark 2. Type list to see the task numbers.
____________________________________________________________
____________________________________________________________
 OOPS!!! "-0" is not a valid task number.
 Use a plain whole number (no + sign or leading zeros), e.g. mark 2. Type list to see the task numbers.
____________________________________________________________
____________________________________________________________
 OOPS!!! "00" is not a valid task number.
 Use a plain whole number (no + sign or leading zeros), e.g. mark 2. Type list to see the task numbers.
____________________________________________________________
____________________________________________________________
 OOPS!!! "01" is not a valid task number.
 Use a plain whole number (no + sign or leading zeros), e.g. unmark 2. Type list to see the task numbers.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 0 doesn't exist.
 Choose a number from 1 to 2. Type list to see the tasks.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] a
 2.[T][ ] b
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] b
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] a
 2.[T][X] b
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC46: Blank lines and unknown commands between valid ones

**Aim:** Check that blank lines and unknown commands between valid commands change nothing.

**Input:**
```
todo a

mark 1
blah
todo b
  
list
unmark 1
list
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [T][ ] a
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! You didn't type a command.
 Available commands: todo, deadline, event, list, mark, unmark, bye.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] a
____________________________________________________________
____________________________________________________________
 OOPS!!! I don't know the command "blah".
 Available commands: todo, deadline, event, list, mark, unmark, bye.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] b
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! You didn't type a command.
 Available commands: todo, deadline, event, list, mark, unmark, bye.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] a
 2.[T][ ] b
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] a
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] a
 2.[T][ ] b
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC47: Retry after each kind of error

**Aim:** Check that every rejected command works once it is corrected.

**Input:**
```
todo
todo read book
mark
mark 1
deadline x
deadline x /by y
event x /from 1
event x /from 1 /to 2
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
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! The task number is missing.
 Type the number of a task after "mark", e.g. mark 2
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 OOPS!!! A deadline needs a /by date, but I couldn't find one.
 Use the format: deadline DESCRIPTION /by DATE, e.g. deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] x (by: y)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! An event needs a /to time, but I couldn't find one.
 Use the format: event DESCRIPTION /from START /to END, e.g. event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] x (from: 1 to: 2)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][ ] x (by: y)
 3.[E][ ] x (from: 1 to: 2)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC48: Mark before and after the first task

**Aim:** Check that marking an empty list fails, and that adding a task then makes marking 1 work but 2 fail.

**Input:**
```
mark 1
todo a
mark 1
mark 2
list
bye
```

**Expected output:**
```
____________________________________________________________
 OOPS!!! There are no tasks to mark yet.
 Add a task first, e.g. todo read book
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] a
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] a
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 2 doesn't exist.
 Choose a number from 1 to 1. Type list to see the tasks.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] a
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC49: Repeated markers in events

**Aim:** Check that with repeated `/to` or `/from` the first `/from` and first `/to` split the text, and a later error changes nothing.

**Input:**
```
event a /from 1 /to 2 /to 3
event b /from 1 /from 2 /to 3
event c /from 1
list
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [E][ ] a (from: 1 to: 2 /to 3)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] b (from: 1 /from 2 to: 3)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! An event needs a /to time, but I couldn't find one.
 Use the format: event DESCRIPTION /from START /to END, e.g. event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[E][ ] a (from: 1 to: 2 /to 3)
 2.[E][ ] b (from: 1 /from 2 to: 3)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC50: Status kept after an error

**Aim:** Check that a deadline's done status and fields survive a later failed command.

**Input:**
```
deadline a /by b
mark 1
deadline
list
bye
```

**Expected output:**
```
____________________________________________________________
 Got it. I've added this task:
   [D][ ] a (by: b)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] a (by: b)
____________________________________________________________
____________________________________________________________
 OOPS!!! A deadline needs a /by date, but I couldn't find one.
 Use the format: deadline DESCRIPTION /by DATE, e.g. deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][X] a (by: b)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
