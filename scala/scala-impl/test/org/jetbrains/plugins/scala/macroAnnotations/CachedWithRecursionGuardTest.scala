package org.jetbrains.plugins.scala.macroAnnotations

import java.util.concurrent.atomic.AtomicInteger

import com.intellij.psi.util.PsiModificationTracker
import org.junit.Assert._

/**
  * Author: Svyatoslav Ilinskiy
  * Date: 9/29/15.
  */
class CachedWithRecursionGuardTest extends CachedWithRecursionGuardTestBase {
  def testWithoutParameters(): Unit = {
    class Elem extends CachedMockPsiElement {
      var depth = 0
      @CachedWithRecursionGuard(this, Right("Failure"), PsiModificationTracker.MODIFICATION_COUNT)
      def recursiveFunction: Either[Long, String] = {
        if (depth > 0) recursiveFunction
        else Left(System.currentTimeMillis())
      }
    }

    val elem = new Elem
    val firstRes: Either[Long, String] = elem.recursiveFunction
    assertTrue(firstRes.isLeft)
    elem.depth = 1
    assertEquals(firstRes, elem.recursiveFunction)

    incModCount(getProject)

    assertEquals(Right("Failure"), elem.recursiveFunction)

    elem.depth = 0
    val secondRes = elem.recursiveFunction

    assertNotEquals(firstRes, secondRes)
    assertEquals(secondRes, elem.recursiveFunction)
  }

  def testWithParameters(): Unit = {
    object Elem extends CachedMockPsiElement {
      val counter = new AtomicInteger(0)

      @CachedWithRecursionGuard(this, "Failure", PsiModificationTracker.MODIFICATION_COUNT)
      def recursiveFunction(d: Option[Int], depth: Int = 0): String = {
        d match {
          case Some(value) => (counter.getAndIncrement() + value).toString
          case _ if depth > 2 => "Blargle"
          case _ =>
            val res = recursiveFunction(None, depth)
            res
        }
      }
    }

    assertEquals("Blargle", Elem.recursiveFunction(None, depth = 3))
    assertEquals("Failure", Elem.recursiveFunction(None))
    assertEquals("1", Elem.recursiveFunction(Some(1)))
    assertEquals("1", Elem.recursiveFunction(Some(1)))

    incModCount(getProject)

    assertEquals("2", Elem.recursiveFunction(Some(1)))
  }
}
