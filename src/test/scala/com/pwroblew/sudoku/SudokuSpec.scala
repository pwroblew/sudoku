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

  val sudokuOrError: Either[Throwable, Sudoku] =
    Sudoku.fromString(validSudokuString)

  val sudokuOrErrorF: FunFixture[Either[Throwable, Sudoku]] = FunFixture(
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
    _ => Sudoku.fromString(validSudokuString).fold(
      error => fail(s"Failed to parse Sudoku: ${error.getMessage}"),
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
}
