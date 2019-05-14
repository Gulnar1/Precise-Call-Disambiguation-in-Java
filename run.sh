#!/bin/bash
clear

mvn exec:java -Dexec.mainClass="vasco.soot.examples.$1" -Dexec.args="-cp target/test-classes/ vasco.tests.$2" 
