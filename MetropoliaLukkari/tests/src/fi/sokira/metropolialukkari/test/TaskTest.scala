package fi.sokira.metropolialukkari.test

import android.test.ProviderTestCase2
import fi.sokira.metropolialukkari.models.MpoliaRealization
import fi.sokira.metropolialukkari.models.MpoliaStudentGroup
import fi.sokira.lukkari.provider.LukkariProvider
import fi.sokira.lukkari.provider.LukkariContract
import fi.sokira.lukkari.provider.LukkariContract._
import fi.sokira.lukkari.provider.test.TestUtils.makeDate
import java.util.Date
import java.util.ArrayList
import fi.sokira.metropolialukkari.MpoliaRealizationAddingTask
import android.os.AsyncTask

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
         import collection.JavaConversions.seqAsJavaList
         testCase.setStudentGroups(new ArrayList(studentGroups))
         testCase
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
         
         def queryRealization(selectionArgs: Array[String]) = {
            import Realization.Columns._
            val projection = Array(CODE, NAME, START_DATE, END_DATE)
            val selection = CODE + " = ?"
            resolver.query(Realization.CONTENT_URI, 
                  projection, selection, selectionArgs, null)
      }
         //TODO jatka tästä
         val cursor = queryRealization(Array(realization.getCode))
      }
   }
}