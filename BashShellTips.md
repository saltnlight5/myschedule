Here are some useful shell aliases and function that makes Java development life easier. It should run well under Linux or Windows with Cygwin.

I often use Ruby to do quick command and scripting. You can use this quick ref:
http://www.rubyholic.com/Languages/Ruby/QuickRef.html


Here are some of my frequently used `.bashrc` goodies:
```

# Add tools in path
export PATH=/c/apps/jdk1.6.0_26/bin:\
/c/apps/apache-maven-2.0.8/bin:\
/c/apps/groovy-1.8.1/bin:\
$PATH

# Removed duplicated PATH entries to keep it clean.
export PATH=`echo -n $PATH | ruby -F: -ane 'print $F.uniq.join(File::PATH_SEPARATOR)'`

# Cygwin tools
function e() { /c/apps/Notepad++/notepad++.exe `cygpath -w "$@"` ; } # use this to edit any text file!
alias open=cygstart # open any cygwin path in Windows Explorer!

# Quick edit of my monthly journal
alias ej='e /c/journals/`date "+%Y-%b"`.txt'

# Java Development
export JAVA_HOME='C:/apps/jdk1.6.0_26' # if you are using cygwin.
export MAVEN_OPTS='-XX:MaxPermSize=512M'

function mkcp() { cygpath -pw `ruby -e "print Dir['$1/**/*.jar'].join(File::PATH_SEPARATOR)"` ; }

alias mvntest='mvn clean test | tee mvn-clean-test_`date '+%F_%H-%M-%S'`.log'
function checktest() {
	# Show failed tests among all the maven surefire results.
	ruby -ne 'print "#$FILENAME : #$&\n" if $_ =~ /(Failures: [1-9][0-9]*.*|Errors: [1-9][0-9]*.*)/' target/surefire-reports/*.txt
}

# Run any standalone junit test
alias junit='run-java org.junit.runner.JUnitCore'

# Check java process with command line args shown
alias jps2='jps -mvl | grep -v sun.tools.jps.Jps' # Show Java processes with full arguments.
alias killjps2="jps2 | ruby -ane '`/usr/bin/kill -f #{$F[0]}`'" # Terminate all the running Java processes.
```