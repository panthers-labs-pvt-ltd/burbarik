import mill._, scalalib._

object burbarik extends ScalaModule {
  def scalaVersion = "2.12.18"
  def ivyDeps = Agg(
    ivy"com.softwaremill.sttp.tapir::tapir-core:1.9.4",
    ivy"com.softwaremill.sttp.tapir::tapir-netty-server-cats:1.9.4",
    ivy"io.opentelemetry:opentelemetry-sdk:1.38.0",
    ivy"io.opentelemetry:opentelemetry-exporter-jaeger:1.38.0",
    ivy"io.prometheus:simpleclient:0.16.0",
    ivy"io.prometheus:simpleclient_hotspot:0.16.0",
    ivy"org.typelevel::cats-effect:3.5.1",
    ivy"org.scalameta::munit:1.0.0-M10"
  )
}