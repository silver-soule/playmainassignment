package models

import com.google.inject.Inject
import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * Created by Neelaksh on 10/8/17.
  */

class UserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserRepositoryTable {

  import driver.api._

/*  def store(user: User): Future[Boolean] = {
    db.run(userQuery += user) map (_ > 0)
  }*/

  def store(user: User): Future[Boolean] = {
    db.run(userQuery += user) map (_ > 0)
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
      .result.headOption.map{
      useropt=>
        useropt.fold(useropt) { user=>
          if(BCrypt.checkpw(password, user.password)) useropt else  None
          }
  })
  }

}

trait UserRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val userQuery: TableQuery[UserTable] = TableQuery[UserTable]

  class UserTable(tag: Tag) extends Table[User](tag, "userinfo") {
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)

    def name = column[String]("name")

    def middleName = column[Option[String]]("middlename")

    def lastName = column[String]("lastname")

    def mobileNumber = column[Long]("mobilenumber")

    def emailId = column[String]("emailid")

    def password = column[String]("password")

    def gender = column[String]("gender")

    def age = column[Int]("age")

    def * = (name, middleName, lastName, mobileNumber, emailId, password, gender, age, id) <> (User.tupled, User.unapply)

  }

}

case class User(name: String, middleName: Option[String], lastName: String, mobileNumber: Long
                , emailId: String, password: String, gender: String, age: Int, id: Long = 0)
