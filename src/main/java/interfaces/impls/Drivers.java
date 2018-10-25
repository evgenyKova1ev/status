package interfaces.impls;

import interfaces.TableDrivers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import objects.Driver;

public class Drivers implements TableDrivers {

    private ObservableList<Driver> driverList = FXCollections.observableArrayList();

    @Override
    public void add(Driver driver) {
        driverList.add(driver);
    }

    @Override
    public void delete(Driver driver) {
        driverList.remove(driver);
    }

    public ObservableList<Driver> getDriverList() {
        return driverList;
    }
}
