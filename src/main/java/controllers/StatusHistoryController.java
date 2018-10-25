package controllers;

import connection.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import objects.Driver;
import objects.Status;

public class StatusHistoryController {

    private ObservableList<Status> historyList = FXCollections.observableArrayList();

    Driver driver;

    Stage currentStage;

    @FXML
    private TableView tableStatusHistory;

    @FXML
    private TableColumn<Status, String> columnData;

    @FXML
    private TableColumn<Status, String> columnStatus;

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public void setCurrentStage(Stage stage) {
        this.currentStage = stage;
    }


    @FXML
    private void initialize() {
//        setDriver();



        columnData.setCellValueFactory(new PropertyValueFactory<Status, String>("data"));
        columnStatus.setCellValueFactory(new PropertyValueFactory<Status, String>("status"));

        tableStatusHistory.setItems(historyList);

    }

    void getDriverHistory() {
        historyList.clear();
        currentStage.setTitle(driver.getName());
        MainController.serverConnector.setHistoryList(historyList);
        Message message = new Message("driverHistory");
        message.setId(driver.getId());
        MainController.serverConnector.getQueue().add(message);
    }
}
