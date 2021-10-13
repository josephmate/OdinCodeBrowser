# Odin Code Browser

This project attempts to create a completely static website of your source code,
implementing navigate using anchors and links. The goal is to be as good as
navigating code as in Intellij.

# Motivation
I used grepcode at least once a week and I am sad to see it go.
I hope that I can create a spiritual successor by using this tool, then hosting
the result on github pages.

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
        7. [ ] Types through wildcard imports
        8. [ ] Types within same package (no import)
        8. [ ] Types from java.lang (ex: RuntimeException)
        8. [ ] Types within same file
4. [ ] Click on method to go the definition of that method
5. [ ] Click on variable to the definition of that variable
6. [ ] Click on method to get a list of implementations
7. [ ] Click on Override takes you to nearest super class that was overridden
8. [ ] File list in root directory /OdinCodeBrowser/jdk8
9. [ ] Click on class definition to get all usages
9. [ ] Click on method definition to get all usages
9. [ ] Click on variable definition to get all usages
9. [ ] Click on class/interface definition to get all subtypes
9. [ ] Click on method definition to get all overrides and impls
9. [x] Nice syntax highlighting somehow without javascript!
10. [ ] Multi repository support (ex: browsing guava but also linking to JDK8)



