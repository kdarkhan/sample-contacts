name := """play-scala-slick-example"""

version := "2.6.x"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies += guice
libraryDependencies += "com.typesafe.play" %% "play-slick" %  "3.0.2"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "3.0.2"

//libraryDependencies += "com.h2database" % "h2" % "1.4.196"
//libraryDependencies += "postgresql" % "postgresql" % "9.3-1102.jdbc41"

libraryDependencies += specs2 % Test


//resolvers +=
//  "Clojars" at "http://clojars.org/repo/"
