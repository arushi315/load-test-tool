# http-load-test-tool
Load test tool allows user to make HTTP request for given parameters. Built based on Vertx &amp; Java 8.


#### How to start tool for HTTP load test

        java -jar -Ddata.files.path=/var/opt/path_to_csv_file_containing_dynamic_parameters.csv http-load-test-tool-1.0-SNAPSHOT-fat.jar

#### How to start tool for Named Pipe test


    -Ddata.files.path=path_to_csv_file -Drun.in.named.pipe.test.mode=true -Dkerberos.config.path=C:/kerberos-config.json
    

Given below sample Kerberos config JSON.

        {
          "pipeNames":[
            "sample-1",
            "sample-2"
          ],
          "durationInSeconds":10,
          "requestsPerSecond":5,
          "principle":"some_principle",
          "password":"some_password",
          "spn":"some_spn"
        }

    

#### Sample input JSON to trigger the load test.

        {
                    "durationInSeconds" : 700000,
                    "useBasicAuth" : true,
                    "basicAuthUser" : "test",
                    "basicAuthPassword" : "test",
                    "rampUpTimeInSeconds" : 20,
                    "httpClientInstances" : 10,
                    "enableMutualAuth" : true,
                    "certOptionsForMutualAuth" : [
                    	{
                    		"path" : "absolute_path_to_cert_1",
                    		"password" : "password_for_cert_1"
                    	},
                    	{
                    		"path" : "absolute_path_to_cert_2",
                    		"password" : "password_for_cert_2"
                    	}
                    ],
                    "testType" : "REQUEST_PER_SECOND",
                    "path" : "/Path-To-Service-Endpoint",
                	"remoteHosts" : [
                		"https://localhost:8443",
                		"https://localhost:8444",
                		"https://localhost:8445"
                	],
                	"commonParameters" : [
                		{
                			"name" : "Dynamic-Param1",
                			"value" : "###"
                		},
                		{
                			"name" : "Dynamic-Param2",
                			"value" : "###"
                		},
                		{
                			"name" : "Dynamic-Param3",
                			"value" : "###"
                		}
                	],
                    "remoteOperations" : [
                    	{
                    		"httpMethod" : "OPTIONS",
                    		"requestFilePath" : "absolute_path_to_request_file",
                    		"operationType" : "Give-It-Some-Unique-Name-1",
                	    	"loadRequestsPerSecond" : 5, 
                	    	"parameters" : [
                	    		{
                	    			"name" : "Static-Param-Name-1",
                	    			"value" : "Static-Param-Value-1"
                	    		}
                	    	]
                    	},
                    	{
                    		"operationType" : "Give-It-Some-Unique-Name-2",
                	    	"loadRequestsPerSecond" : 5, 
                	    	"parameters" : [
                	    		{
                	    			"name" : "Static-Param-Name-2",
                	    			"value" : "Static-Param-Value-2"
                	    		}
                	    	]
                    	},
                    	{
                    		"operationType" : "Give-It-Some-Unique-Name-3",
                	    	"loadRequestsPerSecond" : 1, 
                	    	"parameters" : [
                	    		{
                	    			"name" : "Static-Param-Name-3",
                	    			"value" : "Static-Param-Value-3"
                	    		}
                	    	]
                    	}
                    ]
                }
                
See ![Sample CSV file for HTTP load test input](src/main/resources/http-load-input-query-parameters.csv?raw=true "Title")