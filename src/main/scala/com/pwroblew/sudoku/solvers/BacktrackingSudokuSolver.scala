package com.pwroblew.sudoku.solvers

import com.pwroblew.sudoku.{Sudoku, SudokuSolver}

class BacktrackingSudokuSolver extends SudokuSolver {
  override def solve(sudoku: Sudoku): List[Sudoku] = {

    def loop(pending: List[Sudoku], solutions: List[Sudoku]): List[Sudoku] = {

      pending match {
        case Nil            => solutions
        case sudoku :: rest =>

          sudoku.firstEmptyCellIndex match {
            case None      => loop(rest, sudoku :: solutions)
            case Some(idx) =>loop(_newSudokus(sudoku, idx) ++ rest, solutions)
          }
      }

    }

    loop(List(sudoku).filter(_.isValid), Nil)
  }

  override def solveOne(sudoku: Sudoku): Option[Sudoku] = {

    def loop(pending: List[Sudoku]): Option[Sudoku] = {

      pending match {
        case Nil            => None
        case sudoku :: rest =>

          sudoku.firstEmptyCellIndex match {
            case None      => Some(sudoku)
            case Some(idx) => loop(_newSudokus(sudoku, idx) ++ rest)
          }
      }

    }

    loop(List(sudoku).filter(_.isValid))
  }

  private def _newSudokus(sudoku: Sudoku, idx: Int): List[Sudoku] = {
    val row = idx / 9
    val col = idx % 9
    val newSudokus_ = for {
      num <- 1 to 9
      newSudoku = sudoku.set(row, col, num)
    } yield newSudoku
    newSudokus_.filter(_.isValid).toList
  }

}
