# Odin Code Browser

This project attempts to create a completely static website of your source code,
implementing navigate using anchors and links. The goal is to be as good as
navigating code as in Intellij.

# Motivation
I used grepcode at least once a week and I am sad to see it go.
I hope that I can create a spiritual successor by using this tool, then hosting
the result on github pages.

# Features implemented
1. [ ] Line numbers with links
2. [x] Code is copy and pastable
3. [ ] Navigate to Class on Class Usage
    1. [ ] Variable type
    2. [ ] Method return type
    3. [ ] Extends type
    4. [ ] Implement type
    4. [ ] Annotation (the implementation of the annotation)
4. [ ] Navigate to Method of method call
5. [ ] Click on variable to get list of usages and definition on a new page
6. [ ] List implementations of an interface
7. [ ] Click on a method from an abstract type or interface type gives you a
   list of implementations to choose (no idea how i'll do this nicely without
javascript. Maybe an intermediate page with the list of options?)
8. [ ] Click on Override takes you to nearest super class impl
9. [ ] Syntax highlighting somehow without javascript!
10. [ ] Multi repository support (ex: browsing guava but also linking to JDK8)



## Implementation Plan

1. [x] Experiment with javaparser see if it will work.
    - It was really easy to visit method names and get their line numbers
2. [x] Convert the files to html with line numbers
    1. [x] just write the contents vanilla
    2. [x] respect the new lines and spacing
    3. [x] look at how github adds line numbers to their code
        - they put all the code in lines and a table
        - 1 row per line, 1 row has two cells
        - first cell is the line number
        - second cell is the line text
        - the cell doesn't contain any text.....
        - there is no link on the line number so they must be using javascript
        - doesn't look like i can use this technique.
    4. [x] find another example
        - another site uses two large divs.
        - one large div for all the line numbers
        - second div for the code
        - inside each of the divs, there's one div per line
        - this one i found too challenging since you need to use absolute
          positioning and set style top: const*row px
        - where const is some constant related to font height and row is the row
          number
    5. [x] stack overflow research
        - https://stackoverflow.com/a/32039435/1810962 makes first column
          uncopyable
        - realized that if I put that on the <a href>, it should work for my
          usecase
    4. [x] add line numbers
    5. [x] copy and paste should work on the code
3. [ ] Add anchor links for all the line numbers
4. [ ] Preprocess all files extracting line numbers of all Classes. Keep them in
   a map
    - Try this on JDK8 and see if it scales. If not switch to sqlite db.
5. [ ] Run over all files again, recording starting and end position of all
   class usages.
6. [ ] While writing the source files, check if we're at the position of a Class
   Type Reference, and add a link to the line number
    - step 5 and 6 should be combined to work file by file
    - by adding the links, we should achieve a poor version of syntax
      highlighting

