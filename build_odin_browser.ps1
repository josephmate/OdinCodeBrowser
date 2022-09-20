$Args = (
" --inputSourceDirectory src/main/java" +
" --outputDirectory docs/odin.code.browser" +
" --webPathToCssFile /OdinCodeBrowser/css/styles.css" +
" --webPathToSourceHtmlFiles /OdinCodeBrowser/odin.code.browser" +
" --languageLevel JAVA_16" +
" --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowserJdk8/index.json" +
" --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowserRepos/javaparser-core_3.23.1/index.json" +
" --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowserRepos/commons-lang3_3.11/index.json" +
" --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowserRepos/commons-text_1.9/index.json" +
" --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowserRepos/commons-collections_4.4/index.json"
)

mvn install exec:java `
  "-Dexec.mainClass=Main" `
  "-Dexec.args=$Args"
