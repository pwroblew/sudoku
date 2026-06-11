# Command line Sudoku solver

This is a kind of variation of mine of the RockTheJVM's Sudoku solver. 
In this version however the application is an `IOApp` application, everything is purely functional and the implementation of the solver is slightly different than in the original repository.

Links:
- https://www.youtube.com/watch?v=KZw0wjSDA6g
- https://rockthejvm.com/articles/a-backtracking-sudoku-solver-in-scala
- https://rockthejvm.com/articles/creating-a-cli-sudoku-solver-with-scala-native


To build, call: `scala-cli compile .`\
To run, call: `scala-cli run . < a-file-with-sudoku.txt`\
Example: `scala-cli run . < src/main/resources/sudoku-simple.txt`


Quite a nice data set with sudoku puzzles that can be used for testing the implementation:
+ https://www.kaggle.com/datasets/radcliffe/3-million-sudoku-puzzles-with-ratings

