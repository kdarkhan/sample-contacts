name := """play-scala-slick-example"""

version := "2.6.x"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  guice,
  "com.typesafe.play" %% "play-slick" %  "3.0.2",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.2",
  "com.github.tminglei" %% "slick-pg" % "0.15.4",
  "org.mindrot" % "jbcrypt" % "0.4",
  ehcache,
  specs2 % Test
)
