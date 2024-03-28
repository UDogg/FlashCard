import scalafx.application.JFXApp
import scalafx.scene.{Scene, control => sfx}
import scalafx.scene.layout.VBox
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.stage.Stage

object FlashcardGUI extends JFXApp {

  FlashcardController.loadFlashcards() // Load flashcards at startup

  stage = new JFXApp.PrimaryStage {
    title = "Flashcard App"
    scene = new Scene {
      content = new VBox {
        padding = Insets(20)
        spacing = 10
        children = Seq(
          new sfx.Button("Add New Flashcard") {
            onAction = (e: ActionEvent) => showNewFlashcardDialog()
          },
          // Placeholder for flashcards list
          createFlashcardsListView()
        )
      }
    }
  }

  def createFlashcardsListView(): VBox = {
    val flashcardsBox = new VBox {
      spacing = 10
    }
    refreshFlashcardsListView(flashcardsBox)
    flashcardsBox
  }

  def refreshFlashcardsListView(vbox: VBox): Unit = {
    vbox.children.clear()
    FlashcardController.getFlashcards.foreach { flashcard =>
      val flashcardLabel = new sfx.Label(s"${flashcard.getQuestion}\n${flashcard.getAnswer}") {
        style = "-fx-border-color: black; -fx-padding: 10;"
        maxWidth = 300
        wrapText = true
      }
      vbox.children.add(flashcardLabel)
    }
  }

  def showNewFlashcardDialog(): Unit = {
    val dialog = new sfx.TextInputDialog(defaultValue = "") {
      initOwner(stage)
      title = "New Flashcard"
      headerText = "Create a new Flashcard"
      contentText = "Question:"
    }

    val result = dialog.showAndWait()

    result match {
      case Some(question) =>
        val answerDialog = new sfx.TextInputDialog(defaultValue = "") {
          initOwner(stage)
          title = "Answer for Flashcard"
          headerText = "Enter the answer for your flashcard"
          contentText = "Answer:"
        }

        val answerResult = answerDialog.showAndWait()

        answerResult match {
          case Some(answer) =>
            FlashcardController.addFlashcard(question, answer)
            // Refresh the list view with the new flashcard
            stage.scene().content.headOption.foreach {
              case vbox: VBox => refreshFlashcardsListView(vbox.children(1).asInstanceOf[VBox])
              case _ =>
            }
          case None =>
        }
      case None =>
    }
  }
}
