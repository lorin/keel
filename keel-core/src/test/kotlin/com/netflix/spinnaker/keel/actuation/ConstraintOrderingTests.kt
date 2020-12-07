package com.netflix.spinnaker.keel.actuation

import com.netflix.spinnaker.keel.api.plugins.ConstraintEvaluator
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest

// TODO: Figure out how to do the spring boot magic that injects here
@SpringBootTest
class ConstraintOrderingTests(
  val constraints: List<ConstraintEvaluator<*>>
){

  @Test
  fun shouldDoTheRightThing() {

  }
}
