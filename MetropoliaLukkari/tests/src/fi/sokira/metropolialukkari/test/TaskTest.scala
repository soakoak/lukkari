package fi.sokira.metropolialukkari.test

import android.test.ProviderTestCase2
import junit.framework.Assert.assertEquals
import fi.sokira.metropolialukkari.models.MpoliaRealization
import fi.sokira.metropolialukkari.models.MpoliaStudentGroup
import fi.sokira.lukkari.provider.LukkariProvider
import fi.sokira.lukkari.provider.LukkariContract
import fi.sokira.lukkari.provider.LukkariContract._
import java.util.Date
import java.util.ArrayList
import fi.sokira.metropolialukkari.MpoliaRealizationAddingTask
import android.os.AsyncTask
import android.database.Cursor
import java.util.Calendar

class TaskTest extends ProviderTestCase2[LukkariProvider](
                              classOf[LukkariProvider], 
                              LukkariContract.AUTHORITY) {

   private val Tag = this.getClass.getSimpleName
   
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
//         import collection.JavaConversions.seqAsJavaList
//         testCase.setStudentGroups(new ArrayList(studentGroups))
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
            
      val testTask = new MpoliaRealizationAddingTask(getMockContext)
      testTask.execute(testCase1, testCase2)
      
      while( testTask.getStatus != AsyncTask.Status.FINISHED) {
         Thread.sleep(500)
      }
      

      def assertRealization(realization: MpoliaRealization) = { 
         def resolver = getMockContentResolver
         
         def queryRealization(projection: Array[String], 
               selection: String, selectionArgs: Array[String]) = {
            resolver.query(Realization.CONTENT_URI, 
                  projection, selection, selectionArgs, null)
         }

         import Realization.Columns._
         val projection = Array(CODE, NAME, START_DATE, END_DATE)
         val selection = CODE + " = ?"
         val selectionArgs = Array(realization.getCode)
         val cursor = queryRealization(projection, selection, selectionArgs)
         
         def assertColumn(expected: Any, columnName: String) {
            val idx = cursor.getColumnIndex(columnName)
            val compared = expected match {
               case _: String => cursor.getString(idx)
               case _: Long => cursor.getLong(idx)
            }
            assertEquals(expected, compared)
         }
         
         assertColumn(realization.getCode, CODE)
         assertColumn(realization.getName, NAME)
         assertColumn(realization.getStartDate.getTime, START_DATE)
         assertColumn(realization.getEndDate.getTime, END_DATE)
         
         //TODO student_group testaus
      }
      
      assertRealization(testCase1)
      assertRealization(testCase2)
   }
}