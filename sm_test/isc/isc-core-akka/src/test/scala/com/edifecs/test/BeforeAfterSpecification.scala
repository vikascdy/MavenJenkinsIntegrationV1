package com.edifecs.test

import org.specs2.mutable._
import org.specs2.specification.{Step, Fragments}

// beforeAll/afterAll implementation taken from:
// http://stackoverflow.com/a/16952931
trait BeforeAfterSpecification extends Specification {

  // see http://bit.ly/11I9kFM (specs2 User Guide)
  override def map(fragments: =>Fragments) = 
    Step(beforeAll) ^ fragments ^ Step(afterAll)

  protected def beforeAll()
  protected def afterAll()
}
