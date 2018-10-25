package controllers;

import connection.Message;
import connection.ServerConnector;
import interfaces.impls.Drivers;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import objects.Driver;

import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

public class MainController {

    Drivers drivers;

    static ServerConnector serverConnector;

    static Driver currentDriver;

    @FXML
    private TableColumn<Driver, String> columnCarNumber;

    @FXML
    private TableColumn<Driver, String> columnFIO;

    @FXML
    private TableColumn<Driver, String> columnData;

    @FXML
    private TableColumn<Driver, String> columnStatus;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

    @FXML
    private TableView tableDrivers;

    private Parent fxmlStatus;
    private FXMLLoader fxmlLoaderStatus = new FXMLLoader();
    private StatusHistoryController statusHistoryController;
    private Stage statusStage;

    private Parent fxmlEdit;
    private FXMLLoader fxmlLoaderEdit = new FXMLLoader();
    private EditDialogController editDialogController;
    private Stage editStage;


    @FXML
    private void initialize() {

        try {
            fxmlLoaderStatus.setLocation(getClass().getResource("/statusHistory.fxml"));
            fxmlStatus = fxmlLoaderStatus.load();
            statusHistoryController = fxmlLoaderStatus.getController();

            fxmlLoaderEdit.setLocation(getClass().getResource("/edit.fxml"));
            fxmlEdit = fxmlLoaderEdit.load();
            editDialogController = fxmlLoaderEdit.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }

        drivers = new Drivers();


        serverConnector = new ServerConnector(drivers, this, editDialogController);

        new Thread(serverConnector).start();

        columnCarNumber.setCellValueFactory(new PropertyValueFactory<>("carNumber"));
        columnFIO.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnData.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        serverConnector.getQueue().add(new Message("selectAllDrivers"));

        tableDrivers.setItems(drivers.getDriverList());

        createRowFactory();
    }

    public void createRowFactory() {

        tableDrivers.setRowFactory(tv -> {

            TableRow<Driver> row = new TableRow<Driver>() {
                @Override
                public void updateSelected(boolean selected) {
                    super.updateSelected(selected);
                    Driver driver = this.getItem();

                    if (driver != null && driver.getStatus() != null) {
                        if (driver.getStatus().equals("проблема")) {
                            this.setStyle("-fx-background-color:orangered");
                        } else
                            this.setStyle("");
                    } else {
                        this.setStyle("");
                    }
                }
            };

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Driver rowData = row.getItem();

                    MainController.currentDriver = rowData;

                    try {
                        if (statusStage == null) {
                            statusStage = new Stage();
                            statusStage.setScene(new Scene(fxmlStatus));
                            statusStage.initModality(Modality.WINDOW_MODAL);
                            statusStage.setResizable(false);
                            statusStage.setMaxWidth(500);
                            statusStage.setMaxHeight(600);
//                        stage.initOwner(((Node)event.getSource()).getScene().getWindow());
                            statusHistoryController.setCurrentStage(statusStage);
                        }

                        statusHistoryController.setDriver(rowData);
                        statusHistoryController.getDriverHistory();

                        statusStage.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            return row;
        });
    }

    public void actionButtonPressed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        Button clickedButton = (Button) source;

        if (editStage == null) {
            try {
                editStage = new Stage();
                editStage.setResizable(false);
                editStage.setScene(new Scene(fxmlEdit));
                editStage.initModality(Modality.WINDOW_MODAL);
                editStage.initOwner(((Node)actionEvent.getSource()).getScene().getWindow());
                editDialogController.setCurrentStage(editStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        switch (clickedButton.getId()) {
            case "btnAdd":
                editStage.setTitle("Добавление нового водителя");
                editStage.show();
                editDialogController.clearNotice();
                break;
            case "btnDelete":
                editStage.setTitle("Удаление водителя");
                editStage.show();
                editDialogController.clearNotice();
                break;
        }
    }

    private javafx.event.EventHandler<WindowEvent> closeEventHandler = event -> System.exit(1);

    public javafx.event.EventHandler<WindowEvent> getCloseEventHandler(){
        return closeEventHandler;
    }

}
