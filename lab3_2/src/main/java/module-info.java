module io.jfxdevelop.lab3_2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens io.jfxdevelop.lab3_2 to javafx.fxml;
    exports io.jfxdevelop.lab3_2;
}