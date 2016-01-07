This documentation list tools and instruction on how to setup your environment to develop and contribute to the `MySchedule` project.

NOTE: If you need help on developing new Quartz Jobs or its specific etc, you can also get more help from their support forums. This guide will only provide `MySchedule` project development notes.

Before you begin, you should always see the README.md from the root of the project file for up to date information.

# Java Development #

Required Tools:

  * JDK6
  * Maven3
  * Mercurial (hg)

Optional IDE Tools:

  * IDEA IntelliJ or Eclipse

Optional Misc Tools:
  * MySQL database
  * Chrome or Firefox browser
  * Cygwin (if you are on Windows)

# How we use version format for MySchedule #

MySchedule version format is a.b.c.d, where

  * "a" MySchedule major version
  * "b" indicates the quartz major version.
  * "c" MySchedule minor version
  * "d" MySchedule bug fix version.

# Building `MySchedule` from project source #

  1. bash> hg clone https://code.google.com/p/myschedule
  1. bash> cd myschedule
  1. bash> mvn install

If you want a specific release of source, then switch to it like this:

  1. bash> hg update myschedule-3.2.0.0

Or if you want a maintenance branch, then you switch to the named-branch

  1. bash> hg update myschedule-3.2.x

## Running `MySchedule` using Maven Tomcat plugin directly from project source ##

You can run `myschedule-web` module during development like this in Cygwin/Linux shell:

  1. bash> cd myschedule-web
  1. bash> tomcat7:run

Now you can browse `http://localhost:8080/myschedule-web`

# Performing Releases #

In Windows + Cygwin, you must use a Mercurial for Windows (hg.exe) in PATH in order to work with Maven release plugin!

  1. bash> mvn release:prepare
  1. bash> mvn release:perform

If you need to clean up file when things went wrong, try `bash> mvn release:clean`

Note: the default install repository for the `MySchedule` release is  configured to a local `${user.home}/.m2/repository` directory. You can upload to a remote site manually after the release is done.