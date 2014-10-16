package com.edifecs.epp.isc.async

import com.edifecs.epp.isc.{Isc, ICommandCommunicator}
import com.edifecs.epp.security.SessionId

import scala.concurrent.ExecutionContext

class StatePropagatingExecutor(
  delegate: ExecutionContext,
  communicator: ICommandCommunicator
) extends ExecutionContext {

  override def execute(runnable: Runnable) =
    delegate.execute(new StatePropagatingRunnable(runnable,
      communicator = communicator,
      threadState = Isc.state,
      session = communicator.getSecurityManager.getSessionManager.getCurrentSession))

  override def reportFailure(t: Throwable) =
    delegate.reportFailure(t)
}

private class StatePropagatingRunnable(
  delegate: Runnable,
  communicator: ICommandCommunicator,
  threadState: Isc.ThreadState,
  session: SessionId
) extends Runnable {

  override def run() = {
    communicator.getSecurityManager.getSessionManager.registerCurrentSession(session)
    Isc.setState(threadState)
    delegate.run()
  }
}
