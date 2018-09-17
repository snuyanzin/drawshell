#!/bin/bash
# drawingshell - Script to launch drawing shell on Unix, Linux or Mac OS

BINPATH=$(dirname $0)
exec java -cp "$BINPATH/../target/*" com.springer.DrawingShell "$@"

# End drawingshell.sh
