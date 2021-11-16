package oberon1989.controllers;

import com.google.gson.Gson;
import com.sun.tools.attach.AttachNotSupportedException;
import lombok.RequiredArgsConstructor;
import oberon1989.server.ServerControl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping(value = "server", produces = "text/html;charset=UTF-8")
@RequiredArgsConstructor
public class ServerController {


    private final ServerControl control;


   // @CrossOrigin(origins = "http://localhost:63343")
    @GetMapping("start")
    public String start() {

        return control.startServer();

    }

    //@CrossOrigin(origins = "http://localhost:63343")
    @GetMapping("stop")
    public String stop() throws IOException, AttachNotSupportedException {
        return control.stopServer();
    }

   // @CrossOrigin(origins = "http://localhost:63343")
    @GetMapping("restart")
    public String restart() throws IOException, AttachNotSupportedException {
        return control.restartServer();
    }

    //@CrossOrigin(origins = "http://localhost:63343")
    @PostMapping("send")
    public String send(@RequestParam("cmd") String cmd) {
        control.sendCommand(cmd,null);
        return null;
    }

   // @CrossOrigin(origins = "http://localhost:63343")
    @GetMapping("info")
    public String getInfo() {
        StringBuilder builder=new StringBuilder();
        try {
           Map info = control.getServerInfo();
          // info.forEach((k,v)->builder.append(String.format("Key %s : value %s</br>",k,v)));

            Gson gson = new Gson();
           return gson.toJson(control.getServerInfo());
          // return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
