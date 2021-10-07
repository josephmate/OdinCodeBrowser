# Odin Code Browser

This project attempts to create a completely static website of your source code,
implementing navigate using anchors and links. The goal is to be as good as
navigating code as in Intellij.

# Motivation
I used grepcode at least once a week and I am sad to see it go.
I hope that I can create a spiritual successor by using this tool, then hosting
the result on github pages.

# Features implemented
1. [ ] Syntax highlighting somehow without javascript! Or if it's too hard, use
   javascript at first :(
2. [ ] Navigate to Class on Class Usage
    1. [ ] Variable
    2. [ ] Method return type
    3. [ ] Extends
    4. [ ] Implement
    4. [ ] Annotation (the implementation of the annotation)
3. [ ] Navigate to Method of method call
4. [ ] Click on variable to to see next usage of that variable with in the same
   class
    1. if variable is defined in different file, navigate to it instead
5. [ ] List implementations of an interface
6. [ ] Click on a method from an abstract type or interface type gives you a
   list of implementations to choose (no idea how i'll do this nicely without
javascript. Maybe an intermediate page with the list of options?)
7. [ ] Multi repository support (ex: browsing guava but also linking to JDK8)



## Implementation Plan

1. [x] Experiment with javaparser see if it will work.
    - It was really easy to visit method names and get their line numbers
2. [ ] Convert the files to html with line numbers
    1. [ ] just write the contents vanilla
    2. [ ] add line numbers
    3. [ ] copy and paste should work on the code
    4. [ ] syntax highlighting
3. [ ] Add anchor links for all the line numbers
4. [ ] Preprocess all files extracting line numbers of all Classes. Keep them in
   a map
    - Try this on JDK8 and see if it scales. If not switch to sqlite db.
5. [ ] Run over all files again, recording starting and end position of all
   class usages.
6. [ ] While writing the source files, check if we're at the position of a Class
   Type Reference, and add a link to the line number
    - step 5 and 6 should be combined to work file by file
