package controllers

/**
  * Created by Neelaksh on 14/8/17.
  */

import akka.stream.Materializer
import com.google.inject.Inject
import play.api.http.DefaultHttpFilters
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Filter to add HTTP headers in all requests and prevent browser caching for all the response pages
  * Created by bharat on 1/24/16.
  */

class DisableBrowserCache @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    nextFilter(requestHeader).map { result =>

      result.withHeaders("Cache-Control" -> "no-cache, no-store, must-revalidate",
        "Pragma" -> "no-cache",
        "Expires" -> "0")
    }
  }
}

class CustomHttpFilter @Inject()(disableCache: DisableBrowserCache) extends DefaultHttpFilters(disableCache)
