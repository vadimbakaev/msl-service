name := """msl-service"""

version := "0.0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += jdbc
libraryDependencies += cache
libraryDependencies += ws
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
libraryDependencies += "com.github.etaty" %% "rediscala" % "1.7.0"
libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.12.0"

