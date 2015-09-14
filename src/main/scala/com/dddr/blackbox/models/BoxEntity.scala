package com.dddr.blackbox.models

/**
 * Created by rroche on 9/8/15.
 */

case class SoftwareVersion(name: String, version: String)
/*
case class NewMissionEntity(name: String, vehicleId: VehicleId, actions: Option[List[ActionEntity]] = None,
                             tags: Option[List[String]] = None)
 */
case class BoxEntityNew(title: String, description: String, category: Option[List[String]] = None,
                        firmware: Option[List[SoftwareVersion]] = None)

case class BoxEntity(id: BoxId = BoxId(), title: String, description: String, category: List[String],
                     firmware: List[SoftwareVersion], docType: Option[String] = Some("box"), userId: UserId)
