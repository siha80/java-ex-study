package com.siha.study

import org.scalatest.{WordSpec}

/**
  * Created by 1002375 on 2017. 5. 15..
  */
class ScalaUnitTest extends WordSpec {
  "A Set" when {
    "empty" should {
      "have size 0" in {
        assert(Set.empty.size == 0)
      }
    }
  }
}
