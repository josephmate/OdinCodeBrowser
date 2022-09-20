# Odin Code Browser

This project attempts to create a completely static website of your source code,
that navigates all the way down to the depths of the JDK.
The goal is to navigate as well as Intellij.
Try it yourself by navigating from
[Apache Commons Text's
StringEscapeUtils](https://josephmate.github.io/OdinCodeBrowserRepos/commons-text_1.9/org/apache/commons/text/StringEscapeUtils.html#linenum38).

# Motivation
I used 
[grepcode](https://web.archive.org/web/20150318024036/http://www.grepcode.com/)
at least once a week and I am sad to see it go.
It's navigation was almost as good as an IDE.
You could navigate from apache commons all the way down to the JDK.
Occasionally, I would forget what I was originally doing and get lost in the depths of the JDK.
I hope that I can create a spiritual successor by using this tool,
that will live much longer since the source code is available,
and the resulting static web pages are decentralized.

# Design Philosophy 
1. Each page is static
2. Works well without javascript

# How Do I Use This

## As a code browser
1. Find a project you want to browse from:
   [jdk8](https://josephmate.github.io/OdinCodeBrowserJdk8/),
   [jdk11](https://josephmate.github.io/OdinCodeBrowserJdk11/),
   [jdk17](https://josephmate.github.io/OdinCodeBrowserJdk17/),
   [Apache Lang3](https://josephmate.github.io/OdinCodeBrowserRepos/commons-lang3_3.11/),
   [Apache Collections](https://josephmate.github.io/OdinCodeBrowserRepos/commons-collections_4.4/),
   [Apache Text](https://josephmate.github.io/OdinCodeBrowserRepos/commons-text_1.9),
   [Github Java Parser](https://josephmate.github.io/OdinCodeBrowserRepos/javaparser-core_3.23.1)
   [OdinCodeBrowser](https://josephmate.github.io/OdinCodeBrowser/odin.code.browser)
2. Find your class (ex: A class Odin uses 
   [StringEscapeUtils](https://josephmate.github.io/OdinCodeBrowserRepos/commons-text_1.9/org/apache/commons/text/StringEscapeUtils.html))
3. Start reading code and navigating by clicking on the links, which are
   underlined.

## As a code owner
```
git clone git@github.com:josephmate/OdinCodeBrowser.git
cd OdinCodeBrowser
args="--inputSourceDirectory <path_to_your_source>"
args="$args --outputDirectory <output_directory>"
args="$args --webPathToCssFile https://josephmate.github.io/OdinCodeBrowser/css/styles.css"
args="$args --webPathToSourceHtmlFiles <directory_on_webserver>"
args="$args --languageLevel JAVA_16"
args="$args --urlToDependantIndexJson <url_of_dependencies_index_json_file>"
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
args="$args --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowserJdk8/index.json"
args="$args --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowserRepos/javaparser-core_3.23.1/index.json"
args="$args --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowserRepos/commons-lang3_3.11/index.json"
args="$args --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowserRepos/commons-text_1.9/index.json"
args="$args --urlToDependantIndexJson https://josephmate.github.io/OdinCodeBrowserRepos/commons-collections_4.4/index.json"
mvn install exec:java \
  -Dexec.mainClass=Main \
  -Dexec.args="$args"
```

## Fun and games
1. Start at a random class in [jdk8](https://josephmate.github.io/OdinCodeBrowserJdk8/)
2. Using links only, how fast can you get my favourite class: HashMap?

For example I did
[I18NImpl](https://josephmate.github.io/OdinCodeBrowserJdk8/com/sun/imageio/plugins/common/I18NImpl.html)
->
[PropertyResourceBundle](https://josephmate.github.io/OdinCodeBrowserJdk8/java/util/PropertyResourceBundle.html#linenum122)
->
[HashMap](https://josephmate.github.io/OdinCodeBrowserJdk8/java/util/HashMap.html#linenum137)

However, sometimes you'll pick a class that does not have a path to HashMap :(.

# Alternatives
Your Java IDE can do a much better job.
As I write this project, I use Intellij to navigate from my project's code to the code of the projects I depend on.
The experience is so much better in the IDE and you should use that.
If you are already using your IDE, keep using it!

If you don't use an IDE then github provides some support for navigating code without any extra effort.
Just commit your code as you normally do and github updates the navigation.

The two features together that sets Odin apart from an IDE and GitHub:
1. [x] sharing links to the code (can't do this in an IDE). Since it's a web page,
   this means you can use it on low memory devices like your phone!
2. [x] can link to the dependant sources as well (github can't do this)

# Comparison
| Dimension                | Odin | IDE | Github |
| ------------------------ | ---- | --- | ------ |
| Shares links             | ✅   | ❌  | ✅     |
| Minimal CPU Usage        | ✅   | ❌  | ✅     |
| Minimal Memory Usage     | ✅   | ❌  | ✅     |
| Minimal Storage Usage    | ✅   | ❌  | ✅     |
| Realtime                 | ❌   | ✅  | ❌     |
| Automatically Applied    | ❌   | ✅  | ✅     |
| Static Pages             | ✅   |     | ?      |
| Works without javascript | ✅   |     | ❌     |
| Navigate to dependencies | ✅   | ✅  | ❌     |
| Comment on code          | ❌   | ✅  | ✅     |
| Navigation Complete      | ❌   | ✅  | ❌     |



Explanation of criteria:

* **Shares links**: In Odin and GitHub you can share links to any line in the code.
  An IDE does not let you do that.
* **Minimal CPU/Memory/Storage Usage**: Since Odin and GitHub are not realtime, no cpu, memory or
  disk space is needed by the client to efficiently calculate all the indexes.
* **Realtime** As you make changes in an IDE, the indexes are updated and
  you can navigate to the new code changes or new dependencies. For GitHub you
  need to commit and push before the navigation updates. For Odin you need to
  run this tool and publish the generated html files.
* **Automatically Applied**: By rebuilding in the IDE, new code is recognized.
  By committing and pushing your changes to GitHub, the navigation is updated.
  For Odin, you must manually build and publish the static web pages. You
  unfortunately need to manage this. GitHub and an IDE manage this for you with
  no effort on your part.
* **Static Pages**: Odin's output is hosted as static web pages that can be
  efficiently distributed. This comparison doesn't make sense for an IDE.
* **No javascript**: Odin doesn't use javascript while GitHub needs to.
* **Navigate to dependencies**: IDE and seamlessly navigate to your
  dependencies. Odin plans to navigate to dependencies when you build. It will
  not be as convenient as an IDE. GitHub only lets you navigate within the same
  repository.
* **Navigation Complete**: anything you could possibly want to navigate to or
  from in an IDE works. Odin is working on matching an IDE and might reach the
  same level one day. GitHub navigation is okay. It gives you a list of options
  as a pop. Odin tries to be like an IDE and take you directly to the
  definition of the variable or method without asking you which one. For now,
  Odin sometimes makes mistakes due to method overloading.
* **Comment on code**: GitHub is probably the best for commenting and discussing
  code. You can also do this in an IDE with plugins, but it's not as convenient
  as GitHub. Since Odin pages are static without javascript, it's impossible to
  have commenting.


## Other Alternatives

These are alternatives that I do not use on a daily basis so I do not feel qualified in giving a thoughtful review.
However, I'm going to help you find what you're looking for.

**[woboq](https://code.woboq.org/)** : Looks exactly what I'm trying to achieve, but for C++ instead of Java.
If you're working on open source C/C++ , I recommend checking it out.

**Opengrok**: Provides much better searching than what Odin can provide.
However, as a result some components cannot be hosted as static files.
If you host Opengrok for your project, I recommend it.

**[Sourcegraph](https://sourcegraph.com/searh)**:
The search is really good.
It like a super powered Github search.
However, [they aren't indexing OdinCodeBrowser yet.](https://sourcegraph.com/search?q=context:global+repo:%5Egithub%5C.com/josephmate/.*+&patternType=literal).
I'm not sure what triggers the indexing because some of my old side projects are there.
Also, it does not support cross repo navigation.

# How it Works

1. I use [Github's JavaParser](https://github.com/javaparser/javaparser) to
   visit nodes in the Java AST.
2. Through visiting, I build indexes of classes, their methods, their fields,
   and their super classes.
3. Through one more visit, I look to see if there's anything in the index I can create
   navigation links to and place these into a 'Rendering Queue'.
4. I iterate over the code char by checking if I need to insert anything.
5. All of these are saved as HTML files.
6. The index is saved as a json file for other repos to use. That way they do
   not have to checkout the source code and build the index of all the
   dependencies. It also allows the repositories to independently host
   their source code and allows dependents to navigate from their code host
   on their servers to the code hosted on the dependency's servers.

# Color Scheme

The color scheme is based on [vim-dichromatic](https://github.com/romainl/vim-dichromatic)
put together by [Romain Lafourcade](https://github.com/romainl).
I wanted a single color scheme that could be used by anyone, 
since it is really difficult for readers to change the styles.
In order for someone to change the style, they will need to install a plugin for
their browser and override Odin's css styles.
Maybe in the future there will be some javascript to select from a list of
styles that writes to your browser's local storage.

# Future Work

Below is a checklist of features Odin needs.

1. [x] Line numbers with links
2. [ ] Code is easily copy and pastable
    1. [x] Code is copy and pastable 
    2. [ ] Copied code is exactly the same as pasted code
3. [x] Click on a Type to navigate to the definition of that type
    1. Type
        1. [x] Interface
        1. [x] Annotation
        1. [x] Outer class
        2. [x] Inner class
        3. [x] Enum
        3. [x] Record
        3. [ ] Generics
        3. [ ] Class inside function
        4. [ ] Anonymous class
    1. Type Usage
        1. [x] [Variable type](https://josephmate.github.io/OdinCodeBrowserJdk8/com/oracle/net/Sdp.html#linenum56)
        2. [x] [Method return type](https://josephmate.github.io/OdinCodeBrowserJdk8/com/oracle/net/Sdp.html#linenum104)
        3. [x] [Extends type](http://josephmate.github.io/OdinCodeBrowserJdk8/com/oracle/net/Sdp.html#linenum95)
        4. [x] Implement type
        4. [x] Record
        4. [x] Enum
        3. [ ] Generics
        5. [ ] Annotation (the implementation of the annotation)
        6. [ ] Import
        7. [x] [Types through wildcard imports (ex: List from java.util.*)](https://josephmate.github.io/OdinCodeBrowserJdk8/java/util/concurrent/ScheduledThreadPoolExecutor.html#linenum785)
        8. [x] [Types within same package (ex: ThreadPoolExecutor when in package java.util.concurrent)](https://josephmate.github.io/OdinCodeBrowserJdk8/java/util/concurrent/ScheduledThreadPoolExecutor.html#linenum122)
        8. [x] [Types from java.lang (ex: IllegalArgumentException)](http://josephmate.github.io/OdinCodeBrowserJdk8/java/util/HashMap.html#linenum448)
        8. [ ] Types within same file
4. [ ] Click on method to go the definition of that method
    1. [x] [Static and only one function with the name (ex: Objects.hashCode(key))](http://josephmate.github.io/OdinCodeBrowserJdk8/java/util/HashMap.html#linenum296)
    3. [x] [Functions within the same file (ex: putMapEntries(m, true)](http://josephmate.github.io/OdinCodeBrowserJdk8/java/util/HashMap.html#linenum784)
    3. [x] [Functions on this (ex: this.getCanonName())](http://josephmate.github.io/OdinCodeBrowserJdk8/java/net/SocketPermission.html#linenum621)
    1. [x] [Object instance and only one function with the name and not in super class (ex: x.getClass())](http://josephmate.github.io/OdinCodeBrowserJdk8/java/util/HashMap.html#linenum348)
    1. [x] [Object instance and only one function with the name but in some super class](http://josephmate.github.io/OdinCodeBrowserJdk8/java/util/concurrent/locks/ReentrantLock.html#linenum131)
    1. [x] [super.method() (super.clone() in HashMap)](http://josephmate.github.io/OdinCodeBrowserJdk8/java/util/HashMap.html#linenum1318)
    1. [ ] Chained method calls
    2. [ ] Functions are overloaded (different parameters)
        1. [ ] [all arguments are variables (ex: String.lastIndexOf(char vs String))](https://josephmate.github.io/OdinCodeBrowser/odin.code.browser/indexing/ImportVisitor.html#linenum42)
        1. [ ] [length heuristic for generics (ex: Pair.of(A,B) vs Pair.of(Map.Entry<A,B>))](https://josephmate.github.io/OdinCodeBrowser/odin.code.browser/Director.html#linenum44)
        2. [ ] all args are variables or literals (ex add(a, 1))
        3. [ ] where any of the arguments are expressions (ex: add(add(1,2), add(a,b)))
        4. [ ] variable arguments (ex: int add(int... vals)) 
    3. [ ] import static functions
    3. [ ] Record getter functions
    2. [ ] Super constructors
    2. [ ] Overloaded constructors
    3. [ ] Scoping rules (if names are duplicated in multiple scopes, need to use the closest scope)
    4. [x] [String literal method calls (ex: "true".equals(blah))](https://josephmate.github.io/OdinCodeBrowserJdk8/com/sun/beans/finder/BeanInfoFinder.html)
    4. [x] [class literal method calls (ex: boolean.class.getName())](https://josephmate.github.io/OdinCodeBrowserJdk8/com/sun/beans/finder/PrimitiveTypeMap.html#linenum54)
5. [ ] Click on variable to the definition of that variable
    1. [x] [from function param](http://josephmate.github.io/OdinCodeBrowserJdk8/java/util/HashMap.html#linenum345)
    1. [x] [local var](http://josephmate.github.io/OdinCodeBrowserJdk8/java/util/HashMap.html#linenum568)
    1. [x] [from scope (for/while/if)](http://josephmate.github.io/OdinCodeBrowserJdk8/java/util/HashMap.html#linenum352)
    1. [x] [field var](http://josephmate.github.io/OdinCodeBrowserJdk8/java/util/HashMap.html#linenum291)
    1. [x] [static field var](http://josephmate.github.io/OdinCodeBrowserJdk8/java/util/HashMap.html#linenum384)
    1. [ ] Enum value
    1. [ ] this.field
    1. [ ] record.value
    1. [ ] static variable (ex: System.out)
    1. [ ] variable chaining (ex: a.b.c.d)
6. [ ] Click on method to get a list of implementations
7. [ ] Click on Override takes you to nearest super class's method that was overridden
8. [x] [File list in root directory /OdinCodeBrowserJdk8](https://josephmate.github.io/OdinCodeBrowserJdk8/)
9. [ ] Click on class definition to get all usages
9. [ ] Click on method definition to get all usages
9. [ ] Click on variable definition to get all usages
9. [ ] Click on class/interface definition to get all subtypes
9. [ ] Click on method definition to get all overrides and impls
9. [ ] List of functions and fields at the side of the page.
    1. [ ] List of functions within this class, grouped by visibility
    1. [ ] List of fields within this class
    1. [ ] List of functions and fields from super classes
9. [x] Multi repository support (ex: browsing guava but also linking to JDK8)
    1. [x] Repository exposes it's index file as json
    1. [x] Build loads index files, then builds it own index
    1. [x] Create a demo using current project OdinCodeBrowser -> apache text -> apache commons -> JDK8
        1. Take a look at [SourceHtmlRenderer](https://josephmate.github.io/OdinCodeBrowser/odin.code.browser/rendering/source/SourceHtmlRenderer.html)
        2. In there [I use apache commons text to handle the html escaping for me](https://josephmate.github.io/OdinCodeBrowser/odin.code.browser/SourceHtmlRenderer.html#linenum145)
        3. From SourceHtmlRenderer can navigate to the [StringEscapeUtils.escapeHtml4 implementation](https://josephmate.github.io/OdinCodeBrowserRepos/commons-text_1.9/org/apache/commons/text/StringEscapeUtils.html#linenum660)
        4. escapeHtml4 uses
           [CharSequenceTranslator.translate](https://josephmate.github.io/OdinCodeBrowserRepos/commons-text_1.9/org/apache/commons/text/translate/CharSequenceTranslator.html#linenum84)
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
         1. [ ] literals
             1. [x] strings
             1. [ ] numbers
             1. [ ] boolean
             1. [ ] enum values
         1. [ ] native types (boolean, char, int, etc.)
         1. [ ] keywords (public, static, record, class, etc)
             1. [ ] this
             1. [ ] super
             1. [ ] class + modifiers
             1. [ ] modifiers of constructors
             1. [ ] modifiers of fields
             1. [ ] modifiers of functions
             1. [ ] modifiers of params
             1. [ ] modifiers of locals
             1. [ ] for
             1. [ ] synchronized
             1. [ ] import
             1. [ ] switch case
             1. [ ] if
             1. [ ] while
             1. [ ] do
             1. [ ] return
             1. [ ] extends
             1. [ ] implements
         1. [x] Types
         1. [x] function calls
         1. [x] variables
10. [ ] Render javadoc as text like in Intellij
10. [ ] Link to native code
10. [ ] navigation comparison between Odin, IDE, and GitHub
10. [x] Figure out how to support repos with multiple source directories
10. [ ] Figure out a better release process other than having a developer clone the project and use the `mvn exec:java` goal.
    1. [x] Tried fatJar but does not work well with java 16

