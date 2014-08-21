organization := "com.thenewmotion"

version := "0.1-SNAPSHOT"

name := "evco-id-converter"

scalaVersion := "2.10.4"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/",
  Resolver.sonatypeRepo("releases"),
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
  val akkaVersion = "2.3.3"
  val sprayVersion = "1.3.1"
  Seq(
    "com.mchange"                  %  "c3p0"                 % "0.9.5-pre8",
    "org.slf4j"                % "slf4j-api"               % "1.7.7",
    "org.slf4j"                % "jcl-over-slf4j"          % "1.7.7",
    "org.slf4j"                % "jul-to-slf4j"            % "1.7.7",
    "com.typesafe.scala-logging"     %% "scala-logging-slf4j"     % "2.1.2",
    "ch.qos.logback"           % "logback-classic"         % "1.1.2",
    "org.apache.commons"       % "commons-math3"           % "3.3",
    "org.scalaz"            %% "scalaz-core"          % "7.0.6",
    "org.specs2"            %% "specs2"               % "2.3.12"        % "test"
  )
}

Revolver.settings.settings
