import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.Done
import akka.actor.Status.Success
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.util.parsing.json._
import scala.io.StdIn
import scala.concurrent.Future

object WebServer {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  case class Note(value:Double)
  case class Student(name:String, id: String, list: List[Note])
  case class Course(students: List[Student], name:String, id:String)

  implicit val noteFormat = jsonFormat1(Note)

  object myJsonStudentProtocol extends DefaultJsonProtocol {
    implicit def studentFormat[Note: JsonFormat] = jsonFormat3(Student.apply())
  }

  object myJsonCourseProtocol extends DefaultJsonProtocol {
    implicit def courseFormat[Student: JsonFormat] = jsonFormat3(Course.apply())
  }

  var note1: Note = Note(3)
  var note2: Note = Note(4.2)
  var note3: Note = Note(3.3)
  var note4: Note = Note(4)
  var note5: Note = Note(2.5)

  var student1 = Student("Juan", "1", List(note1, note2, note3))
  var student2 = Student("Andres", "2", List(note4, note3, note5))
  var student3 = Student("Andres", "2", List(note1, note4, note5))
  var student4 = Student("Julian", "5", List(note5, note3, note5))
  var student5 = Student("Juan", "1", List(note5, note3, note3))
  var student6 = Student("Jose", "3", List(note3, note4, note2))
  var student7 = Student("Miguel", "3", List(note2, note2, note3))
  var student8 = Student("Miguel", "3", List(note1, note4, note1))

  var course1 = Course(List(student3, student1, student5, student4, student8), "Curso 1", "111")
  var course2 = Course(List(student5, student1, student6, student2), "Curso 2", "222")
  var course3 = Course(List(student3, student1, student7), "Curso 3", "333")
  var courses: List[Course] = List(course1, course2, course3)

  object Operation {
    var students : List[Student] = Nil

    def fetchStudents(courseId: String) : Future[Option[List[Student]]] = Future {
      courses.find( c => c.id == courseId).flatMap(course => Option(course.students))
    }
  }

  def main(args: Array[String]): Unit = {

    val route : Route = {
      get{
        pathPrefix("students" / IntNumber){
          courseId =>
            val maybeStudents: Future[Option[List[Student]]] = Operation.fetchStudents(courseId.toString)

            onSuccess(maybeStudents) {
              case Some(some) => complete((some.toString))
              case None => complete(StatusCodes.NotFound)
            }
        }
      }
    }

    val bindFuture = Http().bindAndHandle(route, "localhost",8080)
    println(s"Server online at http://localhost:8080 \nPress Return to stop")
    StdIn.readLine()
    bindFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}