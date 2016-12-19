package org.jetbrains.sbt.project.data.service

import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.project.ProjectData
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.util.io.FileUtil
import org.jetbrains.sbt.Sbt
import org.jetbrains.sbt.project.data.SbtBuildModuleNode
import org.jetbrains.sbt.project.module.SbtModule
import org.jetbrains.sbt.resolvers.indexes.ResolverIndex
import org.jetbrains.sbt.resolvers.{SbtIvyResolver, SbtMavenResolver, SbtResolver}

/**
 * @author Nikolay Obedin
 * @since 6/9/15.
 */
class SbtBuildModuleDataServiceTest extends ProjectDataServiceTestCase {

  import ExternalSystemDataDsl._

  private def generateProject(imports: Seq[String], resolvers: Set[SbtResolver]): DataNode[ProjectData] =
    new project {
      name := getProject.getName
      ideDirectoryPath := getProject.getBasePath
      linkedProjectPath := getProject.getBasePath
      modules += new javaModule {
        name := "Module 1"
        moduleFileDirectoryPath := getProject.getBasePath + "/module1"
        externalConfigPath := getProject.getBasePath + "/module1"
        arbitraryNodes += new SbtBuildModuleNode(imports, resolvers)
      }
    }.build.toDataNode


  def doTest(imports: Seq[String], resolvers: Set[SbtResolver]): Unit = {
    FileUtil.delete(ResolverIndex.DEFAULT_INDEXES_DIR)
    importProjectData(generateProject(imports, resolvers))
    val module = ModuleManager.getInstance(getProject).findModuleByName("Module 1")

    if (imports.nonEmpty)
      assert(SbtModule.getImportsFrom(module) == imports)
    else
      assert(SbtModule.getImportsFrom(module) == Sbt.DefaultImplicitImports)

    assert(SbtModule.getResolversFrom(module) == resolvers)
  }


  def testEmptyImportsAndResolvers(): Unit =
    doTest(Seq.empty, Set.empty)

  def testNonEmptyImports(): Unit =
    doTest(Seq("first import", "second import"), Set.empty)

  def testNonEmptyResolvers(): Unit =
    doTest(Seq.empty, Set(
      new SbtMavenResolver("maven resolver", "http:///nothing"),
      new SbtIvyResolver("ivy resolver", getProject.getBasePath)))

  def testNonEmptyImportsAndResolvers(): Unit =
    doTest(Seq("first import", "second import"), Set(
      new SbtMavenResolver("maven resolver", "http:///nothing"),
      new SbtIvyResolver("ivy resolver", getProject.getBasePath)))

  def testModuleIsNull(): Unit = {
    val testProject = new project {
      name := getProject.getName
      ideDirectoryPath := getProject.getBasePath
      linkedProjectPath := getProject.getBasePath
      arbitraryNodes += new SbtBuildModuleNode(Seq("some import"), Set.empty)
    }.build.toDataNode

    importProjectData(testProject)
  }
}