package com.company;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException{
        Organization org = null;
        int id = -1;
        int ind;
        String checker;
        Scanner reader = new Scanner(System.in);
        int port;
        String msg;
        System.out.println("Введите порт на котором будет принимать клиент");
        while (true) {
            try {
                port = Integer.parseInt(reader.nextLine());
                break;
            }
            catch (NumberFormatException numberFormatException){
                System.out.println("Неверный ввод. попробуйте снова");
            }
        }
        Client.create(port);
        System.out.println("Клиент запущен");
        boolean isFirst = true;
        while (true) {
            Client.send(new Message("Client connected", port));
            TimerReceiver.timedReceiver();
            msg = Client.getMessage();

            if (msg != null) break;
            System.out.println("Ответ от сервера отсутствует");
        }


        while (true) {

            Client.showMessage();
            if (!Client.getIsGot() && !isFirst) {
                System.out.println("Ответ от сервера отсутсвует.");
            }
            String sending = reader.nextLine();
            try{
                ind = sending.indexOf(' ');
                checker = sending.substring(0, ind);
                id = Integer.parseInt(sending.substring(ind + 1));
                System.out.println(id);
            }
            catch (IndexOutOfBoundsException ex){
                checker = sending;
            }
            catch (NumberFormatException ex){
                ex.printStackTrace();
                checker = sending;
                id = -1;
            }
            switch (checker) {

                case ("add"):org = new Organization();
                    break;
                case ("update"): {
                    org = new Organization();
                    while (true) {
                        if (id == -1) {
                            System.out.println("Неверный ввод id. Попробуйте снова");
                            id = Integer.parseInt(reader.nextLine());
                        } else {
                            org.setId(id);
                            break;
                        }
                    }
                    break;
                }
            }
            System.out.println();
            if (org == null) Client.send(new Message(sending, port));
            else
            Client.send(new Message(sending, org, port));
            if (sending.equals("exit")) System.exit(1);
            isFirst = false;
        }
    }
}
