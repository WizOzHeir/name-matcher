#!/bin/sh
echo Building WizOzHeir/name-matcher:build
docker build --build-arg url=https://github.com/WizOzHeir/name-matcher\
  --build-arg project=name-matcher\
  --build-arg artifactid=namematcher\
  --build-arg version=0.1.0\
  -t name-matcher - < Dockerfile