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

class HobbyToUserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HobbyToUserRepositoryTable with HobbyRepositoryTable {

  import driver.api._


  /**
    * NOTE => IMPROVE IMPLEMENTATION, CHECK IF COUNT MATCHES SIN UPDATE FUNCTION
    *
    * @param hobby
    * @return
    */
  def update(hobby: List[HobbyToEmail], emailId: String): Future[Boolean] = {
    db.run(hobbyToUserQuery.filter(_.emailId === emailId).delete)
    db.run(hobbyToUserQuery ++= hobby).map(count => count.fold(false)(_ => true))
  }


  def getHobbies(emailId: String): Future[List[Int]] = {
    val hobbyIds = for {(htou, h) <- hobbyToUserQuery.filter(_.emailId === emailId) join hobbyQuery on (_.hobbyId === _.id)} yield h.id
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
