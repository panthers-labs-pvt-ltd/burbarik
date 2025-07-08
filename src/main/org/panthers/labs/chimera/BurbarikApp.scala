package org.panthers.labs.chimera

import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter
import sttp.tapir.server.netty.NettyServer
import sttp.tapir.server.netty.cats.CatsNettyServerInterpreter
import sttp.tapir._
import sttp.tapir.server.ServerEndpoint
import cats.effect._
import io.prometheus.client.exporter.HTTPServer
import io.prometheus.client.hotspot.DefaultExports

object BurbarikApp extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    for {
      _ <- IO(println("[App] Initializing telemetry"))
      _ <- configureTelemetry()
      _ <- IO(println("[App] Starting HTTP Server"))
      _ <- startServer()
    } yield ExitCode.Success
  }

  def configureTelemetry(): IO[Unit] = IO {
    val jaegerExporter = JaegerGrpcSpanExporter.builder()
      .setEndpoint("http://localhost:14250")
      .build()

    val tracerProvider = SdkTracerProvider.builder()
      .addSpanProcessor(BatchSpanProcessor.builder(jaegerExporter).build())
      .setResource(Resource.create(io.opentelemetry.semconv.resource.attributes.ResourceAttributes.SERVICE_NAME, "burbarik-app"))
      .build()

    val openTelemetry = OpenTelemetrySdk.builder()
      .setTracerProvider(tracerProvider)
      .buildAndRegisterGlobal()

    GlobalOpenTelemetry.set(openTelemetry)

    DefaultExports.initialize()
    new HTTPServer(9000)
  }

  def startServer(): IO[Unit] = {
    val helloEndpoint: Endpoint[Unit, Unit, String, Any] =
      endpoint.get.in("/").out(stringBody)

    val helloServerEndpoint: ServerEndpoint[Any, IO] =
      helloEndpoint.serverLogicSuccess[IO](_ => IO {
        println("[App] Handling root endpoint")
        "Welcome to Burbarik Observability"
      })

    CatsNettyServerInterpreter[IO]().start(Seq(helloServerEndpoint)).void
  }
}