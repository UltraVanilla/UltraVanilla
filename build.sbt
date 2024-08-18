scalaVersion := "3.4.2"
organization := "world.ultravanilla"
version := "1.51"
name := "UltraVanilla"
resolvers ++= List(
    "jitpack.io" at "https://jitpack.io",
    "papermc" at "https://papermc.io/repo/repository/maven-public/",
    "dmulloy2-repo" at "https://repo.dmulloy2.net/nexus/repository/public/",
    "jcenter" at "https://jcenter.bintray.com",
    "m2-dv8tion" at "https://m2.dv8tion.net/releases",
)
libraryDependencies ++= List(
    "io.papermc.paper" % "paper-api" % "1.21.1-R0.1-SNAPSHOT" % Provided,
    "net.luckperms" % "api" % "5.3" % Provided,
    "com.zaxxer" % "HikariCP" % "5.0.1",
    "net.dv8tion" % "JDA" % "4.3.0_277" exclude("club.minnced", "opus-java")
)

javacOptions ++= Seq("-source", "21", "-target", "21")

Compile / resourceGenerators += Def.task {
    val file = (Compile / resourceManaged).value / "plugin.yml"
    val pluginConfig = scala.io.Source.fromFile("plugin.yml").getLines.mkString("\n")
    val contents = pluginConfig.replace("@VERSION@", version.value)
    IO.write(file, contents)
    Seq(file)
}.taskValue

assembly / assemblyMergeStrategy := {
    case "module-info.class" => MergeStrategy.first
    case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
}

