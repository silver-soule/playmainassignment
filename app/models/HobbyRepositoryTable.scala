package models

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.Future

/**
  * Created by Neelaksh on 12/8/17.
  */


class HobbyRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HobbyRepositoryTable {

  import driver.api._

  def getAllHobbies(): Future[List[Hobby]] = {
    db.run(hobbyQuery.to[List].result)
  }

}

trait HobbyRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val hobbyQuery: TableQuery[HobbyTable] = TableQuery[HobbyTable]

  class HobbyTable(tag: Tag) extends Table[Hobby](tag, "hobby") {

    def id: Rep[Int] = column[Int]("id")

    def name: Rep[String] = column[String]("hobbyname", O.PrimaryKey)

    def * : ProvenShape[Hobby] = (id, name) <> (Hobby.tupled, Hobby.unapply)

  }

}

case class Hobby(id: Int, name: String)
