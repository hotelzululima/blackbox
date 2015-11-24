package org._3dr.blackbox.utils

import play.api.data.validation.ValidationError
import play.api.libs.json._

/**
  * Created by genarorg on 11/23/15.
  */
trait ValidationErrorFormat {
  implicit object JsErrorJsonWriter extends Writes[Seq[(JsPath, Seq[ValidationError])]] {
    def writes(o: Seq[(JsPath, Seq[ValidationError])]): JsValue = Json.obj(
      "errors" ->
        JsArray(o.map {
          case (path, validationErrors) => Json.obj(
            "path" -> Json.toJson(path.toString()),
            "validationErrors" -> JsArray(validationErrors.map(validationError => Json.obj(
              "message" -> JsString(validationError.message),
              "args" -> JsArray(validationError.args.map(_ match {
                case x: Int => JsNumber(x)
                case x => JsString(x.toString)
              }))
            )))
          )
        }
        )
    )
  }
}
