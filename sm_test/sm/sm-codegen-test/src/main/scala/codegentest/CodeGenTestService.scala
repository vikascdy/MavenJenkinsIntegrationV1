package codegentest

import com.edifecs.servicemanager.annotations._

@Service(
  name="Code Generation Test Service",
  version="1.0",
  description="Used to test the proxy class generator.")
trait CodeGenTestService {
  @Handler
  def handlerA: HandlerA

  @Handler
  def handlerB: HandlerB
}

