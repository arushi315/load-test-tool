package com.http.load.tool.inputdata;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * Created by manish kumar.
 */
@Configuration
public class CsvDataFileReader {

    @Value("#{'${data.files.path:}'.split(',')}")
    private List<String> dataFilePaths;

    @Bean
    public List<Map<String, String>> createDevices() throws IOException {
        List<Map<String, String>> data = null;
        List<List<String>> csvData = readRecords();
        if (csvData != null) {
            List<String> parametersNames = csvData.stream().findFirst().get();
            data = csvData.stream()
                    .skip(1)
                    .map(attributes -> {
                        Map<String, String> parameters = new HashMap<>();
                        int index = 0;
                        for (String name : parametersNames) {
                            parameters.put(name.trim(), attributes.get(index));
                            index++;
                        }
                        return parameters;
                    })
                    .collect(Collectors.toList());
        }
        return data;
    }

    private List<List<String>> readRecords() throws IOException {
        List<List<String>> data = null;
        if (!dataFilePaths.stream().anyMatch(StringUtils::isEmpty)) {
            data = readFromExternalDataFile(dataFilePaths.get(0));
            if (dataFilePaths.size() > 1) {
                for (String dataFilePath : dataFilePaths.stream().skip(1).collect(Collectors.toList())) {
                    List<List<String>> csvFromOtherFiles = readFromExternalDataFile(dataFilePath);
                    if (csvFromOtherFiles.size() != data.size()) {
                        throw new IllegalArgumentException("All CSV files should have same number of records");
                    }
                    for (int index = 0; index < data.size(); index++) {
                        List<String> clone = new ArrayList<>();
                        data.get(index).forEach(clone::add);
                        csvFromOtherFiles.get(index).forEach(clone::add);
                        data.set(index, clone);
                    }
                }
            }
        } else {
            // Most cases we won't execute this flow. This is for people to be able to define a default behavior.
            System.out.println("No external data file provided - assuming that load test will be executed with static parameters.");
        }
        return data;
    }

    private List<List<String>> readFromExternalDataFile(final String externalFilePath) throws IOException {
        List<List<String>> data;
        System.out.println("External data file provided. Path = " + externalFilePath);
        data = Files.readAllLines(Paths.get(externalFilePath))
                .stream()
                .map(line -> asList(line.split(",")))
                .collect(Collectors.toList());
        return data;
    }

    private List<List<String>> getDataFromDefaultFile(final String fileName) throws IOException {
        System.out.println("No external data file provided, using the default one :: " + fileName);
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(
                this.getClass().getClassLoader().getResourceAsStream(fileName)))) {
            return buffer
                    .lines()
                    .map(line -> asList(line.split(",")))
                    .collect(Collectors.toList());
        }
    }
}