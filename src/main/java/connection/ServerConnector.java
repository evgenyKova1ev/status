package connection;

import controllers.EditDialogController;
import controllers.MainController;
import interfaces.impls.Drivers;
import javafx.collections.ObservableList;
import objects.Driver;
import objects.Status;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ServerConnector implements Runnable {

    private EditDialogController editDialogController;

    private MainController mainController;

    private Drivers drivers;

    private ObservableList<Status> historyList;

    private SocketAddress address;

    private Socket socket;

    private ObjectOutputStream objOut;

    private ObjectInputStream objIn;

    public final BlockingDeque<Message> queue = new LinkedBlockingDeque<>();

    public ServerConnector(Drivers drivers, MainController mainController, EditDialogController editDialogController) {
        this.address = new InetSocketAddress("192.168.0.104", 9351);
        this.drivers = drivers;
        this.mainController = mainController;
        this.editDialogController = editDialogController;
    }

    @Override
    public void run() {
        openConnection();

        new Thread(new Ping()).start();

        Message message = new Message();

        while (true) {
            try {
                message = queue.take();
                objOut.writeObject(message);
                objOut.flush();
            } catch (Exception e) {
                e.printStackTrace();
                if (message.getMsg() != null) {
                    if (message.getMsg().equals("add") || message.getMsg().equals("delete"))
                        editDialogController.changeNotice("ERROR");
                }
                reconnect();
            }
        }
    }

    private void openConnection() {
        try {
            socket = new Socket();

            socket.connect(address);

            objOut = new ObjectOutputStream(socket.getOutputStream());

            objIn = new ObjectInputStream(socket.getInputStream());

            new Thread(new Reader()).start();

            queue.add(new Message("selectAllDrivers"));

        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (socket != null)
                    socket.close();
                if (objOut != null)
                    objOut.close();
                if (objIn != null)
                    objIn.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void reconnect() {
        try {
            objOut.close();
            objIn.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        openConnection();
    }

    private class Reader implements Runnable {

        @Override
        public void run() {
            try {

                while (!Thread.currentThread().isInterrupted()) {
                    Message message = (Message) objIn.readObject();

                    switch (message.getMsg()) {
                        case "drivers":
                            selectAllDrivers(message);
                            break;
                        case "driverHistory":
                            getDriverHistory(message);
                            break;
                        case "newDriver":
                            addNewDriverInDriversList(message);
                            break;
                        case "deleteDriver":
                            deleteDriverFromDriversList(message);
                            break;
                        case "status":
                            updateStatus(message);
                            break;
                        default:
                            break;
                    }

                    if (message.getMsg().equals("addERROR") || message.getMsg().equals("deleteERROR"))
                        editDialogController.changeNotice("ERROR");

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                reconnect();
            }
        }
    }

    private class Ping implements Runnable {
        @Override
        public void run() {

            while (true) {

                try {

                    Thread.sleep(60_000);
                    queue.add(new Message());

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void updateStatus(Message message) {
        Message.Data data = message.getData().get(0);

        for (Driver driver : drivers.getDriverList()) {
            if (driver.getId().equals(data.getId())) {
                driver.setDate(data.getDate());
                driver.setStatus(data.getStatus());
            }
        }

//        mainController.changeColor();
    }

    private void addNewDriverInDriversList(Message message) {
        Message.Data data = message.getData().get(0);
        drivers.getDriverList().add(new Driver(data.getId(), data.getCarNumber(), data.getName(), data.getDate(), data.getStatus()));
        editDialogController.changeNotice("addOK");
    }

    private void deleteDriverFromDriversList(Message message) {
        Message.Data data = message.getData().get(0);
        drivers.getDriverList().removeIf(driver -> driver.getName().equals(data.getName()));
        editDialogController.changeNotice("addDelete");
    }

    private void getDriverHistory(Message message) {
        for (Object object : message.getData()) {
            Message.Data data = (Message.Data) object;
            historyList.add(new Status(data.getDate(), data.getStatus()));
        }
        historyList = null;
    }

    private void selectAllDrivers(Message message) {
        drivers.getDriverList().clear();

        for (Object object : message.getData()) {
            Message.Data data = (Message.Data) object;
            drivers.getDriverList().add(new Driver(data.getId(), data.getCarNumber(), data.getName(), data.getDate(), data.getStatus()));
        }
//        mainController.changeColor();
    }

    public void updateDrivers(ObservableList<Driver> list) {
//        String response = sendRequest("updateDrivers!" + list.size());
//
//        String[] drivers = response.split("/");
//
//        if (drivers[0].split("!").length == 3) {
//            for (Data driver : list) {
//                for (String arrDriver : drivers) {
//                    String[] tmp = arrDriver.split("!");
//                    if (tmp[0].equals(driver.getId())) {
//                        if (!tmp[2].equals(driver.getStatus())) {
//                            driver.setDate(tmp[1]);
//                            driver.setStatus(tmp[2]);
//                        }
//                    }
//                }
//            }
//        }

    }

    private Message sendRequest(String message) {
        Socket socket = new Socket();
        Message msg = new Message();

        try {
            socket.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream())) {

            objOut.writeObject(new Message(message));
            objOut.flush();

            msg = (Message) objIn.readObject();

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return msg;
    }

    public BlockingDeque<Message> getQueue() {
        return queue;
    }

    public void setHistoryList(ObservableList<Status> historyList) {
        this.historyList = historyList;
    }

    public void setEditDialogController(EditDialogController editDialogController) {
        this.editDialogController = editDialogController;
    }
}
