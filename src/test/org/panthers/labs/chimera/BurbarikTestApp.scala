package org.panthers.labs.chimera

import munit.CatsEffectSuite
import sttp.client3._

class BurbarikTestApp extends CatsEffectSuite {
  test("GET / should return welcome message") {
    val backend = HttpURLConnectionBackend()
    val request = basicRequest.get(uri"http://localhost:8080/")

    val response = request.send(backend)
    assertEquals(response.body, Right("Welcome to Burbarik Observability"))
  }
}
