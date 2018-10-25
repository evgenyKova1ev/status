package objects;

import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

public class Driver implements Serializable {

    private String id;
    private SimpleStringProperty carNumber;
    private SimpleStringProperty name;
    private SimpleStringProperty date;
    private SimpleStringProperty status;

    private Driver() {

    }

    public Driver(String id,String carNumber, String name, String date, String status) {
        this.id = id;
        this.carNumber = new SimpleStringProperty(carNumber);
        this.name = new SimpleStringProperty(name);
        this.date = new SimpleStringProperty(date);
        this.status = new SimpleStringProperty(status);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCarNumber() {
        return carNumber.get();
    }

    public void setCarNumber(String carNumber) {
        this.carNumber.set(carNumber);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public SimpleStringProperty carNumberProperty() {
        return carNumber;
    }
}
