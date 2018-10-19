#!/bin/bash
# drawingshell - Script to launch drawing shell on Unix, Linux or Mac OS

BINPATH=$(dirname $0)
exec java -Xmx4G -cp "$BINPATH/../target/*" ru.nuyanzin.DrawingShell "$@"

# End drawingshell.sh
