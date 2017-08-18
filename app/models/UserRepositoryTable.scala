package models

import com.google.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Neelaksh on 10/8/17.
  */
@Singleton
class UserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserRepositoryTable {

  import driver.api._

  def store(user: User): Future[Boolean] = {
    db.run(userQuery += user) map (_ > 0)
  }

  def getAllNormalUsers(): Future[List[User]] = {
    db.run(userQuery.filter(_.isAdmin === false).to[List].sortBy(_.firstName).result)
  }

  def checkIfExists(email: String): Future[Boolean] = {
    db.run(userQuery.filter(user => user.emailId === email).to[List].result).map {
      user => user.headOption.fold(false)(_ => true)
    }
  }

  def getUserData(email: String): Future[Option[User]] = {
    db.run(userQuery.filter(user => user.emailId === email).result.headOption)
  }

  def updateUserData(user: UserWithoutPassword): Future[Boolean] = {
    db.run(userQuery.filter(_.emailId === user.emailId).map(user => (user.firstName, user.middleName, user.lastName, user.mobileNumber,
      user.gender, user.age)).update(user.firstName, user.middleName, user.lastName, user.mobileNumber
      , user.gender, user.age)) map (_ > 0)
  }

  def setPermissions(emailId: String, status: Boolean): Future[Boolean] = {
    db.run(userQuery.filter(_.emailId === emailId.trim).map {
      _.isEnabled
    }.update(status)) map (_ > 0)
  }

  def updatePassword(emailId: String, password: String): Future[Boolean] = {
    db.run(userQuery.filter(_.emailId === emailId).map(_.password).update(password)) map (_ > 0)
  }
}

trait UserRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val userQuery: TableQuery[UserTable] = TableQuery[UserTable]

  class UserTable(tag: Tag) extends Table[User](tag, "userinfo") {

    def id: Rep[Int] = column[Int]("id", O.AutoInc)

    def firstName: Rep[String] = column[String]("firstname")

    def middleName: Rep[Option[String]] = column[Option[String]]("middlename")

    def lastName: Rep[String] = column[String]("lastname")

    def mobileNumber: Rep[Long] = column[Long]("mobilenumber")

    def emailId: Rep[String] = column[String]("emailid",O.PrimaryKey)

    def password: Rep[String] = column[String]("password")

    def gender: Rep[String] = column[String]("gender")

    def age: Rep[Int] = column[Int]("age")

    def isEnabled: Rep[Boolean] = column[Boolean]("isenabled")

    def isAdmin: Rep[Boolean] = column[Boolean]("isadmin")

    def * : ProvenShape[User] = (firstName, middleName, lastName, mobileNumber, emailId, password, gender,
                                age, id, isEnabled, isAdmin) <> (User.tupled, User.unapply)

  }

}

case class User(firstName: String, middleName: Option[String], lastName: String, mobileNumber: Long
                , emailId: String, password: String, gender: String, age: Int, id: Int = 0, isEnabled: Boolean = true, isAdmin: Boolean = false)

case class UserWithoutPassword(firstName: String, middleName: Option[String], lastName: String, mobileNumber: Long
                               , emailId: String, gender: String, age: Int)

case class MinUser(firstName: String, emailId: String, isEnabled: Boolean)
