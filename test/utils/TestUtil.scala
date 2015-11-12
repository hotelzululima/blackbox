package utils

import play.api.test.{FakeRequest, PlaySpecification}

/**
  * Created by rroche on 11/12/15.
  */
trait TestUtil extends PlaySpecification {
  def fakeRequest(method: String = "GET", route: String = "/") = FakeRequest(method, route)
    .withHeaders(
      ("Date", "2014-10-05T22:00:00"),
      ("Authorization", "token=fd7ad598-84cb-11e5-a2d8-a7329ba812a2") // this be fake
    )
}
