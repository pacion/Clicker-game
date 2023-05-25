module com.example.pio {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens com.example.pio to javafx.fxml;
    exports com.example.pio;
    exports com.example.pio.upgrade;
    opens com.example.pio.upgrade to javafx.fxml;
    exports com.example.pio.upgrade.perclick;
    opens com.example.pio.upgrade.perclick to javafx.fxml;
    exports com.example.pio.upgrade.persecond;
    opens com.example.pio.upgrade.persecond to javafx.fxml;
}