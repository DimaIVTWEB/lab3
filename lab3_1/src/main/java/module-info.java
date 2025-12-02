module io.jfxdevelop.lab3_1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens io.jfxdevelop.lab3_1 to javafx.fxml;
    exports io.jfxdevelop.lab3_1;
}