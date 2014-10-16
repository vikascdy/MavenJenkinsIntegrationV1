package codegentest

import com.edifecs.epp.isc.async.MessageFuture
import com.edifecs.epp.isc.annotations._

@CommandHandler
trait HandlerA {
  
  @SyncCommand(name="foo")
  def fooCommand(
    @Arg(name="arg") arg: String
  ): String

  @AsyncCommand(name="bar")
  def barCommand(
    @Arg(name="arg") arg: String
  ): MessageFuture[String]

  @SyncCommand(name="arrayCommand")
  def arrayCommand(
    @Arg(name="charArray") charArray: Array[Char],
    @Arg(name="stringArray") stringArray: Array[String]
  ): Array[Int]
}
