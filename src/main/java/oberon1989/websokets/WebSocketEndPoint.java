package oberon1989.websokets;

import lombok.RequiredArgsConstructor;
import oberon1989.server.ServerControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@ServerEndpoint("/input")

public class WebSocketEndPoint {

    @Autowired
    ServerControl control=null;

    @OnOpen
    public void OnOpen(Session session) throws IOException {
        control.addConsumer(session);
        session.getBasicRemote().sendText("Connected...");

    }

    @OnClose
    public void OnClose(Session session) throws IOException {
      control.removeConsumer(session);
      session.close();
    }

    public void SendBroadcast(String msg) throws IOException {
       control.sendTextAllConsumers(msg);
    }
}
