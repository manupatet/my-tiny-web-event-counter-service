package com.expedia.www.my.tiny.web.event.counter.http;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;

import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.expedia.www.my.tiny.web.event.counter.Main;
import com.jayway.restassured.RestAssured;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
// @ContextConfiguration("classpath:spring-config-it.xml")
public class EventCounterRestEndpointTest {

    @Value("${local.server.port}")
    private int serverPort;

    @Before
    public void setUp() {
        RestAssured.port = this.serverPort;
    }

    @Test
    public void eventFreqIntegrationTest() {
        given().post("/event?e=a").then().statusCode(HttpStatus.SC_ACCEPTED);
        given().post("/event?e=b").then().statusCode(HttpStatus.SC_ACCEPTED);
        given().post("/event?e=b").then().statusCode(HttpStatus.SC_ACCEPTED);
        given().post("/event?e=b").then().statusCode(HttpStatus.SC_ACCEPTED);
        given().post("/event?e=a").then().statusCode(HttpStatus.SC_ACCEPTED);
        given().post("/event?e=b").then().statusCode(HttpStatus.SC_ACCEPTED);
        given().post("/event?e=b").then().statusCode(HttpStatus.SC_ACCEPTED);
        given().post("/event?e=c").then().statusCode(HttpStatus.SC_ACCEPTED);
        given().post("/event?e=c").then().statusCode(HttpStatus.SC_ACCEPTED);
        given().post("/event?e=c").then().statusCode(HttpStatus.SC_ACCEPTED);
        given().post("/event?e=d").then().statusCode(HttpStatus.SC_ACCEPTED);

        given().get("/event?e=a").then().statusCode(HttpStatus.SC_OK).body(Matchers.equalTo("2"));
        given().get("/event?e=b").then().statusCode(HttpStatus.SC_OK).body(Matchers.equalTo("5"));
        given().get("/event?e=c").then().statusCode(HttpStatus.SC_OK).body(Matchers.equalTo("3"));
        given().get("/event?e=d").then().statusCode(HttpStatus.SC_OK).body(Matchers.equalTo("1"));

        given().post("/event?e=d").then().statusCode(HttpStatus.SC_ACCEPTED);
        given().post("/event?e=a").then().statusCode(HttpStatus.SC_ACCEPTED);
        given().post("/event?e=c").then().statusCode(HttpStatus.SC_ACCEPTED);
        given().post("/event?e=c").then().statusCode(HttpStatus.SC_ACCEPTED);
        given().post("/event?e=c").then().statusCode(HttpStatus.SC_ACCEPTED);
        given().post("/event?e=b").then().statusCode(HttpStatus.SC_ACCEPTED);

        given().get("/event/persist?k=4").then().statusCode(HttpStatus.SC_OK).body(endsWith("b_6, c_6, a_3, d_2]"));
    }
}
