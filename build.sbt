
name := """playmainassignment"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.postgresql" % "postgresql" % "42.1.4",
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % "test",
  "com.typesafe.slick" % "slick-codegen_2.11" % "3.1.0",
  "org.mockito" % "mockito-all" % "1.10.19" % "test",
  "com.h2database" % "h2" % "1.4.188",
  specs2 % Test,
  evolutions

)

javaOptions in Test += "-Dconfig.file=conf/test.conf"
coverageExcludedPackages := """controllers\..*Reverse.*;router.Routes.*;"""

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
