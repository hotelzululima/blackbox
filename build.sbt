name := "blackbox"

version := "1.0"

lazy val `blackbox` = (project in file(".")).
  enablePlugins(PlayScala).
  settings(javaOptions in Test += "-Dconfig.file=conf/application-test.conf")

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  ws,
  specs2 % Test,
  "org.postgresql" % "postgresql" % "9.4-1205-jdbc42",
  "com.typesafe.play" %% "play-slick" % "2.0.0-M1",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0-M1"
)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

routesGenerator := InjectedRoutesGenerator
