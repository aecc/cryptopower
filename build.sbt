name := "CryptoPower"

version := "0.1"

scalaVersion := "2.12.4"

mainClass := Some("org.aecc.crypto.Main")

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"

libraryDependencies += "joda-time" % "joda-time" % "2.9.9"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.0"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.6"

libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.5.6"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.4"

libraryDependencies += "org.apache.directory.studio" % "org.apache.commons.codec" % "1.8"

libraryDependencies += "org.apache.commons" % "commons-text" % "1.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"


