package com.company;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;

public class TimerReceiver {
    static Scanner reader = new Scanner(System.in);
    public static void lessTimedReceiver(ArrayList<Organization> orgs){
        final Runnable stuffToDo = new Thread(() -> {

        });
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future future = executor.submit(stuffToDo);
        executor.shutdown(); // This does not cancel the already-scheduled task.

        try {
            future.get(10000, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException | ExecutionException ie) {
            //a
        } catch (TimeoutException te) {

        }
    }
}
