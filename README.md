# Chaos Loris
This project is a self-hostable application to randomly destroy Cloud Foundry application instances, as an aid to resilience testing of high-availability systems. The is accomplished by describing a schedule that an application should be acted on as well as the probability that any instance of that application has of being destroyed.

## Usage
The only interface to Chaos Loris today is via a REST-ful interface.  This interface is documented with examples [here][d].

## Requirements
### Java, Maven
The application is written in Java 8 and packaged as a self executable JAR file. This enables it to run anywhere that Java is available.

### Ruby, bundler
In order to build the project you'll need to install Ruby and `bundler` gem. Chaos-loris uses them to render docs on a build stage. Run `gem install bundler` to get `bundler` gem.

## Configuration
Since the application is designed to work in a cloud-native environment, all configuration is done with environment variables.

| Key | Description
| --- | -----------
| `LORIS_CLOUDFOUNDRY_HOST` | The host of the Cloud Foundry instance running the targeted applications.
| `LORIS_CLOUDFOUNDRY_PASSWORD` | The password to log into the Cloud Foundry instance.
| `LORIS_CLOUDFOUNDRY_SKIPSSLVALIDATION` | Whether to skip SSL validation of the Cloud Foundry instance running targeted applications. _(Optional, default `false`)_
| `LORIS_CLOUDFOUNDRY_USERNAME` | The username to log into the Cloud Foundry instance.  The user must have permissions to destroy any configured application.

## Deployment
_The following instructions assume that you have [created an account][c] and [installed the `cf` command line tool][i]._

In order to automate the deployment process as much as possible, the project contains a Cloud Foundry [manifest][y].  To deploy run the following commands:

```bash
./mvnw clean package
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
[d]: http://strepsirrhini-army.github.io/chaos-loris/
[i]: http://docs.run.pivotal.io/devguide/installcf/install-go-cli.html
[j]: http://www.jetbrains.com/idea/
[y]: manifest.yml
