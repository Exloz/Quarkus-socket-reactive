package org.exloz.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.exloz.data.ConfigData;
import org.exloz.services.BinanceSocketProcessorImp;

import java.util.ArrayList;

@Path("/init")
public class StreamInfoBinance {

    @Inject
    BinanceSocketProcessorImp processor;

    //    @POST
//    @Path("/{urlConnection}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response initBinanceDataStream(String urlConnection, ConfigData configData) {
//        var result = processor.process(urlConnection, configData);
//        return Response.accepted().entity(result).build();
//    }
    @GET
    @Path("/{urlConnection}")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<JsonNode> initBinanceDataStream(String urlConnection) {
        ArrayList<String> list = new ArrayList<>();
        list.add("btcusdt@ticker");
        ConfigData configData = new ConfigData("SUBSCRIBE", list);
        return processor.process(urlConnection, configData).invoke(x -> System.out.println(x));
    }
}
