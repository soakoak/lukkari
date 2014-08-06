package fi.sokira.metropolialukkari.test

import android.test.ProviderTestCase2
import android.os.AsyncTask
import android.database.Cursor
import java.util.{Date, ArrayList, List => JList, Calendar}
import junit.framework.Assert
import scala.collection.JavaConversions._

import fi.sokira._
import metropolialukkari.models.{MpoliaRealization, MpoliaStudentGroup}
import metropolialukkari.MpoliaRealizationAddingTask
import metropolialukkari.LukkariUtils.RichCursor
import lukkari.provider
import provider.{LukkariProvider, LukkariContract}
import LukkariContract._
import TaskTest._


class TaskTest extends ProviderTestCase2[LukkariProvider](
                              classOf[LukkariProvider], 
                              LukkariContract.AUTHORITY) {

   private val Tag = this.getClass.getSimpleName
   
   private def resolver = getMockContentResolver
   
   def testRealizationAddingTask {
      val testSgCode1 = "Group_1"
      val testSgCode2 = "Group_2"
      val testSgCode3 = "Group_3"
         
      def makeTestGroup(code: String) = {
         val group = new MpoliaStudentGroup
         group.setCode(code)
         group
      }

      val testGroup1 = makeTestGroup(testSgCode1)
      val testGroup2 = makeTestGroup(testSgCode2)
      val testGroup3 = makeTestGroup(testSgCode3)
      
      def makeTestCase(code: String, name: String, 
            startDate: Date, endDate: Date, 
            studentGroups: Seq[MpoliaStudentGroup]) = {
         
         val testCase = new MpoliaRealization
         testCase.setCode(code)
         testCase.setName(name)
         testCase.setStartDate(startDate)
         testCase.setEndDate(endDate)
         testCase.setStudentGroups(new ArrayList(studentGroups))
         testCase
      }
      
      def makeDate(year: Int, month: Int, day: Int) = {
         val cal = Calendar.getInstance
         cal.setTimeInMillis(0)
         cal.set(year, month, day)
         cal.getTime
      }
      
      val testCode1 = "T3ST1-1"
      val testName1 = "Testaus_1"
      val testStartDate1 = makeDate(2014, 1, 1)
      val testEndDate1 = makeDate(2014, 5, 31)
      val testGroupList1 = testGroup1 :: testGroup2 :: Nil
      
      val testCase1 = makeTestCase(testCode1, testName1, 
            testStartDate1, testEndDate1, testGroupList1)
            
      val testCode2 = "T3ST1-2"
      val testName2 = "Testaus_2"
      val testStartDate2 = makeDate(2013, 1, 1)
      val testEndDate2 = makeDate(2013, 5, 31)
      val testGroupList2 = testGroup2 :: testGroup3 :: Nil
      
      val testCase2 = makeTestCase(testCode2, testName2, 
            testStartDate2, testEndDate2, testGroupList2)
            
      val testTask = new MpoliaRealizationAddingTask(getMockContext, false)
      testTask.execute(testCase1, testCase2)
      
      while( testTask.getStatus != AsyncTask.Status.FINISHED) {
         Thread.sleep(500)
      }

      def assertRealization(realization: MpoliaRealization) = { 
         def queryRealization(projection: Array[String], 
               selection: String, selectionArgs: Array[String]) = {
            resolver.query(Realization.CONTENT_URI, 
                  projection, selection, selectionArgs, null)
         }

         import Realization.Columns._
         val projection = Array(ID, CODE, NAME, START_DATE, END_DATE)
         val selection = CODE + " = ?"
         val selectionArgs = Array(realization.getCode)
         val cursor = queryRealization(projection, selection, selectionArgs)
         
         cursor.assertIfEmpty()
         
         cursor.moveToFirst()
         
         cursor.assertColumn(realization.getCode, CODE)
         cursor.assertColumn(realization.getName, NAME)
         cursor.assertColumn(realization.getStartDate.getTime, START_DATE)
         cursor.assertColumn(realization.getEndDate.getTime, END_DATE)
         
         cursor.getLong(Realization.Columns.ID)
      }
      
      def assertStudentGroups(realizationId: Long, 
            studentGroups: JList[MpoliaStudentGroup]) = {
         
         def queryStudentGroup(projection: Array[String], 
               selection: String, selectionArgs: Array[String]) = {
            
            resolver.query(StudentGroup.CONTENT_URI, 
                  projection, selection, selectionArgs, null)
         }
         
         import StudentGroup.Columns._
         val projection = Array(CODE)
         val selection = REALIZATION_ID + " = ?"
         val selectionArgs = Array(realizationId.toString)
         val cursor = queryStudentGroup(projection, selection, selectionArgs)

         cursor.assertIfEmpty()
         
         cursor.moveToFirst()

         def assertLists[T : Ordering](expected: Seq[T], compared: Seq[T]) {
            val listOfPairs = (expected.sorted, compared.sorted).zipped
            for((i, j) <- listOfPairs) {
               Assert.assertEquals(i, j)
            }
         }
         
         val expectedCodes = for(group <- studentGroups) yield group.getCode
         val comparedCodes = cursor.extractColumnAsStringList(CODE)
         
         assertLists(expectedCodes, comparedCodes)
      }
      
      var realizationIdx =  assertRealization(testCase1)
      assertStudentGroups(realizationIdx, testCase1.getStudentGroups)
      realizationIdx = assertRealization(testCase2)
      assertStudentGroups(realizationIdx, testCase2.getStudentGroups)
   }
}

object TaskTest {
   
   implicit class CursorAsserter(cursor: Cursor) {
      
      def assertIfEmpty() {
         if(cursor.getCount() < 1) {
            Assert.fail("Empty cursor")
         }
      }
      
      def assertColumn(expected: Any, columnName: String) {
         val idx = cursor.getColumnIndex(columnName)
         val compared = expected match {
            case _: String => cursor.getString(idx)
            case _: Long => cursor.getLong(idx)
         }
         Assert.assertEquals(expected, compared)
      }
   }

}