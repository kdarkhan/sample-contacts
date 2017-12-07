name := """contacts-sample"""

version := "0.0.2"

lazy val root = (project in file("."))
    .settings(Seq(
      packageName in Docker := "contacts-sample",
      maintainer in Docker := "Darkhan Kubigenov",
      dockerExposedPorts := Seq(9000),
      dockerUsername := Some("kdarkhan"),
      dockerUpdateLatest := true,
      // disable doc generation
      publishArtifact in (Compile, packageDoc) := false,
      publishArtifact in packageDoc := false,
      sources in (Compile,doc) := Seq.empty
    ))
  .enablePlugins(PlayScala,DockerPlugin)

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
