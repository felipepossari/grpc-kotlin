## gRPC-kotlin project

gRPC project that simulates unary and stream calls. This project uses gRPC, Kotlin and Micronaut.

How to run:

- Git clone;
- Gradle clean build;
- Run the script ssl/instructions.sh to generate a certificate;
- Run server/Application.kt to start the gRPC server;
- Run client/ApplicationClient.kt to start the server calls;

You can debug it adding breakpoints into server/UserEndpoint.kt
