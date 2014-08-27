organization := "com.thenewmotion"

version := "0.1-SNAPSHOT"

name := "iso-id-utils"

scalaVersion := "2.10.4"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "Nexus" at "http://nexus.thenewmotion.com/content/groups/public/",
  "Nexus Snapshots" at "http://nexus.thenewmotion.com/content/repositories/snapshots"
)

publishTo <<= version { v: String =>
  if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at "http://nexus.thenewmotion.com/content/repositories/snapshots-public")
  else                             Some("releases"  at "http://nexus.thenewmotion.com/content/repositories/releases-public")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

releaseSettings

libraryDependencies ++= {
  Seq(
    "org.specs2"                     %% "specs2"               % "2.3.13" % "test"
  )
}