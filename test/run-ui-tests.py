#!/usr/bin/env python3
"""Runs the UI test cases in test/ui-test-plan.md against eric.Eric.

Prints a record of each test case's console input and output, and stops
at the first failure, reporting the actual and expected output.
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
    r"\*\*Expected output:\*\*\n```\n(.*?)\n```",
    re.M | re.S)


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
    cases = CASE.findall(PLAN.read_text(encoding="utf-8"))
    if not cases:
        sys.exit(f"No test cases found in {PLAN}")

    with tempfile.TemporaryDirectory() as build_dir:
        compiled = subprocess.run(
            ["javac", "-d", build_dir] + [str(p) for p in (ROOT / "src/main/java").rglob("*.java")],
            capture_output=True, text=True)
        if compiled.returncode != 0:
            sys.exit("COMPILE ERROR\n" + compiled.stderr)

        for title, aim, inputs, expected in cases:
            run = subprocess.run(["java", "-cp", build_dir, "eric.Eric"], input=inputs + "\n",
                                 capture_output=True, text=True, timeout=30)
            actual = normalise(strip_banner(run.stdout))
            wanted = normalise(expected.splitlines())

            print(f"=== {title}\nAim: {aim}\n--- console input\n{inputs}\n--- console output")
            print("\n".join(actual))
            if actual != wanted:
                print(f"\nFAIL: {title}\n\n--- expected output\n" + "\n".join(wanted)
                      + "\n\n--- actual output\n" + "\n".join(actual))
                sys.exit(1)
            print(f"PASS\n")
        print(f"All {len(cases)} test cases passed.")


if __name__ == "__main__":
    main()
