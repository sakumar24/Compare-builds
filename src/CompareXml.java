import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;

public class CompareXml
{
	/*
	 * Remove this main function and call the compare function directly from Compare class function 
	 * CompareFiles and declare this diffReport in that calss
	 */
	
	public static List<ReportStatistics> diffReport = new ArrayList<ReportStatistics>();
	public static void main(String args[])
	{
		String srcFilePath = "D:/CET-Project/zips/src-build/conf/sample.xml";
		String dstFilePath = "D:/CET-Project/zips/dst-build/conf/sample.xml";

		compare(srcFilePath,dstFilePath);	
		printReport();
	}
	private static void compare(String srcFile, String dstFile)
	{
		try
		{
			if(FilenameUtils.isExtension(srcFile, "xml") && FilenameUtils.isExtension(dstFile, "xml"))
			{
				System.out.println("Comparing Xml files");
				
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				
				dbFactory.setNamespaceAware(true);	
				dbFactory.setCoalescing(true);
				dbFactory.setIgnoringComments(true);
				
				dbFactory.setIgnoringElementContentWhitespace(true);
				/*
				 * TODO: find a way to handle nodes for whitespaces
				 */ 
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document srcXmlDoc = dBuilder.parse(new File(srcFile));
				Document dstCmlDoc = dBuilder.parse(new File(dstFile));
								
								//dBuilder.parse(new ByteArrayInputStream(srcFile.getBytes()));
								// Not working, getting run time exception:
								// 				Content is not allowed in prolog.
				
				srcXmlDoc.normalizeDocument();
				dstCmlDoc.normalizeDocument();
			
				DiffXml diffXml = new DiffXml();
				diffXml.fileName = srcFile;
				diffXml.diff(srcXmlDoc, dstCmlDoc, diffReport);
			}	
		}
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	private static void printReport()
	{
		for(ReportStatistics report : diffReport)
		{
			System.out.println("---------------------------------------------");
			System.out.println("File:"+report.getFilePath());
			System.out.println("Property:"+report.getProperty());
			System.out.println("Previous Value:"+report.getPreviousValue());
			System.out.println("New Value:"+report.getNewValue());
		}
	}
}
