package scalex
package http

import com.typesafe.play.mini._

import play.api.mvc.Action
import play.api.mvc.Results._

object ScalexApp extends com.typesafe.play.mini.Application {
   def route  =  {
      case GET(Path("/")) & QueryString(qs) =>  Action{
          Ok(<h1>It works!</h1>).as("text/html")
      }
    }
}
