val commonSettings = Seq(
  organization := "com.thenewmotion",
  licenses += ("Apache License, Version 2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
)

val specs2 = "org.specs2" %% "specs2-core" % "4.4.1" % "test"

lazy val scala212 = "2.12.8"
lazy val scala211 = "2.11.12"
lazy val supportedScalaVersions = List(scala212, scala211)

val `core` = project
  .enablePlugins(OssLibPlugin)
  .settings(
    name := "mobilityid",
    commonSettings,
    libraryDependencies ++= Seq(
      specs2
    ),
    crossScalaVersions := supportedScalaVersions
  )

val `interpolators` = project
  .enablePlugins(OssLibPlugin)
  .dependsOn(`core`)
  .settings(
    name := "mobilityid-interpolators",
    commonSettings,
    libraryDependencies ++= Seq(
      "com.propensive" %% "contextual" % "1.1.0",
      specs2
    )
  )

val `mobilityid` =
  project.in(file("."))
    .enablePlugins(OssLibPlugin)
    .aggregate(
      `core`,
      `interpolators`)
    .settings(
      publish := {}
    )
