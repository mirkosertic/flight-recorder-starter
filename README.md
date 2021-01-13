# Why & When

![Build Workflow](https://github.com/mirkosertic/flight-recorder-starter/workflows/Build%20Workflow/badge.svg) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.mirkosertic/flight-recorder-starter/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/de.mirkosertic/flight-recorder-starter)

This is a Spring Boot 2 Starter exposing the JDK Flight Recorder as a Spring Boot Actuator Endpoint.

Normally the JDK Flight Recorder is available locally or by JMX remote. Depending on your deployment scenario shell or
JMX access might not be available for the application server. Here comes this handy starter into play!

# How

This starter adds a new Spring Boot Actuator endpoint for JDK Flight Recorder remote control. This RESTful endpoint
allows starting and stopping Flight Recording and downloading the `.jfr` files for further analysis.

Just add the following dependency to your Spring Boot 2 project:

```
<dependency>
  <groupId>de.mirkosertic</groupId>
  <artifactId>flight-recorder-starter</artifactId>
  <version>2.0.1</version>
</dependency>
```

and don't forget to add the following configuration:

```
flightrecorder:
  enabled: true  # is this starter active?
``` 

Please note: the minimum Java/JVM runtime version is 11!

## Starting Flight Recording

The following `cURL` command starts a new Flight Recording and returns the created Flight Recording ID:

```
curl  -i -X PUT -H "Content-Type: application/json" -d '{"duration": "60","timeUnit":"SECONDS"}' http://localhost:8080/actuator/flightrecorder

HTTP/1.1 200 
Content-Type: text/plain
Content-Length: 1
Date: Thu, 03 Sep 2020 11:24:53 GMT

1
```

Flight Recording starts for a given period, in this case 60 seconds and stops then.

Every recording session gets its own unique Flight Recording ID. The endpoint returns this ID as plain text, in this
case ID `1`. This ID must be used to download the recorded data.

## Downloading results

The following `cURL` command stops the Flight Recording with ID `1` and downloads the `.jfr` file:

```
curl --output recording.jfr http://localhost:8080/actuator/flightrecorder/1
```

The downloaded `.jfr` file can be imported into JDK Mission Control (JMC) for further analysis.

## Visiting the interactive Flamegraph

This starter can generate an interactive Flamegraph from a Flight Recorder recording. You can gain a quick overview by
visiting the following URL in your browser to see the graph for a recording with ID `1`:

```
http://localhost:8080/actuator/flightrecorder/1/flamegraph.html
```

and you'll get:

![Flamegraph](docs/flamegraph.png)

The starter automatically tries to visualize only classes belonging to the running Spring Boot application. It filters
the stacktrace samples by classes that are in the package or sub-package of the running application instance annotated
with a
`@SpringBootApplication` annotation.

However, you can always get the unfiltered Flamegraph by visiting:

```
http://localhost:8080/actuator/flightrecorder/1/rawflamegraph.html
```

## Stopping Flight Recording and discarding data

The following `cURL` command stops the Flight Recording with ID `1` and discards all data:

```
curl -X DELETE http://localhost:8080/actuator/flightrecorder/1
```

## Trigger Flight Recording based on Micrometer Metrics

This starter allows automatic Flight Recording based on Micrometer Metrics. Using an application configuration file we
can configure triggers based on SpEL (Spring Expression Language) which are evaluated on a regular basis. Once a trigger
expression evaluates to true, a Flight Recording in started with a predefined duration and configuration. The most
common setup would be to trigger a Flight Recording profiling once CPU usage is above a given value.

Here is a sample configuration file in YAML syntax:

```
flightrecorder:
  enabled: true  # is this starter active?
  recordingCleanupInterval: 5000 # try to cleanup old recordings every 5 seconds
  triggerCheckInterval: 10000 # evaluate trigger expressions every 10 seconds
  trigger:
    - expression: meter('jvm.memory.used').tag('area','nonheap').tag('id','Metaspace').measurement('value') > 100
      startRecordingCommand: 
        duration: 60
        timeUnit: SECONDS
``` 

The list of all created recordings can be seen as a JSON file using the following api:

```
http://localhost:8080/actuator/flightrecorder/
```

## Advanced Configuration

### Location of recordings

By default, all the recording files are stored at temporal system folder, ofter the "/tmp" folder. This base path can be
changed through the following property:

```
flightrecorder:
  jfr-base-path: /my-path 
```