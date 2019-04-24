package parser

import java.io.File

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import data.DataExtractor
import util.{ExcelExport, HebrewUtil}

object Parser {

  def parseAndWriteToXls(folderPathName: String): Unit = {
    val payslips = getListOfPdfFiles(folderPathName).map { filePath =>
      val payslipText = getPayslipText(filePath)
      HebrewUtil.fixText(payslipText)
    }
    val aggregatedData = DataExtractor.parse(payslips)
    val destinationFilePath = folderPathName + "summary.xls"
    ExcelExport.writeToFile(destinationFilePath, aggregatedData)
  }

  private def getListOfPdfFiles(folderPathName: String): Seq[String] = {
    Option(new File(folderPathName).listFiles).getOrElse(Array()).toSeq
      .map(_.getAbsolutePath)
      .filter(_.endsWith(".pdf"))
  }

  private def getPayslipText(filename: String) = {
    val reader = new PdfReader(filename)
    val numberOfPages = reader.getNumberOfPages
    val payslip = new StringBuilder
    for (i <- 0 until numberOfPages) {
      payslip.append(PdfTextExtractor.getTextFromPage(reader, i + 1)).append('\n')
    }
    reader.close()
    payslip.toString
  }
}
