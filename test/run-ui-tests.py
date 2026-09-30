#!/usr/bin/env python3
"""Runs the UI test cases in test/ui-test-plan.md against eric.Eric.

Each test case runs in its own empty temporary folder, so the data file that
Eric saves (data/duke.txt) never touches real data and starts out missing.
Prints a record of each test case's console input and output (and the saved
file, if the test case specifies its expected contents), and stops at the
first failure, reporting the actual and expected output.
Usage: python3 test/run-ui-tests.py [path/to/plan.md]
"""
import re
import subprocess
import sys
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
PLAN = Path(sys.argv[1]) if len(sys.argv) > 1 else ROOT / "test" / "ui-test-plan.md"
CASE = re.compile(
    r"^## (TC\d+: .+?)\n\n\*\*Aim:\*\* (.+?)\n\n\*\*Input:\*\*\n```\n(.*?)\n```\n\n"
    r"\*\*Expected output:\*\*\n```\n(.*?)\n```"
    r"(?:\n\n\*\*Expected file \(data/duke\.txt\):\*\*\n```\n(.*?)\n```)?",
    re.M | re.S)
DATA_FILE = Path("data") / "duke.txt"
NO_FILE = "(file not created)"  # write this as the expected file contents if no file should exist


def strip_banner(output):
    """Removes the startup banner and greeting, keeping everything after it."""
    lines = output.splitlines()
    for i, line in enumerate(lines):
        if line.strip() == "What can I do for you?":
            return lines[i + 2:]  # skip the divider that closes the greeting
    return lines


def normalise(lines):
    return [line.rstrip() for line in lines]


def main():
    cases = [m.groups() for m in CASE.finditer(PLAN.read_text(encoding="utf-8"))]
    if not cases:
        sys.exit(f"No test cases found in {PLAN}")

    with tempfile.TemporaryDirectory() as build_dir:
        compiled = subprocess.run(
            ["javac", "-d", build_dir] + [str(p) for p in (ROOT / "src/main/java").rglob("*.java")],
            capture_output=True, text=True)
        if compiled.returncode != 0:
            sys.exit("COMPILE ERROR\n" + compiled.stderr)

        for title, aim, inputs, expected, expected_file in cases:
            with tempfile.TemporaryDirectory() as work_dir:
                run = subprocess.run(["java", "-cp", build_dir, "eric.Eric"], input=inputs + "\n",
                                     capture_output=True, text=True, timeout=30, cwd=work_dir)
                saved = Path(work_dir) / DATA_FILE
                actual_file = saved.read_text(encoding="utf-8").splitlines() if saved.exists() else None
            actual = normalise(strip_banner(run.stdout))
            wanted = normalise(expected.splitlines())

            print(f"=== {title}\nAim: {aim}\n--- console input\n{inputs}\n--- console output")
            print("\n".join(actual))
            if expected_file is not None:
                print(f"--- saved file {DATA_FILE.as_posix()}")
                print(NO_FILE if actual_file is None else "\n".join(actual_file))
            if actual != wanted:
                print(f"\nFAIL: {title}\n\n--- expected output\n" + "\n".join(wanted)
                      + "\n\n--- actual output\n" + "\n".join(actual))
                sys.exit(1)
            wanted_file = None if expected_file == NO_FILE else (
                None if expected_file is None else normalise(expected_file.splitlines()))
            if expected_file is not None and actual_file != wanted_file:
                print(f"\nFAIL: {title} (saved file)\n\n--- expected file\n" + expected_file
                      + "\n\n--- actual file\n" + (NO_FILE if actual_file is None
                                                      else "\n".join(actual_file)))
                sys.exit(1)
            print("PASS\n")
        print(f"All {len(cases)} test cases passed.")


if __name__ == "__main__":
    main()
