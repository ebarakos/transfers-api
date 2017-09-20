name := """transfers-api"""

scalaVersion := "2.11.7"

resolvers ++= Seq(
  Resolver.jcenterRepo
)

libraryDependencies ++= Seq(
  filters,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "org.mockito" % "mockito-core" % "1.8.5" % Test,
  "net.codingwell" %% "scala-guice" % "4.1.0",
  "com.iheart" %% "play-swagger" % "0.2.1-PLAY2.5",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "com.h2database" % "h2" % "1.4.192"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)
