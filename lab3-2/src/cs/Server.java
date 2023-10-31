package cs;



import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {
    public static void main(String[] args) {
        int port = 1255; // 服务器端口

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("服务端已启用，等待客户端");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // 创建一个新线程来处理客户端请求
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private DataAccessLayer dal;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.dal = new DataAccessLayer(); // 创建数据访问层对象
    }

    @Override
    public void run() {
        try (
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        ) {
            while (true) {
                // 从客户端读取请求
                String request = (String) in.readObject();

                if (request.equals("add")) {
                    addContact(in, out);
                } else if (request.equals("view")) {
                    viewContacts(out);
                } else if (request.equals("update")) {
                    updateContact(in, out);
                } else if (request.equals("delete")) {
                    deleteContact(in, out);
                } else if (request.equals("exit")) {
                    // 处理退出请求
                    break;
                }
            }

            clientSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addContact(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        String id = (String) in.readObject();
        String name = (String) in.readObject();
        String address = (String) in.readObject();
        String phone = (String) in.readObject();

        String response = dal.addContact(id, name, address, phone);
        out.writeObject(response);
    }

    private void viewContacts(ObjectOutputStream out) throws IOException {
        List<String> contacts = dal.viewContacts();
        out.writeObject(contacts);
    }

    private void updateContact(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        String id = (String) in.readObject();
        String newName = (String) in.readObject();
        String newAddress = (String) in.readObject();
        String newPhone = (String) in.readObject();

        String response = dal.updateContact(id, newName, newAddress, newPhone);
        out.writeObject(response);
    }

    private void deleteContact(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        String id = (String) in.readObject();
        String response = dal.deleteContact(id);
        out.writeObject(response);
    }
}
