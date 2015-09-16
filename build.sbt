name := "BlackBox"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Artifactory" at "https://dronekit.artifactoryonline.com/dronekit/libs-snapshot-local/"

credentials += Credentials("Artifactory Realm", "dronekit.artifactoryonline.com", "publish", "Km4-PSH-aEM-6Fm")

libraryDependencies ++= {
  val akkaV = "2.3.12"
  val akkaStreamV = "1.0"
  val scalaTestV = "2.2.4"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-core-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-testkit-experimental" % akkaStreamV,
    "org.scalatest" %% "scalatest" % scalaTestV % "test",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "test",
    "org.mindrot" % "jbcrypt" % "0.3m",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
    "com.couchbase.client" % "java-client" % "2.1.4",
    "com.gilt" %% "handlebars-scala" % "2.0.1",
    "io.dronekit" %% "couchbasescala" % "1.0",
    "io.reactivex" % "rxjava-reactive-streams" % "1.0.1",
    "io.reactivex" %% "rxscala" % "0.25.0"
  )
}
