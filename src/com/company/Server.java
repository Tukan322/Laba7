package com.company;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class Server {

    static int port;
    public static DatagramChannel datagramChannel;

    public static String authorize(int port) throws IOException, ClassNotFoundException, SQLException, NoSuchAlgorithmException {
        Message obj;
        MessageDigest md = MessageDigest.getInstance("MD5");
        while (true) {
            boolean isBreak = false;
            Server.send("Вы зарегестрированы? y-да n-нет");
            obj = (Message) Deserializer.deserialize(Server.receive());
            if (obj.port != port)
                while (obj.port != port){
                    Server.setPort(obj.port);
                    Server.send("Происходит авторизация другого клиента, ожидайте.");
                    obj = (Message) Deserializer.deserialize(Server.receive());
                }
            Server.setPort(obj.port);
            switch (obj.msg.toLowerCase()) {
                case ("y"): {
                    Server.send("Введите ваш логин.");
                    obj = (Message) Deserializer.deserialize(Server.receive());
                    if (obj.port != port)
                        while (obj.port != port) {
                            Server.setPort(obj.port);
                            Server.send("Происходит авторизация другого клиента, ожидайте.");
                            obj = (Message) Deserializer.deserialize(Server.receive());
                        }
                    String login = obj.msg;
                    Server.send("Введите ваш пароль.");
                    obj = (Message) Deserializer.deserialize(Server.receive());
                    if (obj.port != port)
                        while (obj.port != port) {
                            Server.setPort(obj.port);
                            Server.send("Происходит авторизация другого клиента, ожидайте.");
                            obj = (Message) Deserializer.deserialize(Server.receive());
                        }
                    Server.setPort(obj.port);
                    md.update(obj.msg.getBytes());
                    byte[] digest = md.digest();
                    String pass = DatatypeConverter.printHexBinary(digest).toUpperCase();
                    System.out.println("Хэш введенного пароля " + pass);
                    if (DBworking.isUser(login, pass)) {
                        System.out.println(true);
                        isBreak = true;
                        interactive.portArray.add(port);
                        return login;
                    } else {
                        Server.send("Ошибка при вводе данных");
                        System.out.println(false);
                    }
                    break;
                }
                case ("n"): {
                    Server.send("Пожалуйста зарегестрируйтесь");
                    Server.send("Введите ваш логин.");
                    obj = (Message) Deserializer.deserialize(Server.receive());
                    if (obj.port != port)
                        while (obj.port != port) {
                            Server.setPort(obj.port);
                            Server.send("Происходит авторизация другого клиента, ожидайте.");
                            obj = (Message) Deserializer.deserialize(Server.receive());
                        }
                    Server.setPort(obj.port);
                    String login = obj.msg;
                    Server.send("Введите ваш пароль.");
                    obj = (Message) Deserializer.deserialize(Server.receive());
                    if (obj.port != port)
                        while (obj.port != port) {
                            Server.setPort(obj.port);
                            Server.send("Происходит авторизация другого клиента, ожидайте.");
                            obj = (Message) Deserializer.deserialize(Server.receive());
                        }
                    Server.setPort(obj.port);
                    md.update(obj.msg.getBytes());
                    byte[] digest = md.digest();
                    String pass = DatatypeConverter.printHexBinary(digest).toUpperCase();
                    System.out.println("Хэш введенного пароля для регистрации " + pass);
                    if (DBworking.isBusy(login)) {
                        Server.send("Логин уже занят. Попробуйте другой");
                        break;
                    }
                    DBworking.addNewUser(login, pass);
                }
                default:
            }
            if (isBreak){
                interactive.portArray.add(port);
                break;
            }
        }
        return null;
    }


    public static void create() throws IOException {
        SocketAddress it = new InetSocketAddress(InetAddress.getLocalHost(), 3344);

        DatagramChannel dc = DatagramChannel.open();
        dc.configureBlocking(false);
        try {
            dc.bind(it);
        } catch (BindException e){
            System.out.println("Порт занят,отключаюсь.");
            System.exit(0);
        }
        datagramChannel = dc;
    }

    public static byte[] receive() {
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(10000);
            byte[] bytes;
            while(true) {
                InetSocketAddress socketAddress = (InetSocketAddress) datagramChannel.receive(byteBuffer);
                if (socketAddress!= null) {
                    byteBuffer.flip();
                    int limit = byteBuffer.limit();
                    bytes = new byte[limit];
                    byteBuffer.get(bytes,0,limit);
                    byteBuffer.clear();
                    return bytes;
                }
            }
        } catch (IOException ignored) { }
        return new byte[0];
    }

    public static void send(String msg) {
        try {
            DatagramChannel datagramChannel = DatagramChannel.open();
            datagramChannel.bind(null);

            SocketAddress serverAddress = new InetSocketAddress(InetAddress.getLocalHost(), port);
            Message o = new Message(msg);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(o);
            objectOutputStream.flush();
            objectOutputStream.close();

            byte[] buff = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            datagramChannel.configureBlocking(false);
            datagramChannel.send(ByteBuffer.wrap(buff), serverAddress);
            datagramChannel.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (msg.equals("exit")) System.exit(1);
    }

    public static void setPort(int p){
        port = p;
    }

    public static int getPort(){
        return port;
    }
}