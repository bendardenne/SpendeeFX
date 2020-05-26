module spendee {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires jfxtras.fxml;
    requires jfxtras.common;
    requires jfxtras.controls;
    requires org.controlsfx.controls;
    requires java.scripting;
    requires jdk.scripting.nashorn;
    requires jfxutils;
    requires java.prefs;

    exports spendee;
    opens spendee;
    opens spendee.ui;
    opens spendee.ui.filters;
    opens spendee.ui.transactions;
    opens spendee.ui.charts;
    opens spendee.ui.icons;
    opens spendee.ui.config;
    opens spendee.model;
    opens spendee.model.filter;
}
