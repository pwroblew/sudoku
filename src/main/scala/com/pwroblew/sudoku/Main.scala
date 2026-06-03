import cats.syntax.all._
import cats.effect.{IO, IOApp, ExitCode}

object Main extends IOApp.Simple {

  override def run: IO[Unit] = {

    for {
      input <- IO.blocking(
        scala.io.Source.stdin.mkString
      )
      sudokuOrError = Sudoku.fromString(input)
      _ <- sudokuOrError match {

        case Left(error) =>
          IO.println(s"Error parsing Sudoku: ${error.getMessage}")
            *> IO.raiseError(new Exception("Failed to parse Sudoku"))

        case Right(sudoku) =>
          val solver = new BacktrackingSudokuSolver
          val solutions = solver.solveOne(sudoku).toList
          IO.println("Parsed Sudoku:")
            *> IO.println(sudoku.toString)
            *> IO.println(s"Found ${solutions.size} solution(s):")
            *> solutions.traverse_(solution => IO.println(solution.toString))

      }
    } yield ()

  }
}
