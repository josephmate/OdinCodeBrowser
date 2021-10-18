# Odin Code Browser

This project attempts to create a completely static website of your source code,
implementing navigate using anchors and links. The goal is to be as good as
navigating code as in Intellij.

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
3. Each page is static, so it can be efficiently host hosted

# How Do I Use This
1. Go to the [list of all JDK classes](https://josephmate.github.io/OdinCodeBrowser/jdk8/)
2. Find your class (ex: my favourite [HashMap](https://josephmate.github.io/OdinCodeBrowser/jdk8//java/util/HashMap.html))
3. Start reading code and navigating by clicking on the links

# Alternatives
Your Java IDE can do a much better job. As I was write this project, I
use Intellij to navigate from my projects code to the code of the projects I
depend on. The experience is so much better in the IDE and you should use that.
If you are already using your IDE, keep using it!

If you don't use an IDE then github provides some support for navigating code without any extra effort.

The two features together that sets Odin apart from an IDE and GitHub:
1. [x] sharing links to the code (can't do this in an IDE). Since it's webpage,
   this means you can use it on low memory devices like your phone.
2. [ ] can link to the dependant sources as well (github can't do this)

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
| Navigate to dependencies | TODO | ✅  | ❌     |
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
    3. [x] Scoping rules (if names are duplicated in mulitple scopes, need to use the closest scope)
    4. [x] [String literal method calls (ex: "true".equals(blah))](https://josephmate.github.io/OdinCodeBrowser/jdk8/com/sun/beans/finder/BeanInfoFinder.html)
    4. [x] [class literal method calls (ex: boolean.class.getName())](https://josephmate.github.io/OdinCodeBrowser/jdk8/com/sun/beans/finder/PrimitiveTypeMap.html#linenum54)
5. [ ] Click on variable to the definition of that variable
6. [ ] Click on method to get a list of implementations
7. [ ] Click on Override takes you to nearest super class's method that was overridden
8. [x] [File list in root directory /OdinCodeBrowser/jdk8](https://josephmate.github.io/OdinCodeBrowser/jdk8/)
9. [ ] Click on class definition to get all usages
9. [ ] Click on method definition to get all usages
9. [ ] Click on variable definition to get all usages
9. [ ] Click on class/interface definition to get all subtypes
9. [ ] Click on method definition to get all overrides and impls
10. [ ] Multi repository support (ex: browsing guava but also linking to JDK8)
9. [ ] Nice syntax highlighting somehow without javascript!
     1. [x] Some syntax highlighting
     2. A dark mode syntax highlighting on
         1. [ ] javadoc
         1. [ ] keywords (public, static, record, class, etc)
         1. [x] Types
         1. [x] functions
         1. [ ] variables
10. [ ] Render javadoc as text like in Intellij
10. [ ] Link to native code
10. [ ] navigation comparison between Odin, IDE, and GitHub


