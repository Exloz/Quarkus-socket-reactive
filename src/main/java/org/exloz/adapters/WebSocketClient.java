package org.exloz.adapters;

import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import jakarta.websocket.*;
import org.jboss.logging.Logger;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ClientEndpoint
public class WebSocketClient {

    private static final Logger LOGGER = Logger.getLogger(WebSocketClient.class);
    private final BroadcastProcessor<String> messageProcessor = BroadcastProcessor.create();
    private CompletableFuture<Session> connectionFuture;
    private Session session;

    public CompletionStage<Session> connect(String websocketurl) {
        connectionFuture = new CompletableFuture<>();
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            container.connectToServer(this, URI.create(websocketurl));
        } catch (Exception e) {
            LOGGER.errorf("Error:", e);
            connectionFuture.completeExceptionally(e);
        }
        return connectionFuture;
    }

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("WebSocket Conectado correctamente");
        this.session = session;
        connectionFuture.complete(session);
    }

    @OnMessage
    public void onMessage(String message) {
        messageProcessor.onNext(message);

    }

    @OnClose
    public void OnClose(Session session, CloseReason closeReason) {
        LOGGER.info("Websocket cerrado: " + closeReason.getReasonPhrase());
        messageProcessor.onComplete();
    }

    @OnError
    public void onError(Session session, Throwable throwable){
        LOGGER.errorf("Errorrrr: ", throwable);
        connectionFuture.completeExceptionally(throwable);
    }
    public BroadcastProcessor<String> getMessagePublisher(){
        return messageProcessor;
    }

    public void sendMessage(String message) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
        }
    }
}
