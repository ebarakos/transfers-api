package modules.swagger

import javax.inject.Inject

import com.iheart.playSwagger.SwaggerSpecGenerator
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._

import scala.concurrent.Future


class SwaggerController @Inject() (val environment: play.api.Environment) extends Controller {
  implicit val cl = getClass.getClassLoader

  // The root package of your domain classes, play-swagger will automatically generate definitions when it encounters class references in this package.
  // In our case it would be "com.iheart", play-swagger supports multiple domain package names
  val domainPackage = "models"

  private lazy val generator = SwaggerSpecGenerator(domainPackage)

  def specs =
    Action.async { _ =>
      //generate() can also taking in an optional arg of the route file name.
      Future.fromTry(generator.generate("routes")).map(Ok(_))
    }

  def docs = Action {
    Redirect("/docs/swagger-ui/index.html?url=/docs/swagger.json")
  }
}
