organization := "com.thenewmotion"

name := "mobilityid"

scalaVersion := "2.10.4"

crossScalaVersions := Seq("2.10.4", "2.11.2")

ReleaseKeys.crossBuild := true

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "Nexus" at "http://nexus.thenewmotion.com/content/groups/public/",
  "Nexus Snapshots" at "http://nexus.thenewmotion.com/content/repositories/snapshots"
)

publishTo := {
  val nexus = "http://nexus.thenewmotion.com/content/repositories/"
  if (isSnapshot.value) Some("snapshots" at nexus + "snapshots-public")
  else Some("releases"  at nexus + "releases-public")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

releaseSettings

libraryDependencies ++= {
  Seq(
    "org.specs2"                     %% "specs2"               % "2.3.13" % "test"
  )
}

licenses += ("Apache License, Version 2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))