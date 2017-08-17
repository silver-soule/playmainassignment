package models

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.{PrimaryKey, ProvenShape}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Neelaksh on 12/8/17.
  */

class HobbyToUserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HobbyToUserRepositoryTable {

  import driver.api._

  def update(hobby: List[HobbyToEmail], emailId: String): Future[Boolean] = {
    db.run(hobbyToUserQuery.filter(_.emailId === emailId).delete)
    db.run(hobbyToUserQuery ++= hobby).map(count => count.fold(false)(_ => true))
  }

  def getHobbies(emailId: String): Future[List[Int]] = {
    val hobbyIds = hobbyToUserQuery.filter(_.emailId === emailId).map(_.hobbyId)
    db.run(hobbyIds.to[List].result)
  }

}

trait HobbyToUserRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val hobbyToUserQuery: TableQuery[HobbyTable] = TableQuery[HobbyTable]

  class HobbyTable(tag: Tag) extends Table[HobbyToEmail](tag, "hobbytouser") {

    def hobbyId: Rep[Int] = column[Int]("hobbyid")

    def emailId: Rep[String] = column[String]("useremailid")

    def * :ProvenShape[HobbyToEmail] = (hobbyId, emailId) <> (HobbyToEmail.tupled, HobbyToEmail.unapply)

    def pk: PrimaryKey = primaryKey("pk_a", (hobbyId, emailId))

  }
}

case class HobbyToEmail(hobbyId: Int, emailId: String)
