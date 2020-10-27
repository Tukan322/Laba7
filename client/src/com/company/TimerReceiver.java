package com.company;

import java.io.IOException;
import java.util.concurrent.*;

public class TimerReceiver {

    public static void lessTimedReceiver(){
        final Runnable stuffToDo = new Thread(() -> {
            try {
                Message mes = (Message) Deserializer.deserialize(Client.receive());
                Client.setMessage(mes.msg);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println(Client.getMessage());
            System.out.flush();
            Client.setIsGot(true);
        });
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future future = executor.submit(stuffToDo);
        executor.shutdown(); // This does not cancel the already-scheduled task.

        try {
            future.get(100, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException | ExecutionException ie) {
          System.out.println("Ex");
        } catch (TimeoutException te) {
            Client.setMessage(null);
        }
    }

    public static void timedReceiver(){
        final Runnable stuffToDo = new Thread(() -> {
            try {
                Message mes = (Message) Deserializer.deserialize(Client.receive());
                Client.setMessage(mes.msg);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println(Client.getMessage());
            Client.setIsGot(true);
        });
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future future = executor.submit(stuffToDo);
        executor.shutdown(); // This does not cancel the already-scheduled task.

        try {
            future.get(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException | ExecutionException ie) {
            //a
        } catch (TimeoutException te) {
            Client.setMessage(null);
        }
    }
}
