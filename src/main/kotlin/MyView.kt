import tornadofx.*

class MyView: View() {
    override val root = vbox {
        button("Click")
        label("Label")
    }
}