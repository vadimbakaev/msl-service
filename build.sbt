name := """msl-service"""

version := "0.0.2"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play"  % "1.5.1" % Test,
  "com.github.etaty"       %% "rediscala"           % "1.7.0",
  "org.reactivemongo"      %% "play2-reactivemongo" % "0.12.0"
)

