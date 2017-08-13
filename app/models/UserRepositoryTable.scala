package models

import com.google.inject.{Inject, Singleton}
import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * Created by Neelaksh on 10/8/17.
  */
@Singleton
class UserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserRepositoryTable {

  import driver.api._

  def store(user: User): Future[Boolean] = {
    val encryptedUser = user.copy(password = BCrypt.hashpw(user.password, BCrypt.gensalt()))
    db.run(userQuery += encryptedUser) map (_ > 0)
  }

  def getEmailId(email: String): Future[Option[String]] = {
    db.run(userQuery.filter(user => user.emailId === email).map(_.emailId).result.headOption)
  }

  def checkUser(email: String, password: String): Future[Boolean] = {
    db.run(userQuery.filter(user => user.emailId === email).map(_.password)
      .result.headOption.map(
      _.fold(false) {
        hashedPassword =>
          BCrypt.checkpw(password, hashedPassword)
      }
    ))
  }

  def getUserData(email: String, password: String): Future[Option[User]] = {
    db.run(userQuery.filter(user => user.emailId === email)
      .result.headOption.map {
      useropt =>
        useropt.fold(useropt) { user =>
          if (BCrypt.checkpw(password, user.password)) useropt else None
        }
    })
  }

  def getUserData(email: String): Future[Option[User]] = {
    db.run(userQuery.filter(user => user.emailId === email)
      .result.headOption.map {
      useropt=>useropt
    })
  }

  def updateUserData(user:UserWithoutPassword) : Future[Boolean] = {
    db.run(userQuery.filter(_.emailId === user.emailId).map(user=> (user.firstName,user.middleName,user.lastName,user.mobileNumber,
      user.gender,user.age)).update(user.firstName,user.middleName,user.lastName,user.mobileNumber
      ,user.gender,user.age)) map(_ > 0)
  }


}

trait UserRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val userQuery: TableQuery[UserTable] = TableQuery[UserTable]

  class UserTable(tag: Tag) extends Table[User](tag, "userinfo") {

    def id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)

    def firstName: Rep[String] = column[String]("firstname")

    def middleName: Rep[Option[String]] = column[Option[String]]("middlename")

    def lastName: Rep[String] = column[String]("lastname")

    def mobileNumber: Rep[Long] = column[Long]("mobilenumber")

    def emailId: Rep[String] = column[String]("emailid")

    def password: Rep[String] = column[String]("password")

    def gender: Rep[String] = column[String]("gender")

    def age: Rep[Int] = column[Int]("age")

    def isEnabled : Rep[Boolean] = column[Boolean]("isenabled")

    def isAdmin : Rep[Boolean] = column[Boolean]("isadmin")

    def * = (firstName, middleName, lastName, mobileNumber, emailId, password, gender, age, id,isEnabled,isAdmin) <> (User.tupled, User.unapply)

  }

}

case class User(firstName: String, middleName: Option[String], lastName: String, mobileNumber: Long
                , emailId: String, password: String, gender: String, age: Int, id: Int = 0,isEnabled : Boolean = true, isAdmin: Boolean = false)

case class UserWithoutPassword(firstName: String, middleName: Option[String], lastName: String, mobileNumber: Long
                               , emailId: String, gender: String, age: Int)
