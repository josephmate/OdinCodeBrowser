args="--inputSourceDirectory ../jdk8/jdk/src/share/classes"
args="$args --outputDirectory docs/jdk8"
args="$args --webPathToCssFile /OdinCodeBrowser/css/styles.css"
args="$args --webPathToSourceHtmlFiles /OdinCodeBrowser/jdk8"
args="$args --languageLevel JAVA_8"
mvn install exec:java \
  -Dexec.mainClass=Main \
  -Dexec.args="$args"
