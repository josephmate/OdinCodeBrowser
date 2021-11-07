# Odin Code Browser

This project attempts to create a completely static website of your source code,
implementing navigate using anchors and links. The goal is to be as good as
navigating code as in Intellij. See a sample
[here](https://josephmate.github.io/OdinCodeBrowser/commons-text_1.9/org/apache/commons/text/StringEscapeUtils.html).

# Motivation
I used grepcode at least once a week and I am sad to see it go.
It's navigation was almost as good as an IDE.
You could navigate from apache commons all the way down to the JDK.
I hope that I can create a spiritual successor by using this tool,
that will live much longer since the source code is available,
and the resulting static webpages are decentralized.

# Design Philosophy 
1. No javascript
2. No javascript!!!
3. Each page is static

# How Do I Use This

## As a code browser
1. Find a project you like from [this list of supported repos](https://josephmate.github.io/OdinCodeBrowser/)
1. Go to the list of all classes [(ex: Apache Commons Text)](https://josephmate.github.io/OdinCodeBrowser/jdk8/)
2. Find your class (ex: A class Odin uses 
   [StringEscapeUtils](https://josephmate.github.io/OdinCodeBrowser/commons-text_1.9/org/apache/commons/text/StringEscapeUtils.html))
3. Start reading code and navigating by clicking on the links

## As a code owner
```
git clone git@github.com:josephmate/OdinCodeBrowser.git
cd OdinCodeBrowser
args="--inputSourceDirectory <path_to_your_source>"
args="$args --outputDirectory <output_directory>"
args="$args --webPathToCssFile https://josephmate.github.io/OdinCodeBrowser/css/styles.css"
args="$args --webPathToSourceHtmlFiles <directory_on_webserver>"
args="$args --languageLevel JAVA_16"
args="$args --urlToDependantIndexJson <url_of_dependencies_index_file>"
mvn install exec:java \
  -Dexec.mainClass=Main \
  -Dexec.args="$args"
```

Creating an Odin Navigator for my project as an example, with dependencies on
github javaparser, apache commons, apache commons text, and JDK8 that I host on
https://josephmate.github.io/OdinCodeBrowser/ .
```
args="--inputSourceDirectory src/main/java"
args="$args --outputDirectory docs/odin.code.browser"
args="$args --webPathToCssFile /OdinCodeBrowser/css/styles.css"
args="$args --webPathToSourceHtmlFiles /OdinCodeBrowser/odin.code.browser"
args="$args --languageLevel JAVA_16"
args="$args --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowser/jdk8/index.json"
args="$args --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowser/javaparser-core_3.23.1/index.json"
args="$args --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowser/commons-lang3_3.11/index.json"
args="$args --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowser/commons-text_1.9/index.json"
mvn install exec:java \
  -Dexec.mainClass=Main \
  -Dexec.args="$args"
```

## Fun and games
1. Start at a random class in [jdk8](https://josephmate.github.io/OdinCodeBrowser/jdk8/)
2. Using links only, how fast can you get my favourite class: HashMap?

For example I did
[I18NImpl](https://josephmate.github.io/OdinCodeBrowser/jdk8//com/sun/imageio/plugins/common/I18NImpl.html)
->
[PropertyResourceBundle](https://josephmate.github.io/OdinCodeBrowser/jdk8//java/util/PropertyResourceBundle.html#linenum122)
->
[HashMap](https://josephmate.github.io/OdinCodeBrowser/jdk8//java/util/HashMap.html#linenum137)

However, sometimes you'll pick a class that does not have a path to HashMap :(.

# Alternatives
Your Java IDE can do a much better job. As I was write this project, I
use Intellij to navigate from my projects code to the code of the projects I
depend on. The experience is so much better in the IDE and you should use that.
If you are already using your IDE, keep using it!

If you don't use an IDE then github provides some support for navigating code without any extra effort.
Just commit your code as you normally do and github updates the navigation.

The two features together that sets Odin apart from an IDE and GitHub:
1. [x] sharing links to the code (can't do this in an IDE). Since it's webpage,
   this means you can use it on low memory devices like your phone!
2. [x] can link to the dependant sources as well (github can't do this)

# Comparison
| Dimension                | Odin | IDE | Github |
| ------------------------ | ---- | --- | ------ |
| Shares links             | ✅   | ❌  | ✅     |
| Minimal CPU Usage        | ✅   | ❌  | ✅     |
| Minimal Memory Usage     | ✅   | ❌  | ✅     |
| Minamal Storage Usage    | ✅   | ❌  | ✅     |
| Realtime                 | ❌   | ✅  | ❌     |
| Automatically Applied    | ❌   | ✅  | ✅     |
| Static Pages             | ✅   |     | ❌     |
| No javascript            | ✅   |     | ❌     |
| Navigate to dependencies | ✅   | ✅  | ❌     |
| Comment on code          | ❌   | ✅  | ✅     |
| Navigatation Complete    | ❌   | ✅  | ❌     |



Explanation of each:

* **Shares links**: In Odin and GitHub you can share links to any line in the code.
  An IDE does not let you do that.
* **Minimal CPU/Memory/Storage Usage**: Since Odin and GitHub are not realtime, no cpu, memory or
  diskspace is needed by the client to efficiently calculate all the indexes.
* **Realtime** As you make changes in an IDE, the indexes are update and
  you can navigate to the new code changes or new dependencies.
* **Automatically Applied**: By rebuilding in the IDE, new code is recognized.
  By commiting and pushing your changes to GitHub, the navigation is updated.
  For Odin, you must manually build and publish the static webpages. You
  unforutnately need to manage this. GitHub and and IDE manage this for you with
  no effort on your part.
* **Static Pages**: Odin's output is hosted as static webpage that can be
  efficiently distributed. This comparison doesn't make sense for an IDE.
* **No javascript**: Odin doesn't use javascript while GitHub needs to.
* **Navigate to dependencies**: IDE and seemlessly navigate to your
  dependencies. Odin plans to navigate to dependencies when you build. It will
  not be as convenient as an IDE. GitHub only lets you navigate within the same
  repository.
* **Navigation Complete**: anything you could possibly want to navigate to or
  from in an IDE works. Odin is working on matching an IDE and might reach the
  same level one day. GitHub navigation is also not as good.
* **Comment on code**: GitHub is probably the best for comment and discussing
  code. You can also do this in an IDE with plugins, but it's not as convenient
  as GitHub. Since Odin pages are static without javascript, it's impossible to
  have commenting.

# Feature List
1. [x] Line numbers with links
2. [ ] Code is easily copy and pastable
    1. [x] Code is copy and pastable 
    2. [ ] Copied code is exactly the same as pasted code
3. [x] Click on a Type to navigate to the defintion of that type
    1. Type
        1. [x] Interface
        1. [x] Annotation
        1. [x] Outer class
        2. [x] Inner class
        3. [ ] Record
        3. [ ] Class inside function
        4. [ ] Anonymous class
    1. Type Usage
        1. [x] [Variable type](https://josephmate.github.io/OdinCodeBrowser/jdk8/com/oracle/net/Sdp.html#linenum56)
        2. [x] [Method return type](https://josephmate.github.io/OdinCodeBrowser/jdk8/com/oracle/net/Sdp.html#linenum104)
        3. [x] [Extends type](http://josephmate.github.io/OdinCodeBrowser/jdk8/com/oracle/net/Sdp.html#linenum95)
        4. [x] Implement type
        5. [ ] Annotation (the implementation of the annotation)
        6. [ ] Import
        7. [x] [Types through wildcard imports (ex: List from java.util.*)](https://josephmate.github.io/OdinCodeBrowser/jdk8//java/util/concurrent/ScheduledThreadPoolExecutor.html#linenum785)
        8. [x] [Types within same package (ex: ThreadPoolExecutor when in package java.util.concurrent)](https://josephmate.github.io/OdinCodeBrowser/jdk8//java/util/concurrent/ScheduledThreadPoolExecutor.html#linenum122)
        8. [x] [Types from java.lang (ex: IllegalArgumentException)](http://josephmate.github.io/OdinCodeBrowser/jdk8//java/util/HashMap.html#linenum448)
        8. [ ] Types within same file
4. [ ] Click on method to go the definition of that method
    1. [x] [Static and only one function with the name (ex: Objects.hashCode(key))](http://josephmate.github.io/OdinCodeBrowser/jdk8/java/util/HashMap.html#linenum296)
    3. [x] [Functions within the same file (ex: putMapEntries(m, true)](http://josephmate.github.io/OdinCodeBrowser/jdk8//java/util/HashMap.html#linenum784)
    3. [x] [Functions on this (ex: this.getCanonName())](http://josephmate.github.io/OdinCodeBrowser/jdk8/java/net/SocketPermission.html#linenum621)
    1. [x] [Object instance and only one function with the name and not in super class (ex: x.getClass())](http://josephmate.github.io/OdinCodeBrowser/jdk8/java/util/HashMap.html#linenum348)
    1. [ ] Object instance and only one function with the name but in some super class
    1. [ ] Chained method calls
    2. [ ] Functions are overloaded (different parameters)
    3. [ ] import static functions
    3. [ ] Scoping rules (if names are duplicated in mulitple scopes, need to use the closest scope)
    4. [x] [String literal method calls (ex: "true".equals(blah))](https://josephmate.github.io/OdinCodeBrowser/jdk8/com/sun/beans/finder/BeanInfoFinder.html)
    4. [x] [class literal method calls (ex: boolean.class.getName())](https://josephmate.github.io/OdinCodeBrowser/jdk8/com/sun/beans/finder/PrimitiveTypeMap.html#linenum54)
5. [ ] Click on variable to the definition of that variable
    1. [x] [from function param](http://josephmate.github.io/OdinCodeBrowser/jdk8/java/util/HashMap.html#linenum345)
    1. [x] [local var](http://josephmate.github.io/OdinCodeBrowser/jdk8/java/util/HashMap.html#linenum568)
    1. [x] [from scope (for/while/if)](http://josephmate.github.io/OdinCodeBrowser/jdk8/java/util/HashMap.html#linenum352)
    1. [x] [field var](http://josephmate.github.io/OdinCodeBrowser/jdk8/java/util/HashMap.html#linenum291)
    1. [x] [static field var](http://josephmate.github.io/OdinCodeBrowser/jdk8/java/util/HashMap.html#linenum384)
    1. [ ] this.field
    1. [ ] Class then variable (ex: System.out)
    1. [ ] variable chaining (ex: a.b.c.d)
6. [ ] Click on method to get a list of implementations
7. [ ] Click on Override takes you to nearest super class's method that was overridden
8. [x] [File list in root directory /OdinCodeBrowser/jdk8](https://josephmate.github.io/OdinCodeBrowser/jdk8/)
9. [ ] Click on class definition to get all usages
9. [ ] Click on method definition to get all usages
9. [ ] Click on variable definition to get all usages
9. [ ] Click on class/interface definition to get all subtypes
9. [ ] Click on method definition to get all overrides and impls
9. [x] Multi repository support (ex: browsing guava but also linking to JDK8)
    1. [x] Repository exposes it's index file as json
    1. [x] Build loads index files, then builds it own index
    1. [x] Create a demo using current project OdinCodeBrowser -> apache text -> apache commons -> JDK8
        1. Take a look at [SourceHtmlRenderer](https://josephmate.github.io/OdinCodeBrowser/odin.code.browser/SourceHtmlRenderer.html)
        2. In there [I use apache commons text to handle the html escaping for me](https://josephmate.github.io/OdinCodeBrowser/odin.code.browser/SourceHtmlRenderer.html#linenum145)
        3. From SourceHtmlRenderer can navigate to the [StringEscapeUtils.escapeHtml4 implementation](https://josephmate.github.io/OdinCodeBrowser/commons-text_1.9/org/apache/commons/text/StringEscapeUtils.html#linenum660)
        4. escapeHtml4 uses
           [CharSequenceTranslator.translate](https://josephmate.github.io/OdinCodeBrowser/commons-text_1.9/org/apache/commons/text/translate/CharSequenceTranslator.html#linenum84)
        5. Ignore not being able to navigate to the overloaded method. Odin doesn't support that yet.
        6. Validate.isTrue which is in apache commons lang3
        7. Again ignore that it went to the wrong overload of Validate.isTrue
        7. Which throws IllegalArgumentException which allows you to navigate to jdk8!
9. [ ] Nice syntax highlighting somehow without javascript!
     1. [x] Make it look decent on mobile (IE: not tiny text)
     1. [x] Some syntax highlighting
     2. A dark mode syntax highlighting on
         1. [x] pick a colour scheme: [vim-dichromatic](https://github.com/romainl/vim-dichromatic)
         1. [x] comments
         1. [ ] keywords (public, static, record, class, etc)
         1. [x] Types
         1. [x] functions
         1. [ ] variables
10. [ ] Render javadoc as text like in Intellij
10. [ ] Link to native code
10. [ ] navigation comparison between Odin, IDE, and GitHub


