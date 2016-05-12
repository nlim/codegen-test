object Tables extends {
  val profile = slick.driver.PostgresDriver
} with tables.Tables


import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.language.postfixOps
import Tables._
import Tables.profile.api._
import scala.concurrent.ExecutionContext.Implicits.global

object Main {
  val jdbcDriver  = "org.postgresql.Driver"
  val url         = "jdbc:postgresql://localhost:5432/scalasyd"
  val db = Database.forURL(url, driver = jdbcDriver)
  val q  = Artists

  val alice2Name = "Alice2"

  val insertSingle = (Artists returning Artists.map(_.id)) += ArtistsRow(0, alice2Name)

  val deleteAllAlice2s = Artists.filter(_.name === alice2Name).delete

  def runInsert: Future[Int] = {
    db.run(insertSingle)
  }

  def queryAll: Future[Seq[ArtistsRow]]  = {
    db.run(q.result)
  }

  def runDelete: Future[Int] = {
    db.run(deleteAllAlice2s)
  }

  def main(args: Array[String]): Unit = {
    val f = for {
      d <- runDelete
      _: Unit = println(s"Deleted: $d")
      i <- runInsert
      _: Unit = println(s"Inserted and got id: $i")
      v <- queryAll
    } yield v

    Await.result(f.map(println), 60 seconds)
  }
}
