package org.exloz.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.Session;
import org.exloz.adapters.WebSocketClient;
import org.exloz.data.ConfigData;
import org.jboss.logging.Logger;

@ApplicationScoped
public class BinanceSocketProcessorImp implements BinanceSocketProcessor{

    private final WebSocketClient client;
    private static final Logger LOGGER = Logger.getLogger(WebSocketClient.class);

    public BinanceSocketProcessorImp(WebSocketClient client) {
        this.client = client;
    }

    @Override
    public Multi<JsonNode> process(String webSocketUrl, ConfigData config) {
        client.connect(webSocketUrl)
               .thenAccept(session -> {
                   LOGGER.info("Sending config setup");
                   sendConfiguration(session, config);
               });
       return processMessages();
    }

    private void sendConfiguration(Session session, ConfigData configuration) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonConfig = mapper.writeValueAsString(configuration);
            session.getAsyncRemote().sendText(jsonConfig);
            LOGGER.info("Sent configuration: " + jsonConfig);
        } catch (Exception e) {
            LOGGER.errorf("Error: ", e);
            e.printStackTrace();
        }
    }

    private Multi<JsonNode> processMessages() {
        return Multi.createFrom().publisher(client.getMessagePublisher())
                .onItem().transform(this::getData);
    }

    private JsonNode getData(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(message);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
