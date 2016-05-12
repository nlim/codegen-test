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

  val insertAction = DBIO.seq(
    Artists += ArtistsRow(0, "Alice2")
  )

  def runInsert: Future[Unit] = {
    db.run(insertAction)
  }

  def queryAll: Future[Seq[ArtistsRow]]  = {
    db.run(q.result)
  }

  def main(args: Array[String]): Unit = {
    val f = for {
      _ <- runInsert
      v <- queryAll
    } yield v

    Await.result(f.map(println), 60 seconds)
  }
}
