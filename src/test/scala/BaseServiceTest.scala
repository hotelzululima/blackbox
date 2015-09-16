import akka.actor.ActorSystem
import akka.event.{NoLogging, LoggingAdapter}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.dddr.blackbox.http.HttpService
import com.dddr.blackbox.utils.CouchbaseMigration
import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by rroche on 9/8/15.
 */
trait BaseServiceTest extends FlatSpec with HttpService with ScalatestRouteTest with Matchers with CouchbaseMigration {
  override protected val log: LoggingAdapter = NoLogging
  override implicit val system: ActorSystem

  migrate()
}

