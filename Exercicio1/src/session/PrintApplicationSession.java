package session;


import model.Status;
import net.sf.appia.core.*;
import net.sf.appia.core.events.channel.ChannelInit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import event.PrintAlarmEvent;
import event.PrintConfirmEvent;
import event.PrintRequestEvent;
import event.PrintStatusEvent;

/**
 * Session implementing the Print Application protocol.
 * <br>
 * Reads strings, requests their printing and displays 
 * confirmations.
 * 
 * @author  akt & kcg
 */
public class PrintApplicationSession extends Session {

  public PrintApplicationSession(Layer layer) {
    super(layer);
  }

  public void handle(Event event) {
    System.out.println();

    if (event instanceof ChannelInit)
      handleChannelInit((ChannelInit) event);
    else if (event instanceof PrintConfirmEvent)
      handlePrintConfirm((PrintConfirmEvent) event);
    else if (event instanceof PrintAlarmEvent)
      handlePrintAlarm((PrintAlarmEvent) event);
    else if (event instanceof PrintStatusEvent)
      handlePrintStatus((PrintStatusEvent) event);
  }

  private PrintReader reader = null;

  private void handleChannelInit(ChannelInit init) {
    try {
      init.go();
    } catch (AppiaEventException ex) {
      ex.printStackTrace();
    }

    if (reader == null)
      reader = new PrintReader(init.getChannel());
  }

  private void handlePrintConfirm(PrintConfirmEvent conf) {
    System.out.println("[PrintApplication: received confirmation of request "
        + conf.getId() + "]");

    try {
      conf.go();
    } catch (AppiaEventException ex) {
      ex.printStackTrace();
    }
  }

  private void handlePrintAlarm(PrintAlarmEvent alarm) {
    System.out.println("[PrintApplication: received ALARM]");

    try {
      alarm.go();
    } catch (AppiaEventException ex) {
      ex.printStackTrace();
    }
  }

  private void handlePrintStatus(PrintStatusEvent status) {
    System.out.print("[PrintApplication: received");
    System.out.print(" status "
        + (status.getStatus().equals(Status.OK) ? "OK" : "NOK"));
    System.out.println(" for request " + status.getId() + "]");

    try {
      status.go();
    } catch (AppiaEventException ex) {
      ex.printStackTrace();
    }
  }

  private class PrintReader extends Thread {

    public boolean ready = false;
    public Channel channel;
    private BufferedReader stdin = new BufferedReader(new InputStreamReader(
        System.in));
    private int rid = 0;

    public PrintReader(Channel channel) {
      ready = true;
      if (this.channel == null)
        this.channel = channel;
      this.start();
    }

    public void run() {
      boolean running = true;

      while (running) {
        ++rid;
        System.out.println();
        System.out.print("[PrintApplication](" + rid + ")> ");
        try {
          String s = stdin.readLine();

          PrintRequestEvent request = new PrintRequestEvent();
          request.setId(rid);
          request.setString(s);
          request.asyncGo(channel, Direction.DOWN);
        } catch (AppiaEventException ex) {
          ex.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }

        try {
          Thread.sleep(1500);
        } catch (Exception ex) {
          ex.printStackTrace();
        }

        synchronized (this) {
          if (!ready)
            running = false;
        }
      }
    }
  }
}
