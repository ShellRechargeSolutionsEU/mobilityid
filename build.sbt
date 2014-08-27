organization := "com.thenewmotion"

version := "0.1-SNAPSHOT"

name := "iso-id-utils"

scalaVersion := "2.10.4"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/",
  "Nexus" at "http://nexus.thenewmotion.com/content/groups/public/",
  "Nexus Snapshots" at "http://nexus.thenewmotion.com/content/repositories/snapshots"
)

publishTo <<= version { v: String =>
  val nexus = "http://nexus.thenewmotion.com/content/repositories/"
  if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "snapshots")
  else                             Some("releases"  at nexus + "releases")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

libraryDependencies ++= {
  Seq(
    "org.scalaz"                     %% "scalaz-core"          % "7.0.6",
    "org.specs2"                     %% "specs2"               % "2.3.13" % "test"
  )
}