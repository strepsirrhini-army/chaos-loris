# Chaos Loris
[![Build Status](https://travis-ci.org/strepsirrhini-army/chaos-loris.svg)](https://travis-ci.org/strepsirrhini-army/chaos-loris)

This project is a self-hostable application to randomly destroy Cloud Foundry application instances, as an aid to resilience testing of high-availability systems. Its main features are:

## Requirements
### Java, Maven
The application is written in Java 8 and packaged as a self executable JAR file. This enables it to run anywhere that Java is available. Building the application (required for deployment) requires [Maven][m].

## Configuration
Since the application is designed to work in a PaaS environment, all configuration is done with environment variables.

| Key | Description
| --- | -----------

### Reporting

| Key | Description
| --- | -----------
| `DATADOG_APIKEY` | Allows Chaos Loris to log destruction events to [DataDog][d]. If this value is not set Chaos Loris will redirect the output to the logger at `INFO` level.
| `DATADOG_APPKEY` | Used with the `DATADOG_APIKEY` to give DataDog access.
| `DATADOG_TAGS` | A set of tags to attach to each DataDog event.

### Security

## Deployment
_The following instructions assume that you have [created an account][c] and [installed the `cf` command line tool][i]._

In order to automate the deployment process as much as possible, the project contains a Cloud Foundry [manifest][y].  To deploy run the following commands:

```bash
mvn clean package
cf push
```

To confirm that Chaos Loris has started correctly run:

```bash
cf logs chaos-loris --recent
```

## Developing
The project is set up as a Maven project and doesn't have any special requirements beyond that. It has been created using [IntelliJ][j] and contains configuration information for that environment, but should work with other IDEs.


## License
The project is released under version 2.0 of the [Apache License][a].

[a]: http://www.apache.org/licenses/LICENSE-2.0
[c]: https://console.run.pivotal.io/register
[d]: https://www.datadoghq.com
[i]: http://docs.run.pivotal.io/devguide/installcf/install-go-cli.html
[j]: http://www.jetbrains.com/idea/
[m]: http://maven.apache.org
[y]: manifest.yml
