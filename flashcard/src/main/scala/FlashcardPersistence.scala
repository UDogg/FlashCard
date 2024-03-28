import io.circe.syntax._
import io.circe.parser._
import io.circe.generic.auto._
import java.nio.file.{Files, Paths, Path}
import scala.util.{Failure, Success, Try}

object FlashcardPersistence {
  // Assuming the `flashcards.json` file is in the root directory of your project
  private val filename: String = "flashcards.json"
  private val path: Path = Paths.get(filename)

  def saveFlashcards(flashcards: List[Flashcard]): Unit = {
    val json = flashcards.asJson.noSpaces
    Try(Files.write(path, json.getBytes)) match {
      case Success(_) => println("Flashcards saved successfully.")
      case Failure(exception) => println(s"Failed to save flashcards: ${exception.getMessage}")
    }
  }

  def loadFlashcards(): List[Flashcard] = {
    Try(new String(Files.readAllBytes(path))) match {
      case Success(jsonString) => decode[List[Flashcard]](jsonString).toOption.getOrElse {
        println("Failed to decode flashcards from JSON.")
        List.empty
      }
      case Failure(_) =>
        println("Flashcards file not found. A new file will be created upon saving.")
        List.empty
    }
  }
}
