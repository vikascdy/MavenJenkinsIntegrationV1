// -----------------------------------------------------------------------------
// Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------

package com.edifecs.build.plugin

import sbt._
import sbt.Keys._
import java.util.zip.Deflater
import java.util.zip.GZIPOutputStream
import org.kamranzafar.jtar.TarOutputStream
import org.kamranzafar.jtar.TarEntry
import scala.collection.mutable
import java.io._
import java.util.zip.{ ZipEntry, ZipOutputStream }
import Classpaths.managedJars
import com.earldouglas.xsbtwebplugin.PluginKeys._
import sbt.Task
import sbt.LocalProject
import sbtassembly.Plugin.AssemblyKeys._
import sbt.Task
import sbt.LocalProject
import sys.process._
import java.net.URL
import java.io.File

/**
 * Bundle SBT plugin does two things. Builds ZIP distributions with required libs, as well as puts together
 * the final distribution packages for applications that include all UI, service, jobs, pipelines, and manifest
 * information.
 */
object Bundle extends sbt.Plugin {

  private case class ModuleEntry(org: String,
                                 name: String,
                                 revision: String,
                                 classifier: Option[String],
                                 originalFileName: String,
                                 projectRef: ProjectRef) {
    private def classifierSuffix = classifier.map("-" + _).getOrElse("")
    override def toString = "%s:%s:%s%s".format(org, name, revision, classifierSuffix)
    def jarName = "%s-%s%s.jar".format(name, revision, classifierSuffix)
    def fullJarName = "%s.%s-%s%s.jar".format(org, name, revision, classifierSuffix)
    def noVersionJarName = "%s.%s%s.jar".format(org, name, classifierSuffix)
  }

  private implicit def moduleEntryOrdering = Ordering.by[ModuleEntry, (String, String, String, Option[String])](m => (m.org, m.name, m.revision, m.classifier))

  val runtimeFilter = ScopeFilter(inAnyProject, inConfigurations(Runtime))

  // Defined Externally Available Tasks
  val bundle = TaskKey[File]("bundle", "create a complete bundle with all resources bundled")

  // TODO: Also have this create a ZIP file version
  val bundleArchive = TaskKey[File]("bundle-archive", "create a tar.gz archive of the bundle package")
  val bundleZipArchive = TaskKey[File]("bundle-zip-archive", "create a zip archive of the bundle package")


  val bundleHtml = TaskKey[File]("bundle-html", "bundle the html content of the project")
  val bundleLib = TaskKey[File]("bundle-lib", "bundle the lib files of the project")
  val bundleDist = TaskKey[File]("bundle-dist", "bundle the contents in the local projects bundle folder")

  val bundleApplicationsBundles = taskKey[Seq[(File, LocalProject)]]("create a distributable package of the libs of the project")

  // Internal Tasks
  val bundleUpdateReports = taskKey[Seq[(sbt.UpdateReport, ProjectRef)]]("only for retrieving dependent module names")
  val bundleLibJars = TaskKey[Seq[(File, ProjectRef)]]("bundle-lib-jars")
  val bundleAllUnmanagedJars = taskKey[Seq[(Classpath, ProjectRef)]]("all unmanaged jar files")


  // Configurable Settings
  val bundleDir = SettingKey[String]("bundle-dir", "output directory for the packaged application. Default = /target/bundle")
  val bundleServices = SettingKey[Map[String, Map[File, Project]]]("bundle-services", "allows the definition of individual services to be packaged")

  // Define Artifact output
  val bundleApplications = SettingKey[Seq[LocalProject]]("bundle-applications", "List of Applications to include.")
  val bundleLibraryApplications = SettingKey[Map[String, String]]("bundle-library-applications", "List of Applications to include.")

  val bundleArchivePrefix = SettingKey[String]("bundle-archive-prefix", "prefix of (prefix)-(version).tar.gz archive file name")

//  val bundleApplications = TaskKey[Seq[(File, ProjectRef)]]("bundle-applications", "Include Generated bundles in distribution")

  val bundleExclude = SettingKey[Seq[String]]("bundle-exclude", "specify projects to exclude when packaging")

  val bundleResourceDir = SettingKey[Seq[String]](s"bundle-resource-dir", "bundle resource directory. default = Seq($DEFAULT_RESOURCE_DIRECTORY)")
  val bundleHtmlDir = SettingKey[Seq[String]](s"bundle-html-dir", "bundle html directory. default = Seq(DEFAULT_HTML_DIRECTORY)")
  val bundleHtmlContextRoot = SettingKey[String]("bundle-html-context-root", "The context path for the web application")

  val bundleExtraClasspath = SettingKey[Map[String, Seq[String]]]("bundle-extra-classpath", "Extra classpath locations to include")
  val bundleExpandedClasspath = SettingKey[Boolean]("bundle-expanded-classpath", "Expands the wildcard classpath in launch scripts to point at specific libraries")
  val bundleJarNameConvention = SettingKey[String]("bundle-jarname-convention", "default: (artifact name)-(version).jar; original: original JAR name; full: (organization).(artifact name)-(version).jar; no-version: (organization).(artifact name).jar")
  val bundleDuplicateJarStrategy = SettingKey[String]("bundle-duplicate-jar-strategy", "deal with duplicate jars. default to use latest version. latest: use the jar with a higher version; exit: exit the task with error")

  val DEFAULT_RESOURCE_DIRECTORY = "src/main/bundle"
  val DEFAULT_HTML_DIRECTORY = "src/main/webapp"

  val HTML_DIST_DIR = "html"
  val WAR_DIST_DIR = "wars"

  //TODO: This needs to be automated
  val APPLICATION_BUNDLES_TO_EXCLUDE = Seq(
    "isc-api",
    "isc-annotation",
    "isc-jgroups",
    "configuration-api",
    "service-api",
    "esm-api",
    "coordination-service-api",
    "metric-api",
    "metric-core",
    "configuration-api",
    "content-repository-api",
    "coordination-service-api",
    "sm-classloader",
    "manifest",
    "exception"
  )

  lazy val bundleCommonSettings =  Seq[Def.Setting[_]](
    bundle <<= bundle dependsOn (compile in Compile),
    bundleDir := "bundle",
    bundleServices := Map.empty,
    bundleExclude := Seq.empty,
    bundleLibraryApplications := Map.empty,
    bundleResourceDir := Seq(DEFAULT_RESOURCE_DIRECTORY),
    bundleHtmlDir := Seq(DEFAULT_HTML_DIRECTORY),
    bundleHtmlContextRoot := baseDirectory.value.name,
    bundleExtraClasspath := Map.empty,
    bundleExpandedClasspath := false,
    bundleAllUnmanagedJars <<= (thisProjectRef, bundleServices, buildStructure, bundleExclude) flatMap getFromSelectedProjects(unmanagedJars in Compile),
    bundleLibJars <<= (thisProjectRef, bundleServices, buildStructure, bundleExclude) flatMap getFromSelectedProjects(packageBin in Runtime),
    bundleUpdateReports <<= (thisProjectRef, bundleServices, buildStructure, bundleExclude) flatMap getFromSelectedProjects(update),
    bundleJarNameConvention := "default",
    bundleDuplicateJarStrategy := "latest",
    (mappings in bundle) := Seq.empty,
    bundleDist := {
      // Setup Logger
      val out = streams.value

      // Set and Create the dist folder
      val distDir: File = createDistDir()(target.value, bundleDir.value, baseDirectory.value)

      // Copy project jars
      val base: File = baseDirectory.value

      // Copy resources in src/main/bundle folder
      val otherResourceDirs = bundleResourceDir.value.map( dir => base / dir )
      out.log.debug(s"bundled resource directories = ${otherResourceDirs.mkString(",")}")

      // Copy other Resources
      otherResourceDirs.foreach { otherResourceDir =>
        IO.copyDirectory(otherResourceDir, distDir, overwrite=true, preserveLastModified=true)
      }

      out.log.debug("done.")
      distDir
    },
    bundleArchivePrefix := name.value,
    bundleArchive := {
      val out = streams.value
      val targetDir: File = target.value
      val distDir: File = bundle.value
      val archiveStem = s"${bundleArchivePrefix.value}-${version.value}"
      val archiveName = s"${archiveStem}.tar.gz"
      out.log.debug("Generating " + rpath(baseDirectory.value, targetDir / archiveName))
      val tarfile = new TarOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(targetDir / archiveName)) {
        `def`.setLevel(Deflater.BEST_COMPRESSION)
      }))
      def tarEntry(src: File, dst: String) {
        val tarEntry = new TarEntry(src, dst)
        tarEntry.setIds(0, 0)
        tarEntry.setUserName("")
        tarEntry.setGroupName("")
        tarfile.putNextEntry(tarEntry)
      }
//      tarEntry(new File("."), archiveStem)
      val buffer = Array.fill(1024 * 1024)(0: Byte)
      def addFilesToTar(dir: File): Unit = dir.listFiles.foreach {
        file =>
          tarEntry(file, rpath(distDir, file))
          if (file.isDirectory) addFilesToTar(file)
          else {
            def copy(input: InputStream): Unit = input.read(buffer) match {
              case length if length < 0 => input.close()
              case length =>
                tarfile.write(buffer, 0, length)
                copy(input)
            }
            copy(new BufferedInputStream(new FileInputStream(file)))
          }
      }
      addFilesToTar(distDir)
      tarfile.close()

      val archiveFile: File = target.value / archiveName

      archiveFile
    },

    bundleZipArchive := {
      val targetDir: File = target.value
      val distDir: File = bundle.value
      val archiveStem = s"${bundleArchivePrefix.value}-${version.value}"

      // Generate ZIP File Distribution
      val archiveZipName = s"${archiveStem}.zip"
      createZipRecurse(null, distDir, (targetDir / archiveZipName).getAbsolutePath)

      val archiveFile: File = target.value / archiveZipName

      archiveFile
    },

    // disable publishing the main jar produced by `package`
    publishArtifact in (Compile, packageBin) := false,

    // disable publishing the main API jar
    publishArtifact in (Compile, packageDoc) := false,

    // disable publishing the main sources jar
    publishArtifact in (Compile, packageSrc) := false
  ) ++ addArtifact(Artifact("sm-dist", "dist", "zip", "dist"), bundleZipArchive) ++ addArtifact(Artifact("sm-dist", "dist", "tar.gz", "dist"), bundleArchive)


  lazy val bundleApplicationSettings = bundleCommonSettings ++ Seq[Def.Setting[_]](
    bundleExclude := APPLICATION_BUNDLES_TO_EXCLUDE,
    resourceDirectory in Test := baseDirectory.value / "src" / "main" / "bundle",
    bundle := {
      val out = streams.value

      out.log.info(s"Bundling project ${thisProject.value.id}.")

      var dist = bundleHtml.value
      dist = bundleLib.value
      dist = bundleDist.value



      dist
    },
    bundleHtml := {
      // Setup Logger
      val out = streams.value

      // Set and Create the dist folder
      val distDir: File = createDistDir()(target.value, bundleDir.value, baseDirectory.value)

      // Loop through service definitions
      for {
        (n: String, s: Map[File, Project]) <- bundleServices.value;
        (f: File, p: Project) <- s
      } yield {
        val path = file(thisProjectRef.value.build.getPath) / f.getPath / p.base.toString

        out.log.debug(s"Scanning project at ${path} for resources to bundle.")

        // Copy the HTML Content to the html folder
        val otherHtmlDirs = bundleHtmlDir.value.map(dir => path / dir)
        out.log.debug(s"Bundled resource directories = ${otherHtmlDirs.mkString(",")}")

        val htmlDistDir: File = distDir / HTML_DIST_DIR / bundleHtmlContextRoot.value

        out.log.debug(s"HTML Output directory = ${htmlDistDir}")

        // Copy other Html
        otherHtmlDirs.foreach {
          otherHtmlDir: File => {
            IO.copyDirectory(otherHtmlDir, htmlDistDir, overwrite = true, preserveLastModified = true)
          }
        }
      }

      distDir
    },
    bundleLib := {
      // Setup Logger
      val out = streams.value

      // Set and Create the dist folder
      val distDir: File = createDistDir()(target.value, bundleDir.value, baseDirectory.value)

      // Loop through service definitions
      for {
        (n: String, s: Map[File, Project]) <- bundleServices.value
      } yield {

        // Remove the dist directory
        distDir.delete()

        // Create target/bundle/lib folder
        val libDir = distDir / "services" / n / "lib"
        libDir.delete()
        libDir.mkdirs()

        // Copy project jars
        val base: File = baseDirectory.value
        out.log.debug("Copying libraries to " + rpath(base, libDir))
        val libs: Seq[File] = bundleLibJars.value.map(_._1)
        out.log.debug("project jars:\n- " + libs.map(path => rpath(base, path)).mkString("\n- "))
        libs.foreach(l => IO.copyFile(l, libDir / l.getName))

        val dependentJars = collection.immutable.SortedMap.empty[ModuleEntry, File] ++ (
          for {
            (r: sbt.UpdateReport, projectRef) <- bundleUpdateReports.value
            c <- r.configurations if c.configuration == "runtime"
            m <- c.modules
            (artifact, file) <- m.artifacts if DependencyFilter.allPass(c.configuration, m.module, artifact)}
          yield {
            val mid = m.module
            val me = ModuleEntry(mid.organization, mid.name, mid.revision, artifact.classifier, file.getName, projectRef)
            me -> file
          })// TODO: Investigate this filter: .filter(tuple => tuple._1.classifier == None)

        // check duplicate jars
        val distinctDpJars = dependentJars.foldLeft(mutable.HashMap.empty[String, (ModuleEntry, File)])((result, jar) => {
          val key = jar._1.noVersionJarName
          if (result.contains(key)) {
            val old = result(key)
            val oldVersion = old._1.revision
            val newVersion = jar._1.revision
            if (oldVersion != newVersion) {
              if (bundleDuplicateJarStrategy.value == "exit")
                sys.error(s"Version conflict on ${key}: [${old._1.projectRef.project}] using $oldVersion V.S. [${jar._1.projectRef.project}] using $newVersion")

              out.log.warn(s"Version conflict on ${key}: [${old._1.projectRef.project}] using $oldVersion V.S. [${jar._1.projectRef.project}] using $newVersion")
              val latest = if (oldVersion > newVersion) old else jar
              out.log.warn(s"\tUsing the latest version ${latest._1.fullJarName}")
              result += (key -> latest)
            }
          }else {
            result += (key -> jar)
          }
          result
        }).map(_._2).toMap

        // Copy dependent jars
        def resolveJarName(m: ModuleEntry, convention: String) = {
          convention match {
            case "original" => m.originalFileName
            case "full" => m.fullJarName
            case "no-version" => m.noVersionJarName
            case _ => m.jarName
          }
        }

        out.log.debug("project dependencies:\n- " + distinctDpJars.keys.mkString("\n- "))
        for ((m, f) <- distinctDpJars) {
          val targetFileName = resolveJarName(m, bundleJarNameConvention.value)
          IO.copyFile(f, libDir / targetFileName, true)
        }

        // Copy unmanaged jars in ${baseDir}/lib folder
        out.log.debug("Unmanaged dependencies:")
        for ((m, projectRef) <- bundleAllUnmanagedJars.value; um <- m; f = um.data) {
          out.log.debug("- " + f.getPath)
          IO.copyFile(f, libDir / f.getName, true)
        }

        // Copy explicitly added dependencies
        val mapped: Seq[(File, String)] = (mappings in bundle).value
        out.log.debug("explicit dependencies:")
        for ((file, path) <- mapped) {
          out.log.debug("- " + file.getPath)
          IO.copyFile(file, distDir / path, true)
        }

        def write(path: String, content: String) {
          val p = distDir / path
          out.log.debug("Generating %s".format(rpath(base, p)))
          IO.write(p, content)
        }

        val progVersion = version.value

        // Output the version number
        write("VERSION", "version:=" +

          progVersion + "\n")
      }

      out.log.debug("done.")
      distDir
    }
  )

  lazy val bundleSettings = bundleCommonSettings ++ Seq[Def.Setting[_]](
    bundleApplications := Seq.empty,
    bundleApplicationsBundles <<= (thisProjectRef, buildStructure) flatMap getDistFromSelectedProjects(bundleDist),
    bundle := {
      // Set and Create the dist folder
      val distDir: File = createDistDir()(target.value, bundleDir.value, baseDirectory.value)

      distDir.delete()

      var dist = bundleLib.value
      dist = bundleDist.value

      val out = streams.value

      // Load all configured bundles into the bundle archive
      bundleApplications.value
      for {
        p: LocalProject <- bundleApplications.value
      } yield {
        for {
          (f: File, p: LocalProject) <- bundleApplicationsBundles.value
        } yield {
          out.log.info("Packaging Bundle: " + f.getPath)
          // Copy the generated application bundles
          recursiveCopy(f, target.value / bundleDir.value / "ServiceManager" / "apps" / p.project)
        }
      }

      for {
        (name: String, url: String) <- bundleLibraryApplications.value
      } yield {
        out.log.info(s"Packaging Bundle: ${name} from url ${url}")

        IO.unzipURL(new URL(url), target.value / bundleDir.value / "ServiceManager" / "apps" / name)

      }

      dist
    },

    bundleLib := {
      // Setup Logger
      val out = streams.value

      // Set and Create the dist folder
      val distDir: File = createDistDir()(target.value, bundleDir.value, baseDirectory.value)

      val dependentJars = collection.immutable.SortedMap.empty[ModuleEntry, File] ++ (
        for {
          (r: sbt.UpdateReport, projectRef) <- bundleUpdateReports.value
          c <- r.configurations if c.configuration == "runtime"
          m <- c.modules
          (artifact, file) <- m.artifacts if DependencyFilter.allPass(c.configuration, m.module, artifact)}
        yield {
          val mid = m.module
          val me = ModuleEntry(mid.organization, mid.name, mid.revision, artifact.classifier, file.getName, projectRef)
          me -> file
        }).filter(tuple => tuple._1.classifier == None)

      // Create target/bundle/lib folder
      //TODO: Create Separation of Lib Files Dynamically
      val libDir = distDir / "ServiceManager" / "platform" / "core" / "lib" / "common"
      val binSystemDir = distDir / "ServiceManager" / "bin" / "system"
      libDir.delete()
      libDir.mkdirs()

      binSystemDir.mkdirs()

      // Copy project jars
      val base: File = baseDirectory.value
      out.log.debug("Copying libraries to " + rpath(base, libDir))
      val libs: Seq[File] = bundleLibJars.value.map(_._1)
      out.log.debug("project jars:")
      libs.foreach(l => {
        out.log.debug("    " + l.getPath)
        if ((l.getName.startsWith("sm-launcher") && !l.getName.startsWith("sm-launcher-api-"))
            || l.getName.startsWith("sm-classloader")) {
          IO.copyFile(l, binSystemDir / l.getName)
        } else {
          IO.copyFile(l, libDir / l.getName)
        }
      })

      // check duplicate jars
      val distinctDpJars = dependentJars.foldLeft(mutable.HashMap.empty[String, (ModuleEntry, File)])((result, jar) => {
        val key = jar._1.noVersionJarName
        if (result.contains(key)) {
          val old = result(key)
          val oldVersion = old._1.revision
          val newVersion = jar._1.revision
          if (oldVersion != newVersion) {
            if (bundleDuplicateJarStrategy.value == "exit")
              sys.error(s"Version conflict on ${key}: [${old._1.projectRef.project}] using $oldVersion V.S. [${jar._1.projectRef.project}] using $newVersion")

            out.log.warn(s"Version conflict on ${key}: [${old._1.projectRef.project}] using $oldVersion V.S. [${jar._1.projectRef.project}] using $newVersion")
            val latest = if (oldVersion > newVersion) old else jar
            out.log.warn(s"\tUsing the latest version ${latest._1.fullJarName}")
            result += (key -> latest)
          }
        }else {
          result += (key -> jar)
        }
        result
      }).map(_._2).toMap

      // Copy dependent jars
      def resolveJarName(m: ModuleEntry, convention: String) = {
        convention match {
          case "original" => m.originalFileName
          case "full" => m.fullJarName
          case "no-version" => m.noVersionJarName
          case _ => m.jarName
        }
      }

      out.log.debug("Project dependencies:\n    " + distinctDpJars.keys.mkString("\n    "))
      for ((m, f) <- distinctDpJars) {
        val projectLibPath = libDir.getParentFile / m.projectRef.project
        projectLibPath.mkdirs()
        val targetFileName = resolveJarName(m, bundleJarNameConvention.value)
        IO.copyFile(f, projectLibPath / targetFileName, true)
      }

      // Copy unmanaged jars in ${baseDir}/lib folder
      out.log.debug("Unmanaged dependencies:")
      for ((m, projectRef) <- bundleAllUnmanagedJars.value; um <- m; f = um.data) {
        out.log.debug("    " + f.getPath)
        IO.copyFile(f, libDir / f.getName, true)
      }

      // Copy explicitly added dependencies
      val mapped: Seq[(File, String)] = (mappings in bundle).value
      out.log.debug("Explicit dependencies:")
      for ((file, path) <- mapped) {
        out.log.debug("    " + file.getPath)
        IO.copyFile(file, distDir / path, true)
      }


      def write(path: String, content: String) {
        val p = distDir / path
        out.log.debug("Generating %s".format(rpath(base, p)))
        IO.write(p, content)
      }

      val progVersion = version.value

      // Output the version number
      write("VERSION", "version:=" + progVersion + "\n")

      distDir
    }
  )

  private def getFromSelectedProjects[T](targetTask: TaskKey[T])(currentProject: ProjectRef, services: Map[String, Map[File, Project]], structure: BuildStructure, exclude: Seq[String]): Task[Seq[(T, ProjectRef)]] = {
    def allProjectRefs(currentProject: ProjectRef): Seq[ProjectRef] = {
      def isExcluded(p: ProjectRef) = exclude.contains(p.project)
      val children = Project.getProject(currentProject, structure).toSeq.flatMap {
        p => p.uses
      }
      (currentProject +: (children flatMap (allProjectRefs(_)))) filterNot (isExcluded)
    }
    val projects = allProjectRefs(currentProject).distinct
    projects.map(p => (Def.task {((targetTask in p).value, p)}) evaluate structure.data).join
  }

  private def getWarsFromSelectedProjects[T](targetTask: TaskKey[T])(currentProject: ProjectRef, services: Map[String, Map[File, Project]], structure: BuildStructure, exclude: Seq[String]): Task[Seq[(T, ProjectRef)]] = {
    def allProjectRefs(currentProject: ProjectRef): Seq[ProjectRef] = {
      def isExcluded(p: ProjectRef) = !p.project.endsWith("-ui")
      val children = Project.getProject(currentProject, structure).toSeq.flatMap {
        p => p.uses
      }
      (currentProject +: (children)) filterNot (isExcluded)
    }
    val projects = allProjectRefs(currentProject).distinct
    projects.map(p => (Def.task {((targetTask in p).value, p)}) evaluate structure.data).join
  }

  private def getDistFromSelectedProjects[T](targetTask: TaskKey[T])(currentProject: ProjectRef, structure: BuildStructure): Task[Seq[(T, LocalProject)]] = {
    val projects = (bundleApplications in currentProject) evaluate structure.data
    projects.map(p => (Def.task {((targetTask in p).value, p)}) evaluate structure.data).join
  }

  private def rpath(base: File, f: RichFile) = f.relativeTo(base).getOrElse(f).toString

  private def createDistDir()(target: File, bundleDir: String, baseDirectory: File) : File = {

    // Set and Create the dist folder
    val distDir: File = target / bundleDir

    distDir.mkdirs()

    distDir
  }

  def recursiveCopy(fSource: File, fDest: File) {
    if (fSource.isDirectory()) {
      // A simple validation, if the destination is not exist then create it
      if (!fDest.exists()) {
        fDest.mkdirs();
      }

      // Create list of files and directories on the current source
      // Note: with the recursion 'fSource' changed accordingly
      val fList = fSource.list();

      for(index <- 0 to (fList.length-1)) {
        val dest = new File(fDest, fList(index));
        val source = new File(fSource, fList(index));

        // Recursion call take place here
        recursiveCopy(source, dest);
      }
    }
    else {
      // Found a file. Copy it into the destination, which is already created in 'if' condition above

      // Open a file for read and write (copy)
      val fInStream = new FileInputStream(fSource);
      val fOutStream = new FileOutputStream(fDest);

      // Read 2K at a time from the file
      val buffer = new Array[Byte](2048);

      // In each successful read, write back to the source
      Iterator
        .continually (fInStream.read(buffer))
        .takeWhile (-1 != )
        .foreach (read=>fOutStream.write(buffer, 0, read))

      // Safe exit
      if (fInStream != null) {
        fInStream.close();
      }

      if (fOutStream != null) {
        fOutStream.close();
      }
    }
  }

  private final val BUFFER: Int = 2048

  /**
   * Create a zip archive from a folder, recursively using default zip options.
   *
   * @param path           initial folder where to add files, if empty or null will include directly into zip file
   * @param sourceFolder   the folder to archive
   * @param destinationZip destination file
   */
  final def createZipRecurse(path: String, sourceFolder: File, destinationZip: String) {
    var zip: ZipOutputStream = null
    var fileWriter: FileOutputStream = null
    fileWriter = new FileOutputStream(destinationZip)
    zip = new ZipOutputStream(fileWriter)
    zipFolderRecurse(path, sourceFolder, zip)
    zip.flush
    zip.close
  }

  /**
   * Add a directory to a zip file.
   *
   * @param path      initial folder where to add files, if empty will include directly into zip file
   * @param srcFolder the folder to archive
   * @param zip       zip archive stream
   */
  def zipFolderRecurse(path: String, srcFolder: File, zip: ZipOutputStream) {
    val files: Array[File] = srcFolder.listFiles
    if (files == null) {
      throw new IOException("There wasn't any file found in " + srcFolder + "directory")
    }
    else {
      for (file <- files) {
        if (file.isDirectory) {
          if (path == null) {
            zipFolderRecurse(file.getName, file, zip)
          } else {
            zipFolderRecurse(path + "/" + file.getName, file, zip)
          }
        }
        else {
          addFile(path, file, zip)
        }
      }
    }
  }

  /**
   * Add a file to a zip archive.
   *
   * @param pathToFile path to file inside zip
   * @param file       the file to add
   * @param zip        zip archive stream
   */
  def addFile(pathToFile: String, file: File, zip: ZipOutputStream) {
    val buf: Array[Byte] = new Array[Byte](BUFFER)
    var len: Int = 0
    val in: FileInputStream = new FileInputStream(file)
    if (pathToFile == null) {
      zip.putNextEntry(new ZipEntry(file.getName))
    } else {
      zip.putNextEntry(new ZipEntry(pathToFile + "/" + file.getName))
    }
    while ((({
      len = in.read(buf); len
    })) > 0) {
      zip.write(buf, 0, len)
    }
    in.close
  }
}