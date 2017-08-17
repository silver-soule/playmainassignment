package util

import org.mindrot.jbcrypt.BCrypt

/**
  * Created by Neelaksh on 15/8/17.
  */
class Hasher {
  def hashPassword(password:String):String = {
    BCrypt.hashpw(password,BCrypt.gensalt())
  }

  def checkPassword(plainPassword:String, hashedPassword:String) : Boolean = {
    BCrypt.checkpw(plainPassword,hashedPassword)
  }
}
