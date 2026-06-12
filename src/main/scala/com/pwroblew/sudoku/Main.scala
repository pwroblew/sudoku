package com.pwroblew.sudoku

import cats.syntax.all._
import cats.effect.{IO, IOApp, ExitCode}
import solvers.BacktrackingSudokuSolver
import solvers.BacktrackingSudokuSolverImproved
import com.pwroblew.sudoku.SudokuParserError
import com.pwroblew.sudoku.SudokuSolverName
import com.pwroblew.sudoku.SudokuSearchType

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    val solver = new BacktrackingSudokuSolverImproved

    val program = for {
      (solverName, searchType) <- IO.fromEither(
        (args.headOption, args.drop(1).headOption).tupled
          .toRight(
            new Throwable(
              """Usage: sudoku <solver> <search-type>
                                  |       available solvers: basic, improved
                                  |       available search types: all, one""".stripMargin
            )
          )
      )

      solver <- SudokuSolverName.fromString(solverName) match {
        case Some(SudokuSolverName.Basic)    => IO(new BacktrackingSudokuSolver)
        case Some(SudokuSolverName.Improved) =>
          IO(new BacktrackingSudokuSolverImproved)
        case None =>
          IO.raiseError(new Throwable(s"Unknown solver: $solverName"))
      }

      solverMethod <- SudokuSearchType.fromString(searchType) match {
        case Some(SudokuSearchType.All) => IO(solver.solve)
        case Some(SudokuSearchType.One) =>
          IO(solver.solveOne).map(_.andThen(_.toList))
        case None =>
          IO.raiseError(
            new Throwable(s"Unknown search type: $searchType")
          )
      }

      input <- IO.blocking(
        scala.io.Source.stdin.mkString
      )

      sudokuBoard <- IO.fromEither(
        Sudoku
          .fromString(input)
          .leftMap(error =>
            new Throwable(s"Error parsing Sudoku: ${error.message}")
          )
      )

      _ <- IO.println("Parsed Sudoku:")
        *> IO.println(sudokuBoard.toString)

      exitCode <- {
        val solutions = solverMethod(sudokuBoard)
        val outputMessage = solutions.length match {
          case 0 => "No solutions found."
          case 1 => "Found 1 solution:"
          case n => s"Found $n solutions:"
        }
        IO.println(outputMessage)
          *> solutions.traverse_(solution => IO.println(solution.toString))
          *> IO(ExitCode.Success)

      }
    } yield exitCode

    program.handleErrorWith { error =>
      IO.println(s"Error: ${error.getMessage}").as(ExitCode.Error)
    }

  }
}
