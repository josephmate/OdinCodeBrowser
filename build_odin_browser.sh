args="--inputSourceDirectory src/main/java"
args="$args --outputDirectory docs/odin.code.browser"
args="$args --webPathToCssFile /OdinCodeBrowser/css/styles.css"
args="$args --webPathToSourceHtmlFiles /OdinCodeBrowser/odin.code.browser"
args="$args --languageLevel JAVA_16"
args="$args --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowserJdk8/index.json"
args="$args --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowserRepos/javaparser-core_3.23.1/index.json"
args="$args --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowserRepos/commons-lang3_3.11/index.json"
args="$args --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowserRepos/commons-text_1.9/index.json"
args="$args --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowserRepos/commons-collections_4.4/index.json"
mvn install exec:java \
  -Dexec.mainClass=Main \
  -Dexec.args="$args"
