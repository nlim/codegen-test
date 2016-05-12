val slickVersion = "3.1.1"

lazy val mainProject = Project(
  id= "codegen-test",
  base=file("."),
  settings = Defaults.defaultSettings ++ Seq(
    scalaVersion := "2.11.8",
    libraryDependencies ++= List(
      "com.typesafe.slick" %% "slick" % slickVersion,
      "com.typesafe.slick" %% "slick-codegen" % slickVersion,
      "org.slf4j" % "slf4j-nop" % "1.7.19",
      "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
      "com.zaxxer" % "HikariCP" % "2.4.1"
    ),
    slick <<= slickCodeGenTask, // register manual sbt command
    sourceGenerators in Compile <+= slickCodeGenTask // register automatic code generation on every compile, remove for only manual use
  )
)

// code generation task

lazy val slick = TaskKey[Seq[File]]("gen-tables")
lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
  val outputDir = dir.getPath // place generated files in sbt's managed sources folder
  val slickDriver = "slick.driver.PostgresDriver"
  val jdbcDriver  = "org.postgresql.Driver"
  val url         = "jdbc:postgresql://localhost:5432/scalasyd"
  val pkg         = "tables"
  val fname       = outputDir + "/tables/Tables.scala"
  toError(r.run("slick.codegen.SourceCodeGenerator", cp.files, Array(slickDriver, jdbcDriver, url, outputDir, pkg, "root", ""), s.log))
  Seq(file(fname))
}
