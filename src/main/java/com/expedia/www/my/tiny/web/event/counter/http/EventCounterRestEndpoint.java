package com.expedia.www.my.tiny.web.event.counter.http;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.expedia.www.my.tiny.web.event.counter.domain.Event;
import com.expedia.www.my.tiny.web.event.counter.service.EventCounter;


@Singleton
@Path("/event")
public class EventCounterRestEndpoint {

    private final EventCounter<Event<String>> counter = new EventCounter<>(10, 60000);

    @POST
    @Produces("text/plain")
    public Response record(@QueryParam("e") final String eventType) {
        this.counter.recordEvent(new Event<String>(eventType));
        return Response.accepted().build();
    }

    @GET
    @Produces("text/plain")
    public Long getEvents(@QueryParam("e") final String eventType) {
        return this.counter.getCount(new Event<String>(eventType));
    }

    @GET
    @Path("/persist")
    @Produces("text/plain")
    public Response persist(@QueryParam("k") final Integer count) {
        String s = this.counter.persistTopKCounts(count);
        return Response.ok().entity("Top "+count+": "+s).build();
    }
}
