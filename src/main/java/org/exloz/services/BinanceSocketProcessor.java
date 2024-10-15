package org.exloz.services;

import com.fasterxml.jackson.databind.JsonNode;
import io.smallrye.mutiny.Multi;
import org.exloz.data.ConfigData;


public interface BinanceSocketProcessor {

    Multi<JsonNode> process (String webSocketUrl, ConfigData config);
}
