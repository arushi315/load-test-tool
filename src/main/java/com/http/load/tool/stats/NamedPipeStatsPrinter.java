package com.http.load.tool.stats;

import com.http.load.tool.condition.NamedPipeModeCondition;
import com.http.load.tool.dataobjects.TestStatus;
import com.http.load.tool.pipe.NamedPipeReader;
import io.vertx.rxjava.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.OptionalDouble;

/**
 * Created by manish kumar.
 */
@Component
@Conditional({NamedPipeModeCondition.class})
public class NamedPipeStatsPrinter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NamedPipeStatsPrinter.class);
    @Autowired
    private Vertx vertx;
    @Autowired
    private TestStatus testStatus;

    @PostConstruct
    public void init() {
        vertx.setPeriodic(5000, doNothing -> {
            String stats = getStats();
            LOGGER.info(stats);
            System.out.println(stats);
        });
    }

    private String getStats() {
        String stats = "";
        if (testStatus.getNamedPipeReaders() != null) {

            long testDuration = testStatus.getTestStartTime() > 0 ? ((System.currentTimeMillis() - testStatus.getTestStartTime()) / 1000) : 0;
            List<NamedPipeReader> pipes = testStatus.getNamedPipeReaders();
            OptionalDouble avg = pipes.stream().mapToDouble(pipe -> pipe.getAvgTimeInMillis()).average();
            stats = "\n\n\n\nTest Duration = " + testDuration + " Seconds"
                    + "\nTotal requests = " + testStatus.getTotalRequests()
                    + "\nSuccess = " + testStatus.getSuccessCount()
                    + "\nError = " + testStatus.getErrorCount()
                    + "\nMissed requests count = " + testStatus.getMissedRequestsOnPipe()
                    + "\nAvg time taken = " + (avg.isPresent() ? avg.getAsDouble() : 0)
                    + "\nAvailable processes count = " + pipes.stream().filter(pipe -> !pipe.isInUse()).count();
        }
        return stats;
    }
}