#!/usr/bin/env bash
#
# Copyright 2013 Zemian Deng
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#    http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Start up script for Winstone embedded servlet container to boostrap myscheduler.war web application for
# managing Quartz Schedulers.
#
# Created by: Zemian Deng 03/28/2013

# This run script dir (resolve to absolute path)
SCRIPT_DIR=$(cd $(dirname $0) && pwd)    # This dir is where this script live.
APP_DIR=$(cd $SCRIPT_DIR/.. && pwd)      # Assume the application dir is one level up from script dir.

if [[ $OS == Windows* ]]; then
	APP_DIR="`cygpath -wa $APP_DIR`"
	SCRIPT_DIR="`cygpath -wa $SCRIPT_DIR`"
fi

$SCRIPT_DIR/run-java \
-Dscheduler.home=$APP_DIR \
-Dmyschedule.settings=$SCRIPT_DIR/myschedule-settings.properties \
winstone.Launcher \
--commonLibFolder=$APP_DIR/lib \
--ajp13Port=-1 \
--warfile=$APP_DIR/war/myschedule.war \
"$@"
