package ophir
package http

import org.scalatra._

trait MainServlet extends ScalatraServlet {

  get("/") {
    <html>Hello!</html>
  }
}
