val commonSettings = Seq(
  organization := "com.thenewmotion",
  licenses += ("Apache License, Version 2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
)

val specs2 = "org.specs2" %% "specs2-core" % "3.8.7" % "test"

val `core` = project
  .enablePlugins(OssLibPlugin)
  .settings(
    name := "mobilityid",
    commonSettings,
    libraryDependencies ++= Seq(
      specs2
    )
  )

val `interpolators` = project
  .enablePlugins(OssLibPlugin)
  .dependsOn(`core`)
  .settings(
    name := "mobilityid-interpolators",
    commonSettings,
    libraryDependencies ++= Seq(
      "com.propensive" %% "contextual" % "1.0.0",
      specs2
    )
  )