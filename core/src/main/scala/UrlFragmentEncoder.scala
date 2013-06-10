package scalex

import java.net.URLEncoder

// see http://stackoverflow.com/questions/607176/java-equivalent-to-javascripts-encodeuricomponent-that-produces-identical-outpu
object UrlFragmentEncoder {

  def encode(fragment: String): String =
    URLEncoder.encode(fragment, "UTF-8")
      .replace("+", "%20")
      .replace("%21", "!")
      .replace("%27", "'")
      .replace("%28", "(")
      .replace("%29", ")")
      .replace("%7E", "~")
}
