This is a kind of variation of mine on RockTheJVMs version of Sudoku solver.
Here the whole app is an IOApp, everything is purely functional, and the version
of solver here is also different than in the original repo.

Links:
https://www.youtube.com/watch?v=KZw0wjSDA6g
https://rockthejvm.com/articles/a-backtracking-sudoku-solver-in-scala
https://rockthejvm.com/articles/creating-a-cli-sudoku-solver-with-scala-native


To build call:
scala-cli compile .

To run:
scala-cli run . < a-file-with-sudoku.txt

Example:
scala-cli run . < src/main/resources/sudoku-simple.txt


Quite a nice data set with sudokus:
https://www.kaggle.com/datasets/radcliffe/3-million-sudoku-puzzles-with-ratings

