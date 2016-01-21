import net.lingala.zip4j.exception.ZipException;
import java.io.File;
import net.lingala.zip4j.core.ZipFile;

public class Compare
{
	public static void main(String[] args) 
	{
		if(args.length < 2)
		{
			System.out.println("Usage : java Compare <src-build-path> <dst-build-path>");
			System.exit(1);
		}
		
		String srcBuildZip = args[0];
		String dstBuildZip = args[1];
		
		File srcBuildZipFile = new File(srcBuildZip);
		File dstBuildZipFile = new File(dstBuildZip);
		
		String srcBuildUnzipPath = getUnzipDirPath(srcBuildZipFile);
		String dstBuildUnzipPath = getUnzipDirPath(dstBuildZipFile);
		
		unzip(srcBuildZip,srcBuildUnzipPath);
		unzip(dstBuildZip,dstBuildUnzipPath);
		
		compareDirs(srcBuildUnzipPath,dstBuildUnzipPath);
	}

	private static void compareDirs(String srcBuildDirPath, String dstBuildDirPath)
	{
		// Read all file in the directories
		/*
		 * TODO : implement it in such a way that files present on dst but not in src are also identified.
		 */
		File srcBuildDir = new File(srcBuildDirPath);
		
		for(File file : srcBuildDir.listFiles())
		{
			String srcBuildFilePath = file.getAbsolutePath();
			String dstBuildFilePath = srcBuildFilePath.replace(srcBuildDirPath, dstBuildDirPath);
			
			if(file.isFile())
				compareFiles(srcBuildFilePath,dstBuildFilePath);
			else
				compareDirs(srcBuildFilePath,dstBuildFilePath);
		}
	}

	private static void compareFiles(String srcBuildFilePath, String dstBuildFilePath)
	{
		
		File srcFile = new File(srcBuildFilePath);
		File dstFile = new File(dstBuildFilePath);
		
		System.out.println("\nSource build file:"+srcFile.getAbsolutePath());
		if(!dstFile.exists())
			System.out.println("**File is not preseent in the destination**");
		else
			System.out.println("Destination build file:"+dstFile.getAbsolutePath());
		
		// Call function based on file extension
		
		
	}

	private static String getUnzipDirPath(File buildZipFile) 
	{
		String fileName = buildZipFile.getName();
		String fileNameWithoutZip = fileName.substring(0,fileName.lastIndexOf('.'));
		
		//get path to create the directory to store unzipped content
		String buildUnzipPath = buildZipFile.getParent()+File.separator+fileNameWithoutZip;
		
		// And create the directory
		File buildUnzipDir = new File(buildUnzipPath);
		buildUnzipDir.deleteOnExit();
		buildUnzipDir.mkdir();
		
		return buildUnzipPath;
	}

	public static void unzip(String source,String  destination)
	{
		String password = "password";
		System.out.println("Zip file path:"+source);
		System.out.println("Output folder path:"+destination);

		try 
		{
			ZipFile zipFile = new ZipFile(source);
			
			if (zipFile.isEncrypted()) {
				zipFile.setPassword(password);
			}
			zipFile.extractAll(destination);
		}
		catch (ZipException e) 
		{
			e.printStackTrace();
		}
	}

}
