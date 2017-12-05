package com.http.load.tool;

import com.http.load.tool.dataobjects.TestInput;
import com.http.load.tool.dataobjects.TestStatus;
import com.http.load.tool.executor.HttpLoadExecutor;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by manish kumar.
 */
@Component
public class HttpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);
    @Autowired
    private TestStatus testStatus;
    @Autowired
    private TestInput input;
    @Autowired(required = false)
    private HttpLoadExecutor httpLoadExecutor;
    @Autowired
    private Vertx vertx;
    @Autowired
    private ErrorHandler errorHandler;
    @Autowired
    private InputDataValidator validator;
    @Autowired
    private HttpServer httpServer;
    @Value("${load.tool.port:8080}")
    private int port;

    @PostConstruct
    public void startServer() {
        System.setProperty("logback.configurationFile", "logback.xml");
        vertx.createHttpServer(new HttpServerOptions()
                .setCompressionSupported(true))
                .requestHandler(createRequestHandler()::accept)
                .listen(port);
        LOGGER.info("Started HTTP server at port {}", port);
    }

    private Router createRequestHandler() {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create()).failureHandler(context -> errorHandler.handle(context));

        router.post("/load/start").handler(validator);
        router.post("/load/start").handler(context -> httpServer.startLoadTest(context));
        router.post("/load/stop").handler(context -> httpServer.stopLoadTest(context));
        return router;
    }

    private void startLoadTest(final RoutingContext context) {
        HttpServerResponse response = context
                .response()
                .setChunked(true);
        if (testStatus.isTestNotRunning()) {
            testStatus.reset(input.getHttpLoadInput());
            httpLoadExecutor.scheduleTest();
            response
                    .write("Load test triggered successfully!!!");
        } else {
            response
                    .write("Load test already running!!!")
                    .setStatusCode(400);
        }
        response.end();
    }

    private void stopLoadTest(final RoutingContext context) {
        testStatus.shutdown();
        httpLoadExecutor.stopTest();
        context.response().setChunked(true).write("Load test stopped successfully!!!").end();
    }
}