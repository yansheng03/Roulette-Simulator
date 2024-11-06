package Roulette

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.{FXMLLoader, FXMLView, NoDependencyResolver}
import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafx.scene.image.Image

object Main extends JFXApp {

  val rootResource = getClass.getResourceAsStream("/view/RouletteGameStart.fxml")
  // initialize the loader object.
  val loader = new FXMLLoader(null, NoDependencyResolver)
  // Load root layout from fxml file.
  loader.load(rootResource);
  // retrieve the root component BorderPane from the FXML
  val roots = loader.getRoot[jfxs.layout.Pane]


  stage = new PrimaryStage {
    title = "Casino Roulette Game"
    icons += (new Image(getClass.getResourceAsStream("/images/RouletteIcon.png")))
    scene = new Scene {
      root = roots
    }
  }
}


