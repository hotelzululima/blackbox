package com.dddr.blackbox.services

import akka.http.scaladsl.model.{MediaTypes, HttpEntity, HttpResponse}
import com.dddr.blackbox.models.UserEntity
import com.gilt.handlebars.scala.binding.dynamic._
import com.gilt.handlebars.scala.Handlebars

/**
 * Created by rroche on 9/8/15.
 */
trait LayoutService {
  def generateView(title: String, userObject: UserEntity, layoutTemplate: String, partial: String): HttpResponse ={

    val dataMap = Map("title" -> title, "user" -> userObject)
    val binding = DynamicBinding(dataMap)
    val partials = Map("main" -> Handlebars(partial))
    val layoutEngine = Handlebars(layoutTemplate)
    val layoutString = layoutEngine(context = binding, partials = partials)

    HttpResponse(entity = HttpEntity(MediaTypes.`text/html`, layoutString))
  }
}
