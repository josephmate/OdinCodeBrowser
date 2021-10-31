args="--inputSourceDirectory ../commons-text_1.9/src/main/java"
args="$args --outputDirectory docs/commons-text_1.9"
args="$args --webPathToCssFile /OdinCodeBrowser/css/styles.css"
args="$args --webPathToSourceHtmlFiles /OdinCodeBrowser/commons-text_1.9"
args="$args --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowser/jdk8/index.json"
mvn install exec:java \
  -Dexec.mainClass=Main \
  -Dexec.args="$args"
