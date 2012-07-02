#!/bin/bash
# shell script to compress the SocialArchivalTool.

jar cfm SocialArchivalTool.jar manifest.txt code source org docs *.sh README.txt HELP.txt
