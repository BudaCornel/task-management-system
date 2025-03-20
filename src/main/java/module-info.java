module com.example.pt2025_30422_buda_cornel_assignment1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;




    opens com.example.pt2025_30422_buda_cornel_assignment1 to javafx.fxml;


    opens com.example.pt2025_30422_buda_cornel_assignment1.userInterface to javafx.graphics;


    exports com.example.pt2025_30422_buda_cornel_assignment1.userInterface;

}
