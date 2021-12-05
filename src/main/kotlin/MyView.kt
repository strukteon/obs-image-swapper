import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.Priority
import tornadofx.*
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*

class MyView: View() {
    private val controller: MyController by inject()

    init {
        title = "OBS Image Swapper"
    }

    override val root = vbox {
        hbox {
            label("Folder: ")
            textfield(controller.selectedFolder)
            button("Choose Folder") {
                action {
                    var dir = chooseDirectory("Select Target Directory") ?: return@action
                    println(dir)
                    controller.selectedFolder.value = dir.absolutePath;
                }
            }
        }
        hbox {
            label("Interval in ms: ")
            textfield(controller.timeInterval)
        }
        hbox {
            button("Start") {
                disableProperty().bind(controller.isRunning)
                action {
                    controller.start()
                }
            }
            button("Stop") {
                disableProperty().bind(!controller.isRunning)
                action {
                    controller.stop()
                }
            }
        }
        hbox {
            label("Current Image: ")
            label(controller.currentImage)
        }
        hbox {
            label("Time remaining: ")
            label(controller.timeRemaining)
            label("ms")
        }
    }
}

class MyController: Controller() {
    private var timer: Timer? = null

    private val timerUpdateInterval = 100

    val timeInterval = SimpleIntegerProperty(500)
    val timeRemaining = SimpleIntegerProperty(-1)
    val isRunning = SimpleBooleanProperty(false)

    val selectedFolder = SimpleStringProperty("C:\\Users\\Nils\\Pictures\\Unsplash")
    val currentImage = SimpleStringProperty("Not started yet")

    fun start() {
        val folder = File(selectedFolder.get())
        val imageFiles = folder.listFiles()
        val fileCount = imageFiles.size
        var fileIndex = -1

        val fileOutput = File("output.jpg")

        println("starting timer")
        isRunning.value = true
        timer = Timer()
        timer!!.scheduleAtFixedRate(object: TimerTask() {
            override fun run() {
                //println("Not yet implemented")
                runAsync {  } ui {
                    timeRemaining.value -= timerUpdateInterval
                    if (timeRemaining < 0) {
                        timeRemaining.value = timeInterval.value
                        fileIndex += 1
                        if (fileIndex == fileCount) fileIndex = 0
                        Files.copy(imageFiles[fileIndex].toPath(), fileOutput.toPath(), StandardCopyOption.REPLACE_EXISTING)
                        currentImage.value = imageFiles[fileIndex].name
                    }
                }
            }
        }, 0, timerUpdateInterval.toLong())
    }

    fun stop() {
        println("stopping timer")
        isRunning.value = false
        timer!!.cancel()
        timer!!.purge()
        timer = null
    }
}