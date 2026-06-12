# Command line Sudoku solver

## Short description

This is a kind of variation of mine of the RockTheJVM's Sudoku solver. 
In this version however the application is an `IOApp` application, everything is purely functional and the implementation of the solver is slightly different than in the original repository.

Links:
- https://www.youtube.com/watch?v=KZw0wjSDA6g
- https://rockthejvm.com/articles/a-backtracking-sudoku-solver-in-scala
- https://rockthejvm.com/articles/creating-a-cli-sudoku-solver-with-scala-native

## Usage

The Sudoku board is read from standard input.
There are two optional parameters: `solver` and `search-type`
Available solvers: `basic` and `improved`.
Available search types: `all` and `one`.

To build, call: 
```
scala-cli compile .
```
To run with `scala-cli`, call: 
```
scala-cli run . -- [solver] [search-type] < a-file-with-sudoku.txt
```

Example: 
```
scala-cli run . -- improved all < src/main/resources/sudoku-simple.txt
```

When packaged with `scala-cli package . --power -o sudoku` one can call: 
```
./sudoku improved all < src/main/resources/sudoku-simple.txt
```


## Input format

Provide an input sudoku as a single line of characters, either digits or a dot. A dot or a zero is treated as an empty cell in the puzzle.

Examples:

```
..3..9........23......5..876.......53.94.....4....523...7.13.....6..8.9...12.....
006000510052070000007610000080000050700002698004007000009260000000090300000000160
```

Quite a nice data set with sudoku puzzles that can be used for testing the implementation can be found here:
+ https://www.kaggle.com/datasets/radcliffe/3-million-sudoku-puzzles-with-ratings

## Algorithm

It is a classic backtracking algorithm. It starts with a one-element list containing the initial Sudoku. Then a Sudoku is taken from the list, a missing cell in the sudoku is found, and then new Sudoku boards are created by filling that cell with possible values. New Sudoku boards are added back to the list of boards still to explore. If none of new Sudoku boards are created out of the given sudoku, then the 'branch' is discarded and a next Sudoku board is taken from the list for processing. The procedure repeats until there are no boards left to explore. When a board has no empty cells and is valid, it is added to the list of solutions.

### BacktrackingSudokuSolver - `basic` solver

In this implementation, in each step the first board from the list is taken, and the first missing cell is chosen to generate new Sudoku boards. Then newly created boards are added at the beginning of the list and the procedure repeats.

### BacktrackingSudokuSolverImproved - `improved` solver

The idea is that instead of the first missing cell, the one with the lowest number of possible values is chosen.
