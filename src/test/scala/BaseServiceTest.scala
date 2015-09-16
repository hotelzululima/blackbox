import akka.actor.ActorSystem
import akka.event.{NoLogging, LoggingAdapter}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.dddr.blackbox.http.{Protocol, HttpService}
import com.dddr.blackbox.utils.CouchbaseMigration
import io.dronekit.CouchbaseScala
import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by rroche on 9/8/15.
 */
trait BaseServiceTest extends FlatSpec with HttpService with ScalatestRouteTest with Matchers with CouchbaseMigration {
  override val log: LoggingAdapter = NoLogging
  override implicit val system: ActorSystem
  val protocol = new Protocol()
  val couchbase = new CouchbaseScala(
    couchbaseConfig.getString("hostname"),
    couchbaseConfig.getString("bucket"),
    couchbaseConfig.getString("password"),
    protocol)

  migrate()
}

