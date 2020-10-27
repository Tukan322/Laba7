package com.company;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;



public class interactive implements Runnable {
    public static ArrayList<Integer> portArray = new ArrayList<>();
    public static void mode() throws IOException, ClassNotFoundException, SQLException, NoSuchAlgorithmException {
        ArrayList<Organization> orgs = new ArrayList<>();
        String workLogin;
        Server.create();
        System.out.println("Сервер активен");
        DBworking.ConnectionToDB();
        Message obj = (Message) Deserializer.deserialize(Server.receive());
        Server.setPort(obj.port);
        Server.send("Сервер был запущен");
        workLogin = Server.authorize(Server.getPort());
        orgs = DBworking.getFromDb(orgs);
        String o;
        String checker;
        String checker2;
        int id = 0;
        int vId;
        String fileName;
        /*try {
            fileName = System.getenv("fileName");
            int N = Commands.countFile(fileName);
            String[] input = Commands.execute(fileName, N);
            String[] vInput = new String[13];
            for (int i = 0; i < N; i++) {
                if (!(i % 13 == 12)) {
                    vInput[i % 13] = input[i];
                } else {
                    orgs.add(new Organization(id, orgs, vInput));
                    id += 1;
                }
            }
        } catch (IndexOutOfBoundsException | NullPointerException ex) {
            System.out.println("ошибка автоматического заполнения из указанного файла");
        }
        */
        String type;
        while (true) {
            boolean isAuthorized=false;
            System.out.println();
            System.out.println("Ожидаю команду.");
            System.out.println(Server.getPort());
            Server.send("Введите комманду. help для справки");
            obj = (Message) Deserializer.deserialize(Server.receive());
            Server.setPort(obj.port);
            for (Integer ports:portArray){
                if (obj.port == ports) isAuthorized = true;
            }
            if (!isAuthorized) {
                Server.authorize(obj.port);
            }
            o = obj.msg;

            System.out.println(o);
            try {
                int split = o.indexOf(' ');
                checker = o.substring(0, split);
                checker2 = o.substring(split + 1);
            } catch (IndexOutOfBoundsException ex) {
                checker2 = "";
                checker = o;
            }
            switch (checker) {
                case ("Client connected"):{
                    workLogin = Server.authorize(Server.getPort());
                }
                case ("spam"):
                    for (int i = 0; i < 200; i++)
                        Server.send("spam");
                    break;
                case ("help"):
                    Commands.help();
                    break;
                case ("info"):
                    Commands.info(orgs);
                    break;
                case ("show"):
                    Server.send("Выполняется команда show");
                    for (Organization org : orgs) {
                        Commands.show(org);
                    }
                    break;
                case ("add"): {
                    boolean isEnd = false;
                    Organization org = new Organization();
                    org.setName(obj.fields[0]);
                    org.setCoordinatesX(Integer.parseInt(obj.fields[1]));
                    org.setCoordinatesY(Integer.parseInt(obj.fields[2]));
                    org.setCreationDate(Date.valueOf(LocalDate.now()));
                    org.setAnnualTurnover(Integer.parseInt(obj.fields[3]));
                    org.setFullName(obj.fields[4]);
                    for (Organization oneOrg : orgs) {
                        if (org.getFullName().equals(oneOrg.getFullName())) {
                            Server.send("Введенное полное имя уже занято. Повторите попытку");
                            isEnd = true;
                        }
                    }
                    if (isEnd) break;
                    org.setEmployeesCount(Long.parseLong(obj.fields[5]));
                    switch (obj.fields[6]) {
                        case ("TRUST"):
                            org.setType(OrganizationType.TRUST);
                            break;
                        case ("GOVERNMENT"):
                            org.setType(OrganizationType.GOVERNMENT);
                            break;
                        case ("PRIVATE_LIMITED_COMPANY"):
                            org.setType(OrganizationType.PRIVATE_LIMITED_COMPANY);
                            break;
                        case ("OPEN_JOINT_STOCK_COMPANY"):
                            org.setType(OrganizationType.OPEN_JOINT_STOCK_COMPANY);
                            break;
                        default:
                            Server.send("");
                    }
                    org.setStreet(obj.fields[7]);
                    org.setTown(obj.fields[8]);
                    org.setX(Float.parseFloat(obj.fields[9]));
                    org.setY(Long.parseLong(obj.fields[10]));
                    org.setZ(Float.parseFloat(obj.fields[11]));
                    if (DBworking.addToDb(org, workLogin)) {
                        org.setId(DBworking.getSqlId(org));
                        orgs.add(org);
                    }

                    id += 1;
                    break;
                }
                case ("update"): {
                    try {
                        boolean isEnd = false;
                        vId = Integer.parseInt(obj.fields[12]);
                        Organization org = new Organization();
                        org.setId(vId);
                        org.setName(obj.fields[0]);
                        org.setCoordinatesX(Integer.parseInt(obj.fields[1]));
                        org.setCoordinatesY(Integer.parseInt(obj.fields[2]));
                        org.setCreationDate(Date.valueOf(LocalDate.now()));
                        org.setAnnualTurnover(Integer.parseInt(obj.fields[3]));
                        org.setFullName(obj.fields[4]);
                        for (Organization oneOrg : orgs) {
                            if (org.getFullName().equals(oneOrg.getFullName())) {
                                Server.send("Введенное полное имя уже занято. Повторите попытку");
                                isEnd = true;
                            }
                        }
                        if (isEnd) break;
                        org.setEmployeesCount(Long.parseLong(obj.fields[5]));
                        switch (obj.fields[6]) {
                            case ("TRUST"):
                                org.setType(OrganizationType.TRUST);
                                break;
                            case ("GOVERNMENT"):
                                org.setType(OrganizationType.GOVERNMENT);
                                break;
                            case ("PRIVATE_LIMITED_COMPANY"):
                                org.setType(OrganizationType.PRIVATE_LIMITED_COMPANY);
                                break;
                            case ("OPEN_JOINT_STOCK_COMPANY"):
                                org.setType(OrganizationType.OPEN_JOINT_STOCK_COMPANY);
                                break;
                            default:
                                Server.send("");
                        }
                        org.setStreet(obj.fields[7]);
                        org.setTown(obj.fields[8]);
                        org.setX(Float.parseFloat(obj.fields[9]));
                        org.setY(Long.parseLong(obj.fields[10]));
                        org.setZ(Float.parseFloat(obj.fields[11]));
                        orgs.remove(vId);
                        DBworking.removeById(vId);
                        orgs.add(org);
                        DBworking.addToDb(org, workLogin);
                        id += 1;
                        break;
                    } catch (IndexOutOfBoundsException | NumberFormatException ex) {
                        Server.send("Неверный ввод id или элемент с таким id отсутствует. Попробуйте снова.");
                    }
                    break;
                }
                case ("remove_by_id"):
                    try {
                        boolean k = true;
                        vId = Integer.parseInt(checker2);
                        Organization wrong = new Organization();
                        for (Organization org : orgs)
                            if (vId == org.getId()) {
                                wrong = org;
                                k = false;
                            }
                        assert workLogin != null;
                        if (workLogin.equals(DBworking.isThisUser(vId))){

                        if (k) Server.send("Элемент с таким id отсутствует");
                            else {
                                orgs.remove(wrong);
                                DBworking.removeById(vId);
                        }
                        }
                        else {
                            Server.send("Вы не можете удалить объект, который не принадлежит вам");
                        }
                    } catch (IndexOutOfBoundsException | NumberFormatException ex) {
                        Server.send("Неверный ввод id или элемент с таким id отсутствует. Попробуйте снова.");
                    }
                    break;
                case ("clear"):
                    DBworking.clearTable();
                    orgs.clear();
                    break;
                case ("save"): {
                    //
                }
                break;
                case ("execute_script"):
                    fileName = checker2;
                    orgs = Commands.executeScript(orgs, id, fileName);
                    orgs.sort(Comparator.comparing(Organization::getId));
                    for (Organization org : orgs)
                        id = org.getId();
                    id += 1;
                    break;
                case ("exit"):
                    System.out.println("Клиент отключен");
                    break;
                case ("remove_first"):{
                    int lessID= Integer.MAX_VALUE;
                    for (Organization org:orgs){
                        if (org.getId() < lessID) lessID = org.getId();
                    }
                    try {
                        orgs.remove(lessID);
                        DBworking.removeById(lessID);
                    } catch (IndexOutOfBoundsException ex) {
                        Server.send("Произошла ошибка. Невозможно удалить первый элемент в пустой коллекции");
                    }
                }
                    break;
                case ("reorder"):
                    Collections.reverse(orgs);
                    break;
                case ("sort"):
                    orgs.sort(Comparator.comparing(Organization::getId));
                    break;
                case ("remove_all_by_type"):
                    type = checker2;
                    DBworking.removeByType(type);
                    ArrayList<Organization> copy = new ArrayList<>();
                    for (Organization org : orgs) {
                        if (!org.getStringType().equals(type)) {
                            copy.add(org);
                        }

                        orgs = copy;
                    }
                    break;
                case ("filter_less_than_annual_turnover"):
                    int anTurn = Integer.parseInt(checker2);

                    for (Organization org : orgs) {
                        try {
                            if (org.getAnnualTurnover() < anTurn)
                                Commands.show(org);
                            String isContinue = ((Message) Deserializer.deserialize(Server.receive())).msg;
                            if (isContinue.equals("n")) break;
                        } catch (NullPointerException ex) {
                            //
                        }
                    }

                    break;
                case ("filter_greater_than_employees_count"):
                    int emCount = Integer.parseInt(checker2);
                    for (Organization org : orgs) {
                        if (org.getEmployeesCount() > emCount)
                            Commands.show(org);
                        String isContinue = ((Message) Deserializer.deserialize(Server.receive())).msg;
                        if (isContinue.equals("n")) break;
                    }
                    break;
                case ("ShowId"):
                    Server.send(String.valueOf(id));
                    break;
                case ("StrTypes"):
                    for (Organization org : orgs) Server.send(org.getStringType());
                    break;
                default: {
                    if (!o.equals("Client connected")) {
                        System.out.println("Ошибка ввода");
                        Server.send("Ошибка ввода.");
                    }
                }
            }
            //isFirst = false;
        }

    }

    @Override
    public void run() {
        try {
            mode();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}