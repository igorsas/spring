package spring.core.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import spring.core.app.event.Event;
import spring.core.app.event.EventType;
import spring.core.app.logger.EventLogger;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

public class App {
    @Autowired
    private Client client;
    @Autowired
    @Qualifier("loggerMap")
    private Map<EventType, EventLogger> loggers;
    @Autowired
    @Qualifier("consoleEventLogger")
    private EventLogger eventLogger;

    private App() {
    }

    //TODO: should be Map<EventType, List<EventLogger>>
    public App(Client client, EventLogger eventLogger, Map<EventType, EventLogger> loggers) {
        this.client = client;
        this.eventLogger = eventLogger;
        this.loggers = loggers;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
//        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        App app = (App) context.getBean("app");
        Event event = (Event) context.getBean("event");
        event.setId(app.client.getId());
        event.setMessage(app.client.getGreeting());
        app.logEvent(EventType.INFO, event);
        context.close();
    }

    public void logEvent(EventType type, Event event) {
        EventLogger logger = loggers.get(type);
        if (logger == null)
            logger = eventLogger;
        logger.logEvent(event);
    }

    @PostConstruct
    public void init() {
        loggers = new HashMap<EventType, EventLogger>();
    }
}
