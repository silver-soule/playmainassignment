package models

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Neelaksh on 13/8/17.
  */

class AssignmentRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends AssignmentRepositoryTable {

  import driver.api._

  def addAssignment(assignment: Assignment): Future[Boolean] = {
    db.run(assignmentQuery += assignment) map (_ > 0)
  }

  def getAllAssignments(): Future[List[Assignment]] = {
    db.run(assignmentQuery.to[List].result)
  }

  def deleteAssignment(assignmentId: Int): Future[Boolean] = {
    db.run(assignmentQuery.filter(_.id === assignmentId).delete) map (_ > 0)
  }
}

trait AssignmentRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val assignmentQuery: TableQuery[AssignmentTable] = TableQuery[AssignmentTable]

  class AssignmentTable(tag: Tag) extends Table[Assignment](tag, "assignment") {

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def title: Rep[String] = column[String]("title")

    def description: Rep[String] = column[String]("description")

    def * :ProvenShape[Assignment] = (title, description, id) <> (Assignment.tupled, Assignment.unapply)

  }

}

case class Assignment(title: String, description: String, id: Int = 0)
