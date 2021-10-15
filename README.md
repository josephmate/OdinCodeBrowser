# Odin Code Browser

This project attempts to create a completely static website of your source code,
implementing navigate using anchors and links. The goal is to be as good as
navigating code as in Intellij.

# Motivation
I used grepcode at least once a week and I am sad to see it go.
I hope that I can create a spiritual successor by using this tool, then hosting
the result on github pages.

# Alternatives
Your Java IDE can do a much better job. As I was write this project, I
use Intellij to navigate from my projects code to the code of the projects I
depend on. The experience is so much better in the IDE and you should use that.

If you don't use an IDE then github provides some support for navigating code without any extra effort.
At the moment it is superior to this tool and you should use that.

The two features together I hope to acheieve that that will set Odin apart from an IDE and Github:
1. sharing links to the code (can't do this in an IDE)
2. can link to the dependant sources as well (github can't do this)

# Design Philosophy 
1. No javascript
2. No javascript!!!
3. Each page is static, so it can be efficiently host hosted

# How Do I Use This
1. Go to the [list of all JDK classes](https://josephmate.github.io/OdinCodeBrowser/jdk8/)
2. Find your class (ex: my favourite [HashMap](https://josephmate.github.io/OdinCodeBrowser/jdk8//java/util/HashMap.html))
3. Start reading code and navigating by clicking on the links

# Features implemented
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
    1. [ ] Static and only one function with the name
    1. [ ] Object instance and only one function with the name and not in super class
    1. [ ] Object instance and only one function with the name but in some super class
    1. [ ] Chained method calls
    2. [ ] Functions are overloaded (different parameters)
    3. [ ] Functions within the same file
    3. [ ] Functions on this.method()
    4. [x] [String literal method calls like ("true".equals(blah))](https://josephmate.github.io/OdinCodeBrowser/jdk8/com/sun/beans/finder/BeanInfoFinder.html)
    4. [x] [class literal method calls like (FileInputStream.class.equals(in.getClass())))](https://josephmate.github.io/OdinCodeBrowser/jdk8/java/nio/channels/Channels.html#linenum350)
5. [ ] Click on variable to the definition of that variable
6. [ ] Click on method to get a list of implementations
7. [ ] Click on Override takes you to nearest super class's method that was overridden
8. [x] [File list in root directory /OdinCodeBrowser/jdk8](https://josephmate.github.io/OdinCodeBrowser/jdk8/)
9. [ ] Click on class definition to get all usages
9. [ ] Click on method definition to get all usages
9. [ ] Click on variable definition to get all usages
9. [ ] Click on class/interface definition to get all subtypes
9. [ ] Click on method definition to get all overrides and impls
9. [x] Nice syntax highlighting somehow without javascript!
10. [ ] Multi repository support (ex: browsing guava but also linking to JDK8)
10. [ ] Link to native code


