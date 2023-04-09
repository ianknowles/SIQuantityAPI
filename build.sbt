ThisBuild / scalaVersion := "2.13.10"

ThisBuild / version := "0.1-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """SIQuantityAPI""",
    libraryDependencies ++= Seq(
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
			"com.typesafe.play" %% "play-slick" % "5.1.0",
			"com.typesafe.play" %% "play-slick-evolutions" % "5.1.0",
			"org.xerial" % "sqlite-jdbc" % "3.40.1.0",
    )
  )