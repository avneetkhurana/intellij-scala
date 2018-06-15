package org.jetbrains.plugins.scala.components.libextensions

import org.jetbrains.plugins.scala.project.ProjectContext

import scala.reflect.ClassTag

class DynamicExtensionPoint[T](implicit val tag: ClassTag[T]) {

  def getExtensions(implicit context: ProjectContext): Seq[T] = {
    LibraryExtensionsManager.getInstance(context.project).getExtensions(tag.runtimeClass).asInstanceOf[Seq[T]]
  }

  def getExtension(implicit project: ProjectContext): T = getExtensions.head
}
