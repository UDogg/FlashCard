import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ScrollPane, Label, MenuItem, ContextMenu, TextInputDialog}
import scalafx.scene.layout.VBox
import scalafx.geometry.Insets

object AppGUI extends JFXApp {

  FlashcardController.loadFlashcards() // Ensure flashcards are loaded at startup

  val flashcardList = new VBox()
  val scrollPane = new ScrollPane {
    content = flashcardList
    prefHeight = 350
    prefWidth = 300
  }

  private def refreshFlashcards(): Unit = {
    flashcardList.children.clear()
    FlashcardController.getFlashcards.foreach { flashcard =>
      val label = new Label {
        text = s"${flashcard.question}\n${flashcard.answer}"
        style = "-fx-border-color: black; -fx-padding: 10;"
        maxWidth = 280
        wrapText = true
      }

      // Context menu for edit and delete
      val contextMenu = new ContextMenu()
      val deleteItem = new MenuItem("Delete")
      deleteItem.onAction = _ => {
        FlashcardController.deleteFlashcard(flashcard.getId)
        refreshFlashcards()
      }

      val editItem = new MenuItem("Edit")
      editItem.onAction = _ => showEditFlashcardDialog(flashcard)

      contextMenu.items.addAll(editItem, deleteItem)
      label.contextMenu = contextMenu

      flashcardList.children.add(label)
    }
  }

  private def showEditFlashcardDialog(flashcard: Flashcard): Unit = {
    val dialog = new TextInputDialog(flashcard.question + "|" + flashcard.answer) {
      initOwner(stage)
      title = "Edit Flashcard"
      headerText = "Edit the question and answer."
      contentText = "Format: question|answer"
    }

    dialog.showAndWait().foreach { input =>
      input.split("\\|") match {
        case Array(question, answer) =>
          FlashcardController.updateFlashcard(flashcard.getId, question.trim, answer.trim)
          refreshFlashcards()
        case _ => // Handle error or invalid format
      }
    }
  }

  private def showAddFlashcardDialog(): Unit = {
    val dialog = new TextInputDialog("") {
      initOwner(stage)
      title = "Add Flashcard"
      headerText = "Enter the question and answer."
      contentText = "Format: question|answer"
    }

    dialog.showAndWait().foreach { input =>
      input.split("\\|") match {
        case Array(question, answer) =>
          FlashcardController.addFlashcard(question.trim, answer.trim)
          refreshFlashcards()
        case _ => // Handle error or invalid format
      }
    }
  }

  stage = new PrimaryStage {
    title = "Flashcard App"
    scene = new Scene {
      content = new VBox {
        padding = Insets(20)
        children = Seq(
          scrollPane,
          new Button("New Flashcard") {
            onAction = _ => showAddFlashcardDialog()
          }
        )
      }
    }
  }

  refreshFlashcards() // Refresh to display any existing flashcards
}
