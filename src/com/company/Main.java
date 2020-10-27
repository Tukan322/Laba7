package com.company;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Main {
    static ExecutorService executor = Executors.newFixedThreadPool(10);
    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, NoSuchAlgorithmException {
        interactive inter = new interactive();
        interactive.mode();

    }
}
