import cats.syntax.all._

class BacktrackingSudokuSolver extends SudokuSolver {
  override def solve(sudoku: Sudoku): List[Sudoku] = {

    def loop(pending: List[Sudoku], solutions: List[Sudoku]): List[Sudoku] = {

      pending.headOption match {
        case None         => solutions
        case Some(sudoku) =>

          sudoku.firstEmptyCellIndex match {
            case None      => loop(pending.tail, List(sudoku) ++ solutions)
            case Some(idx) =>
              val row = idx / 9
              val col = idx % 9
              val newSudokus: IndexedSeq[Sudoku] = for {
                num <- 1 to 9
                newSudoku = sudoku.set(row, col, num)
              } yield newSudoku

              loop(
                newSudokus.filter(_.isValid).toList ++ pending.tail,
                solutions
              )
          }
      }

    }

    loop(List(sudoku).filter(_.isValid), Nil)
  }

  override def solveOne(sudoku: Sudoku): Option[Sudoku] = {

    def loop(pending: List[Sudoku]): Option[Sudoku] = {

      pending.headOption match {
        case None         => None
        case Some(sudoku) =>

          sudoku.firstEmptyCellIndex match {
            case None      => Some(sudoku)
            case Some(idx) =>
              val row = idx / 9
              val col = idx % 9
              val newSudokus: IndexedSeq[Sudoku] = for {
                num <- 1 to 9
                newSudoku = sudoku.set(row, col, num)
              } yield newSudoku

              loop(
                newSudokus.filter(_.isValid).toList ++ pending.tail
              )
          }
      }

    }

    loop(List(sudoku).filter(_.isValid))
  }

}
