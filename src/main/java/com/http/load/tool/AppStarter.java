package com.http.load.tool;

import com.http.load.tool.dataobjects.TestInput;
import com.http.load.tool.dataobjects.TestStatus;
import io.vertxbeans.rxjava.VertxBeans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Created by manish kumar.
 */
@SpringBootApplication
@Import(VertxBeans.class)
public class AppStarter {

    public static void main(String[] args) {
        new SpringApplication(AppStarter.class).run(args);
    }

    @Bean
    public TestStatus createStatus() {
        return new TestStatus();
    }

    @Bean
    public TestInput createDefaultInput() {
        return new TestInput();
    }
}