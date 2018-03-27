import org.scalatest.FunSuite

class LogsParserTest extends FunSuite {
  test("parse_logs") {
    LogsParser.parse(Array[String]("5","1521983835000", "-", "1522080087000", "--debug"))
  }
}
