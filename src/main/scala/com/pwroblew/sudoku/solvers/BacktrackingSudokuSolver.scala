package com.pwroblew.sudoku.solvers

import com.pwroblew.sudoku.Sudoku
import com.pwroblew.sudoku.SudokuSolver

import scala.annotation.tailrec

class BacktrackingSudokuSolver extends SudokuSolver {
  override def solve(sudoku: Sudoku): List[Sudoku] = {

    @tailrec
    def loop(pending: List[Sudoku], solutions: List[Sudoku]): List[Sudoku] = {

      pending match {
        case Nil            => solutions
        case sudoku :: rest =>

          sudoku.firstEmptyCellIndex match {
            case None      => loop(rest, sudoku :: solutions)
            case Some(idx) => loop(sudoku.newBoards(idx) ++ rest, solutions)
          }
      }

    }

    loop(List(sudoku).filter(_.isValid), Nil)
  }

  override def solveOne(sudoku: Sudoku): Option[Sudoku] = {

    @tailrec
    def loop(pending: List[Sudoku]): Option[Sudoku] = {

      pending match {
        case Nil            => None
        case sudoku :: rest =>

          sudoku.firstEmptyCellIndex match {
            case None      => Some(sudoku)
            case Some(idx) => loop(sudoku.newBoards(idx) ++ rest)
          }
      }

    }

    loop(List(sudoku).filter(_.isValid))
  }

}
