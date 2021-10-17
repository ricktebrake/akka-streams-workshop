ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "nl.quintor"

val AkkaVersion = "2.6.16"

lazy val root = (project in file("."))
  .settings(
    name := "akka-streams-workshop-scala",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.10",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,
      "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % "3.0.3"
    )
  )