package com.pwroblew.sudoku

trait SudokuSolver {
  def solve(sudoku: Sudoku): List[Sudoku]
  def solveOne(sudoku: Sudoku): Option[Sudoku]
}
