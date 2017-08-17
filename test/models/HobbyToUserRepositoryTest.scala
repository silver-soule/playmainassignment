package models

import org.scalatestplus.play.PlaySpec

/**
  * Created by Neelaksh on 15/8/17.
  */

class HobbyToUserRepositoryTest extends PlaySpec {
  private val userRepo = new ModelsTest[HobbyToUserRepository]
  val user = User("Neelakshadaddaa", None, "Chauhan", 995407, "nilaxch1@gmail.com", "Potato123", "male", 21)
  val hobbyToUser = List(HobbyToEmail(1, user.emailId))
  "HobbyToUserRepository" should {

    "update hobbies" in {
      val storeResult = userRepo.result(userRepo.repository.update(hobbyToUser, user.emailId))
      storeResult mustEqual true
    }

    "get hobbies" in {
      val storeResult = userRepo.result(userRepo.repository.getHobbies(user.emailId))
      storeResult.head mustEqual 1
    }

  }
}
