package com.pwroblew.sudoku

import cats.implicits._

enum SudokuStatus {
  case Incomplete, Solved, Invalid
}

case class SudokuParserError(message: String)

case class Sudoku private (data: Vector[Int]) {

  assert(data.size == 81, "Sudoku must have exactly 81 cells")

  def get(row: Int, col: Int): Int = {
    assert(row >= 0 && row < 9, "Row must be between 0 and 8")
    assert(col >= 0 && col < 9, "Column must be between 0 and 8")
    data(row * 9 + col)
  }

  def set(row: Int, col: Int, value: Int): Sudoku = {
    assert(row >= 0 && row < 9, "Row must be between 0 and 8")
    assert(col >= 0 && col < 9, "Column must be between 0 and 8")
    assert(value >= 0 && value <= 9, "Value must be between 0 and 9")
    copy(data.updated(row * 9 + col, value))
  }

  def firstEmptyCellIndex: Option[Int] = data.indexWhere(_ == 0) match {
    case -1  => None
    case idx => Some(idx)
  }

  def allEmptyCellIndices: List[Int] = data.zipWithIndex.collect {
    case (0, idx) => idx
  }.toList

  override def toString: String = {
    data
      .grouped(27)
      .map(subSegment =>
        subSegment
          .grouped(9)
          .map(row =>
            row
              .grouped(3)
              .map(subRow =>
                subRow
                  .map {
                    case 0 => '.'
                    case n => n.toString
                  }
                  .mkString(" ")
              )
              .mkString(" | ")
          )
          .mkString("\n")
      )
      .mkString("\n------+-------+------\n") +
      "\nStatus: " + getStatus.toString + "\n"
  }

  def getStatus: SudokuStatus = {
    if (data.contains(0)) SudokuStatus.Incomplete
    else if (isValid) SudokuStatus.Solved
    else SudokuStatus.Invalid
  }

  def isValid: Boolean = {

    val rowsValid = (0 until 9).forall { row =>
      val values = (0 until 9).map(col => get(row, col))
      values.count(_ == 0) + values.filter(_ != 0).distinct.size == 9
    }

    val columnsValid = (0 until 9).forall { col =>
      val values = (0 until 9).map(row => get(row, col))
      values.count(_ == 0) + values.filter(_ != 0).distinct.size == 9
    }

    val subgrids = for {
      row <- 0 until 3
      col <- 0 until 3
    } yield (row * 3, col * 3)
    val subgridsValid = subgrids.forall { (row, col) =>
      val values = for {
        r <- 0 until 3
        c <- 0 until 3
      } yield get(row + r, col + c)
      values.count(_ == 0) + values.filter(_ != 0).distinct.size == 9
    }

    rowsValid && columnsValid && subgridsValid
  }

  def newBoards(idx: Int): List[Sudoku] = {
    val row = idx / 9
    val col = idx % 9
    val newBoards_ = for {
      num <- 1 to 9
      newSudoku = this.set(row, col, num)
    } yield newSudoku
    newBoards_.filter(_.isValid).toList
  }

  def newBoards: List[(Int, List[Sudoku])] = {
    allEmptyCellIndices.map(idx => (idx, newBoards(idx)))
  }

  def newBoardsSmallest: Option[(Int, List[Sudoku])] = {
    newBoards.minByOption { case (_, boards) => boards.size }
  }

}

object Sudoku {

  def fromString(s: String): Either[SudokuParserError, Sudoku] = {
    val cleaned = s.replaceAll("\\s", "")
    if (cleaned.length != 81) then
      SudokuParserError("Input string must have exactly 81 characters").asLeft
    else
      cleaned
        .map {
          case '.'            => 0.asRight
          case '0'            => 0.asRight
          case c if c.isDigit => c.asDigit.asRight
          case _              =>
            SudokuParserError(
              "Invalid character in input string"
            ).asLeft
        }
        .toVector
        .sequence
        .map(Sudoku(_))
  }
}

enum SudokuSolverName {
  case Basic, Improved
}

object SudokuSolverName {
  def fromString(s: String): Option[SudokuSolverName] = s.toLowerCase match {
    case "basic"    => Some(SudokuSolverName.Basic)
    case "improved" => Some(SudokuSolverName.Improved)
    case _          => None
  }
}

enum SudokuSearchType {
  case All, One
}

object SudokuSearchType {
  def fromString(s: String): Option[SudokuSearchType] = s.toLowerCase match {
    case "all" => Some(SudokuSearchType.All)
    case "one" => Some(SudokuSearchType.One)
    case _     => None
  }
}
