module com.quickzee.common {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // --- Add this line ---
    requires mysql.connector.j;
    // ---------------------

    exports com.quickzee.common.gui; // Assuming you fixed the previous error
    opens com.quickzee.common.gui to javafx.fxml;
    exports com.quickzee.common.util;
    opens com.quickzee.common.model to javafx.base, javafx.fxml;
}