package controllers;

import connection.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditDialogController {

    private Stage currentStage;

    private String fullName;

    @FXML
    private Label notice;

    @FXML
    private Button btnOk;

    @FXML
    private Button btnCancel;

    @FXML
    private TextField txtSurname;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPatronymic;

    @FXML
    private TextField txtCarNumber;



    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void actionOk(ActionEvent actionEvent) {

        String surname = txtSurname.getText().trim();
        String name = txtName.getText().trim();
        String patronymic = txtPatronymic.getText().trim();
        String carNumber = txtCarNumber.getText().replace(" ", "");
        String fullName = surname + " " + name + " " + patronymic;
        this.fullName = fullName;


        if (currentStage.getTitle().equals("Добавление нового водителя")) {
            Message message = new Message("add");
            message.setStatus(fullName + "/" + carNumber);
            MainController.serverConnector.getQueue().add(message);
        } else {
            Message message = new Message("delete");
            message.setStatus(fullName);
            MainController.serverConnector.getQueue().add(message);
        }

        txtSurname.setText("");
        txtName.setText("");
        txtPatronymic.setText("");
        txtCarNumber.setText("");
    }

    public void setCurrentStage(Stage stage) {
        this.currentStage = stage;
    }

    public void changeNotice(String status) {
        Platform.runLater(() -> {
            switch (status) {
                case "addOK":
                    notice.setText(fullName + " успешно добавлен.");
                    break;
                case "addDelete":
                    notice.setText(fullName + " успешно удален.");
                    break;
                default:
                    notice.setText("Упс, произошла ошибка :(");
                    break;
            }
        });
    }

    public void clearNotice() {
        notice.setText("");
    }

}
