package oberon1989.server;





import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import lombok.*;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import javax.websocket.Session;
import java.io.*;
import java.util.*;


@Getter
@Setter
@RequiredArgsConstructor
public class ServerControl implements Serializable {
    private int port;
    private String path;
    private String serverJar;
    private  Query query;
    private String screenName;
    private List<Session> webSocketClients;



    public void init()
    {


            path="/home/ubuntu/forge_server/start.sh";
            serverJar="forge-1.7.10-10.13.4.1614-1.7.10-universal.jar";

            port=222;
        }




    public String startServer()
    {
        if(!query.pingServer()) {
            try {

                Process process =Runtime.getRuntime().exec(String.format("screen-dmS %s sh %s",screenName,path));
                ServerLog(process.getInputStream());
                return "Сервер запущен";
            } catch (IOException e) {
                e.printStackTrace();
                return e.getMessage();

            }
        }
        else
        {
            return "Сервер уже запущен";
        }

    }

    public String stopServer() throws IOException, AttachNotSupportedException {

        if(query.pingServer())
        {
            List<VirtualMachineDescriptor> machines = VirtualMachine.list();
            for(VirtualMachineDescriptor descriptor : machines)
            {
                if(descriptor.displayName().contains(serverJar)) {


                    try
                    {
                        ProcessHandle handle = ProcessHandle.of(Long.parseLong(descriptor.id())).get();
                        handle.destroy();
                        while (handle.isAlive())
                        {

                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
            process=null;
            input=null;
            output=null;
            return "Server stopping";
        }
      return "Server is not running.";
    }

    public String restartServer() throws IOException, AttachNotSupportedException {
        if(query.pingServer())
        {

            stopServer();
            startServer();
            return "server restarting";

        }
        else
        {
            return "server is not running";
        }

    }

    public boolean sendCommand(String cmd,Process process)
    {
        try
        {
            PrintWriter writer = new PrintWriter(output);
            writer.println(cmd);
            writer.flush();
            if(cmd.equals("stop"))
            {

                webSocketClients.forEach(session-> {
                    try {
                        session.getBasicRemote().sendText("Сервер остановлен");
                        session.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                input=null;
                output=null;
                process=null;
            }


            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public void ServerLog(InputStream stream)
    {

        if(stream!=null)
        {
            Thread thread = new Thread(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    Scanner scanner = new Scanner(stream);
                    PrintWriter writer = new PrintWriter(new FileWriter("D:\\mc_log.txt"));
                    while (scanner.hasNextLine())
                    {
                        String str = scanner.nextLine();
                        sendTextAllConsumers(str);
                        System.out.println(str);
                        writer.println(str);
                        writer.flush();
                    }
                    writer.close();
                }
            });
            thread.start();

        }
    }

    public Map<String,String> getServerInfo() throws IOException {
        query.sendQuery();
        return query.getValues();
    }

    public void addConsumer(Session session)
    {
        webSocketClients.add(session);
    }

    public void removeConsumer(Session session)
    {
        if(webSocketClients.contains(session))webSocketClients.remove(session);
    }

    public int countConsumer()
    {
        return webSocketClients.size();
    }

    public void sendTextAllConsumers(String msg)
    {
        webSocketClients.forEach(client-> {
            try {
                client.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}

