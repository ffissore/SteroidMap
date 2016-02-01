#!/usr/bin/env bash

set -e

cp README.md src/site/markdown/index.md

mvn clean install org.pitest:pitest-maven:mutationCoverage site
