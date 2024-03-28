import io.circe.generic.auto._
import io.circe.parser._
import scala.io.Source
import scala.util.Try

object FlashcardController {
  private var flashcards: List[Flashcard] = List()

  // Ensure IDs are unique by starting from the highest ID found in the existing list
  private def getNextId: Int = flashcards.map(_.getId).foldLeft(0)((acc, id) => Math.max(acc, id)) + 1

  def addFlashcard(question: String, answer: String): Unit = {
    val newId = getNextId // Get the next unique ID
    val newFlashcard = new Flashcard(newId, question, answer) // Use the constructor that sets the ID
    flashcards = flashcards :+ newFlashcard
    FlashcardPersistence.saveFlashcards(flashcards) // Save every time a new card is added
  }

  def getFlashcards: List[Flashcard] = flashcards

  // Load existing flashcards from storage
  def loadFlashcards(): Unit = {
    flashcards = FlashcardPersistence.loadFlashcards()
  }

  def updateFlashcard(id: Int, newQuestion: String, newAnswer: String): Unit = {
    flashcards = flashcards.map { flashcard =>
      if (flashcard.getId == id) new Flashcard(id, newQuestion, newAnswer) // Retain the original ID
      else flashcard
    }
    FlashcardPersistence.saveFlashcards(flashcards) // Save after updating
  }

  def deleteFlashcard(id: Int): Unit = {
    flashcards = flashcards.filterNot(_.getId == id)
    FlashcardPersistence.saveFlashcards(flashcards) // Save after deleting
  }

  def importFlashcardsFromJson(filePath: String): Unit = {
    val fileContents = Try(Source.fromFile(filePath).getLines.mkString).getOrElse("")
    decode[List[Flashcard]](fileContents) match {
      case Right(cards) =>
        // Ensure that newly imported cards do not conflict with existing IDs
        val maxExistingId = getNextId - 1
        cards.foreach(card => if (card.getId <= maxExistingId) card.setId(getNextId))
        flashcards = flashcards ++ cards
        FlashcardPersistence.saveFlashcards(flashcards)
      case Left(error) => println(s"Failed to parse JSON: $error")
    }
  }
}
