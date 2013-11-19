import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "infopanel"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "org.apache.xmlgraphics" % "batik-transcoder" % "1.7",
    "org.apache.xmlgraphics" % "batik-codec" % "1.7",
          "com.google.apis" % "google-api-services-calendar" % "v3-rev59-1.17.0-rc",
          "com.google.http-client" % "google-http-client-jackson2" % "1.17.0-rc",
          "com.google.oauth-client" % "google-oauth-client-jetty" % "1.17.0-rc"

  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}

