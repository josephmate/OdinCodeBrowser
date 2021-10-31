args="--inputSourceDirectory ../commons-lang3_3.11/src/main/java"
args="$args --outputDirectory docs/commons-lang3_3.11"
args="$args --webPathToCssFile /OdinCodeBrowser/css/styles.css"
args="$args --webPathToSourceHtmlFiles /OdinCodeBrowser/commons-lang3_3.11"
args="$args --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowser/jdk8/index.json"
mvn install exec:java \
  -Dexec.mainClass=Main \
  -Dexec.args="$args"
