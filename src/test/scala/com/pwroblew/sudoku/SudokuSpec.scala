package com.pwroblew.sudoku

import com.pwroblew.sudoku.SudokuParserError
import com.pwroblew.sudoku.solvers.BacktrackingSudokuSolver
import munit.*

class SudokuSpec extends FunSuite {

  val validSudokuString: String =
    """932457816
      |648321579
      |715896324
      |486713952
      |251948763
      |379562481
      |824179635
      |563284197
      |197635248""".stripMargin

  val sudokuOrError: Either[SudokuParserError, Sudoku] =
    Sudoku.fromString(validSudokuString)

  val sudokuOrErrorF: FunFixture[Either[SudokuParserError, Sudoku]] =
    FunFixture(
      _ => Sudoku.fromString(validSudokuString),
      _ => ()
    )

  sudokuOrErrorF.test("Parsing valid Sudoku string") { sudokuOrErr =>
    assert(sudokuOrErr.isRight)
    assertEquals(
      sudokuOrErr.map(_.toString).getOrElse("").filter(_.isDigit),
      validSudokuString.replaceAll("\\s", "")
    )
  }

  val sudokuF: FunFixture[Sudoku] = FunFixture(
    _ =>
      Sudoku
        .fromString(validSudokuString)
        .fold(
          error => fail(s"Failed to parse Sudoku: ${error.message}"),
          sudoku => sudoku
        ),
    _ => ()
  )

  sudokuF.test("Sudoku is valid") { sudoku =>
    assert(sudoku.isValid)
  }

  sudokuF.test("Sudoku status is Solved") { sudoku =>
    assertEquals(sudoku.getStatus, SudokuStatus.Solved)
  }

  sudokuF.test("get(x, y) returns correct value") { sudoku =>
    assertEquals(sudoku.get(0, 0), 9)
    assertEquals(sudoku.get(8, 8), 8)
    assertEquals(sudoku.get(4, 4), 4)
    assertEquals(sudoku.get(2, 5), 6)
    assertEquals(sudoku.get(7, 1), 6)
  }

  val sudokuInvalidString: String =
    """932457816
      |648321579
      |486713952
      |251948763
      |379562481
      |824179635
      |563284197
      |197635248""".stripMargin

  val sudokuInvalidStringF: FunFixture[String] = FunFixture(
    _ => sudokuInvalidString,
    _ => ()
  )

  sudokuInvalidStringF.test("Parsing Sudoku invalid string") { str =>
    val sudokuOrErr = Sudoku.fromString(str)

    assert(sudokuOrErr.isLeft)
    assertEquals(
      sudokuOrErr.fold(error => error.message, _ => ""),
      "Input string must have exactly 81 characters"
    )
  }

  val invalidSudokuString: String =
    """.32457816
      |648321579
      |71.896324
      |486713952
      |251948763
      |379562481
      |824179635
      |563284197
      |197635244""".stripMargin

  val invalidSudokuStringF: FunFixture[
    (Either[SudokuParserError, Sudoku], BacktrackingSudokuSolver)
  ] =
    FunFixture(
      _ =>
        (Sudoku.fromString(invalidSudokuString), new BacktrackingSudokuSolver),
      _ => ()
    )

  invalidSudokuStringF.test("Parsing and trying to solveinvalid Sudoku") {
    case (sudokuOrErr, solver) =>
      assert(sudokuOrErr.isRight)
      assertEquals(
        sudokuOrErr.map(_.getStatus).getOrElse(SudokuStatus.Invalid),
        SudokuStatus.Incomplete
      )
      val solution = sudokuOrErr.flatMap(s =>
        solver.solveOne(s).toRight("No solution found")
      )
      assert(solution.isLeft)
      assertEquals(solution.left.toOption.getOrElse(""), "No solution found")
  }

  val incompleteValidSudokuString: String =
    """93245.816
      |648.21579
      |715896324
      |486710952
      |251948763
      |379..2481
      |824179635
      |56328.197
      |19703.2.8""".stripMargin

  val incompleteValidSudokuStringF: FunFixture[
    (Either[SudokuParserError, Sudoku], BacktrackingSudokuSolver)
  ] =
    FunFixture(
      _ =>
        (
          Sudoku.fromString(incompleteValidSudokuString),
          new BacktrackingSudokuSolver
        ),
      _ => ()
    )

  incompleteValidSudokuStringF.test(
    "Parsing and solving incomplete but valid Sudoku"
  ) { case (sudokuOrErr, solver) =>
    assert(sudokuOrErr.isRight)
    assertEquals(
      sudokuOrErr.map(_.getStatus).getOrElse(SudokuStatus.Invalid),
      SudokuStatus.Incomplete
    )
    val solution1 =
      sudokuOrErr.flatMap(s => solver.solveOne(s).toRight("No solution found"))
    assert(solution1.isRight)
    assertEquals(
      solution1.map(_.toString).getOrElse("").filter(_.isDigit),
      validSudokuString.replaceAll("\\s", "")
    )

    val solution2 = sudokuOrErr.flatMap(s =>
      solver.solve(s).headOption.toRight("No solution found")
    )
    assert(solution2.isRight)
    assertEquals(
      solution2.map(_.toString).getOrElse("").filter(_.isDigit),
      validSudokuString.replaceAll("\\s", "")
    )
  }
}
