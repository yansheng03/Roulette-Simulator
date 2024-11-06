The .fxml files have images inside. These images have url for their paths such as <Image url="@/images/RouletteWheel.png" />.
To open the file in scenebuilder so that the images appear as well, 2 dots are needed after @. For example, <Image url="@../images/RouletteWheel.png" />.
Once done editing in scenebuilder, the dots should be deleted and reverted so that the program can launch. This seems to be an issue in scenebuilder not recognizing the file path without these dots.
