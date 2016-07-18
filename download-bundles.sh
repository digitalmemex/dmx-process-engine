#!/bin/sh

for b in $(egrep ^http bundle.urls); do
  wget $b
done

