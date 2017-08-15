package models

import org.scalatestplus.play._

/**
  * Created by Neelaksh on 11/8/17.
  */
class UserRepositoryTest extends PlaySpec {
  private val userRepo = new ModelsTest[UserRepository]
  val user = User("Neelakshadaddaa", None, "Chauhan", 995407, "nilaxch1@gmail.com", "Potato123", "male", 21)
  "UserRepository" should {
    "store data" in {
      val storeResult = userRepo.result(userRepo.repository.store(user))
      storeResult mustEqual true
    }

    "get all non-admin users" in {
      val storeResult = userRepo.result(userRepo.repository.getAllNormalUsers())
      storeResult.length mustEqual 2
    }

    "check if user exists " in {
      val storeResult = userRepo.result(userRepo.repository.checkIfExists(user.emailId))
      storeResult mustEqual true
    }

    "get user if it exists" in {
      val storeResult = userRepo.result(userRepo.repository.getUserData(user.emailId))
      storeResult.head.emailId mustBe user.emailId
    }

    "update user data" in {
      val userWithoutPassword = UserWithoutPassword("abc", None, "Potato", 1234, "nilaxch12@gmail.com", "male", 21)
      val storeResult = userRepo.result(userRepo.repository.updateUserData(userWithoutPassword))
      storeResult mustEqual true
    }

    "fail to update user data " in {
      val userWithoutPassword = UserWithoutPassword("abc", None, "Potato", 1234, "nilaxch114442@gmail.com", "male", 21)
      val storeResult = userRepo.result(userRepo.repository.updateUserData(userWithoutPassword))
      storeResult mustEqual false
    }

    "flip permissions" in {
      val storeResult = userRepo.result(userRepo.repository.setPermissions("nilaxch123@gmail.com", false))
      storeResult mustEqual true
    }

    "fail to change permissions" in {
      val storeResult = userRepo.result(userRepo.repository.setPermissions("nilaxch12356@gmail.com", false))
      storeResult mustEqual false
    }

    "update password" in {
      val storeResult = userRepo.result(userRepo.repository.updatePassword("nilaxch123@gmail.com", "Potato123"))
      storeResult mustEqual true
    }
  }

}
