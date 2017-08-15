
package models

import org.scalatestplus.play.PlaySpec

/**
  * Created by Neelaksh on 15/8/17.
  */
class HobbyRepositoryTableTest extends PlaySpec {
  private val userRepo = new ModelsTest[HobbyRepository]

  "HobbyRepository" should {
    "get all hobbies" in {
      val storeResult = userRepo.result(userRepo.repository.getAllHobbies())
      storeResult.head.name mustEqual "dancing"
    }
  }
}