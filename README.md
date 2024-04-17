# Acrolinx Java SDK Demo

This is a showcase building an automated [Acrolinx](https://www.acrolinx.com/) Integration using the [Acrolinx Java SDK](https://github.com/acrolinx/sdk-java).

For integrating the Sidebar see: [Acrolinx Java Sidebar Demo](https://github.com/acrolinx/acrolinx-sidebar-demo-java).

## Prerequisites

Please contact [Acrolinx SDK support](https://github.com/acrolinx/acrolinx-coding-guidance/blob/main/topics/sdk-support.md)
for consulting and getting your integration certified.

This sample works with a test license on an internal Acrolinx URL. This license is only meant for demonstration and developing purposes. Once you finished your integration, you'll have to get a license for your integration from Acrolinx.

Before you start developing your own integration, you might benefit from looking into:

* [Build With Acrolinx](https://support.acrolinx.com/hc/en-us/categories/10209837818770-Build-With-Acrolinx),
* the [Guidance for the Development of Acrolinx Integrations](https://github.com/acrolinx/acrolinx-coding-guidance),
* the [Acrolinx Platform API](https://github.com/acrolinx/platform-api)
* the [Rendered Version of Acrolinx Platform API](https://acrolinxapi.docs.apiary.io/#)
* the [Acrolinx SDKs](https://github.com/acrolinx?q=sdk), and
* the [Acrolinx Demo Projects](https://github.com/acrolinx?q=demo).

## Getting Started

### Build the Project

1. You need Java 11 to build this project.
2. This project uses [Gradle](https://gradle.org/).
To build this project with the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html#sec:using_wrapper), execute the following command:

```bash
./gradlew build
```

on an UNIX system, or

```batch
gradlew build
```

on a Windows computer.

## Run the Samples

```bash
./gradlew SimpleStringCheckDemo
```

```
./gradlew DocxCheckDemo
```
