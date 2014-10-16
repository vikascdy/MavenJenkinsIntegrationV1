package codegentest

import com.edifecs.epp.isc.async.MessageFuture
import com.edifecs.epp.isc.annotations._

@CommandHandler(namespace="b")
trait HandlerB {
  
  @SyncCommand
  def alpha: String

  @AsyncCommand
  def beta: MessageFuture[String]
}

