import com.typesafe.sbt.SbtNativePackager.autoImport._

name := "chaberi"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.6"

resolvers ++= Seq(
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
  Resolver.sonatypeRepo("public")
)

libraryDependencies ++= Seq(
  jdbc,
  cache,
  filters,
  "org.scalikejdbc" %% "scalikejdbc" % "2.2.7",
  "org.scalikejdbc" %% "scalikejdbc-config" % "2.2.7",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.4.0",
  "org.scalikejdbc" %% "scalikejdbc-test" % "2.2.7" % "test",
  "org.skinny-framework" %% "skinny-orm" % "1.3.+",
  "org.skinny-framework" %% "skinny-test" % "1.3.+" % "test",
  "org.scalatestplus" %% "play" % "1.4.0-M3" % "test",
  "org.scalaz" %% "scalaz-core" % "7.1.3",
  "com.github.nscala-time" %% "nscala-time" % "2.0.0",
  "commons-io" % "commons-io" % "2.4",
  "mysql" % "mysql-connector-java" % "5.1.34",
  "nu.validator.htmlparser" % "htmlparser" % "1.4",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
  "net.sourceforge.htmlunit" % "htmlunit" % "2.15",
  "com.github.tototoshi" % "play-json-naming_2.11" % "1.0.0",
  "com.amazonaws" % "aws-java-sdk" % "1.10.11",
  "jp.t2v" %% "play2-auth" % "0.14.0",
  "jp.t2v" %% "play2-auth-test" % "0.14.0" % "test",
  "commons-io" % "commons-io" % "2.4",
  "joda-time" % "joda-time" % "2.4",
  "org.joda" % "joda-convert" % "1.6",
  "jmagick" % "jmagick" % "6.6.9",
  "nu.validator.htmlparser" % "htmlparser" % "1.4",
  "org.julienrf" %% "play-json-variants" % "2.0",
  "org.imgscalr" % "imgscalr-lib" % "4.2",
  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "org.pegdown" % "pegdown" % "1.5.0" % "test"
)

//routesImport ++= Seq(
//  "controllers.QueryBindable._",
//  "models._"
//)

javaOptions in Test += "-Dconfig.file=conf/application-test.conf"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
