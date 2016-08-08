#!/usr/bin/env bash

set -e

VERSION=`git tag | tail -n 1`

sed "s/@@VERSION@@/$VERSION/g" README.template.md > README.md

cp README.md src/site/markdown/index.md

mvn clean install org.pitest:pitest-maven:mutationCoverage site
