package models

import org.scalatestplus.play.PlaySpec

/**
  * Created by Neelaksh on 15/8/17.
  */
class AssignmentRepositoryTest extends PlaySpec {
  private val userRepo = new ModelsTest[AssignmentRepository]
  private val newAssignment = new Assignment("scala03", "another assignment")
  "AssignmentRepository" should {
    "get all assignments" in {
      val storeResult = userRepo.result(userRepo.repository.getAllAssignments())
      storeResult.head.title mustEqual "scala101"
    }

    "add assignment" in {
      val storeResult = userRepo.result(userRepo.repository.addAssignment(newAssignment))
      storeResult mustEqual true
    }

    "delete assignment" in {
      val storeResult = userRepo.result(userRepo.repository.deleteAssignment(3))
      storeResult mustEqual true
    }
  }
}
