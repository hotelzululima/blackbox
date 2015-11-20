name := "blackbox"

version := "1.0"

lazy val blackbox = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  ws,
  "org.postgresql" % "postgresql" % "9.4-1205-jdbc42",
  "com.typesafe.play" %% "play-slick" % "2.0.0-M1",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0-M1",
  "org.scalatestplus" %% "play" % "1.4.0-M3" % "test",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "com.pauldijou" %% "jwt-play" % "0.4.1"

)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

routesGenerator := InjectedRoutesGenerator

