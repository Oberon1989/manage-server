package oberon1989.config;


import oberon1989.server.ServerControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;


@Configuration
@ComponentScan("oberon1989")
@PropertySource("classpath:/application.properties")

public class AppConfig {


    @Bean(initMethod = "init")
    @Autowired()
    public ServerControl server(Environment env)
    {

        return new ServerControl(env);
    }

}
