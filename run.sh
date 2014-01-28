#!/usr/bin/env bash

APPNAME="PositionKeeper"
. serverconfig.properties

# find voltdb binaries in either installation or distribution directory.
VOLTDB_BASE=$voltdb_base
VOLTDB_BIN="$VOLTDB_BASE/bin"
# installation layout has all libraries in $VOLTDB_ROOT/lib/voltdb
if [ -d "$VOLTDB_BIN/../lib/voltdb" ]; then
    VOLTDB_BASE=$(dirname "$VOLTDB_BIN")
    VOLTDB_LIB="$VOLTDB_BASE/lib/voltdb"
    VOLTDB_VOLTDB="$VOLTDB_LIB"
# distribution layout has libraries in separate lib and voltdb directories
else
    VOLTDB_LIB="$VOLTDB_BIN/../lib"
    VOLTDB_VOLTDB="$VOLTDB_BIN/../voltdb"
fi

APPCLASSPATH=$CLASSPATH:$({ \
    \ls -1 "$VOLTDB_VOLTDB"/voltdb-*.jar; \
    \ls -1 "$VOLTDB_LIB"/*.jar; \
    \ls -1 "$VOLTDB_LIB"/extension/*.jar; \
} 2> /dev/null | paste -sd ':' - )
CLIENTCLASSPATH=$CLASSPATH:$({ \
    \ls -1 "$VOLTDB_VOLTDB"/voltdbclient-*.jar; \
    \ls -1 "$VOLTDB_LIB"/commons-cli-1.2.jar; \
    \ls -1 "$VOLTDB_LIB"/guava-12.0.jar; \
	\ls -1 lib/*.jar; \
} 2> /dev/null | paste -sd ':' - )

VOLTDB="$VOLTDB_BIN/voltdb"
LOG4J="$VOLTDB_VOLTDB/log4j.xml"
LICENSE="$VOLTDB_VOLTDB/license.xml"
HOST=$host
CLIENTHOST=$clienthost
PROCEDURENAME=""
MAXHEAPSIZE=$maxheapsize

# remove build artifacts
function clean() {
    rm -rf obj debugoutput $APPNAME.jar voltdbroot voltdbroot
}

# compile the source code for procedures and the client
function srccompile() {
    mkdir -p obj
    javac -target 1.6 -source 1.6 -classpath $APPCLASSPATH -d obj \
        src/PositionKeeper/*.java \
        src/PositionKeeper/procedures/*.java
    # stop if compilation fails
    if [ $? != 0 ]; then exit; fi
}

# build an application catalog
function catalog() {
    srccompile
    $VOLTDB compile --classpath obj -o $APPNAME.jar ddl.sql
    # stop if compilation fails
    if [ $? != 0 ]; then exit; fi
}

# run the voltdb server locally
function server() {
    # if a catalog doesn't exist, build one
    if [ ! -f $APPNAME.jar ]; then catalog; fi
    export VOLTDB_HEAPSIZE=$MAXHEAPSIZE
    # run the server
    $VOLTDB create catalog $APPNAME.jar deployment deployment.xml \
        license $LICENSE host $HOST
}

# run the voltdb server locally
function rejoin() {
    # if a catalog doesn't exist, build one
    if [ ! -f $APPNAME.jar ]; then catalog; fi
    # run the server
    $VOLTDB deployment deployment.xml \
        license $LICENSE host $HOST
}

#insert trade data into voltdb
function tradesimulator() {
    srccompile
    java -classpath obj:$CLIENTCLASSPATH:obj -Dlog4j.configuration=file://$LOG4J \
        PositionKeeper.TestDataSimulator \
        --displayinterval=5 \
        --duration=120 \
        --servers=$CLIENTHOST \
		--tradevolume=500000
}

#test the voltdb query performance
function positionkeeper() {
    srccompile
    java -classpath obj:$CLIENTCLASSPATH:obj -Dlog4j.configuration=file://$LOG4J \
        PositionKeeper.$PROCEDURENAME \
        --displayinterval=5 \
        --duration=120 \
        --servers=$CLIENTHOST \
	    --tradevolume=500000
}

function help() {
    echo "Usage: ./run.sh {clean|catalog|server|async-benchmark|aysnc-benchmark-help|...}"
    echo "       {...|sync-benchmark|sync-benchmark-help|jdbc-benchmark|jdbc-benchmark-help}"
}

# Run the target passed as the first arg on the command line
# If no first arg, run server
if [ $# = 1 ];
	then $1
else 
	if [ $# -gt 0 ]; 
		then PROCEDURENAME=$2
		echo $PROCEDURENAME
		$1; 
	else server; 
	fi
fi
