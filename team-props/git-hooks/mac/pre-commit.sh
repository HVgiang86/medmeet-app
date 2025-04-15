#!/usr/bin/env bash
echo "Running static analysis..."

echo "Start running ktlint"
./gradlew ktlintFormat ktlintCheck --daemon
status1=$?
if [[ "$status1" = 0 ]] ; then
    echo "*******************************************************"
    echo "             Ktlint runs successfully                  "
    echo "*******************************************************"
else
    echo "*******************************************************"
    echo "                 Ktlint failed                         "
    echo "     Please fix the reported issues before commit.     "
    echo "*******************************************************"
    exit status1
fi

# add changed files after run auto format
git add .
