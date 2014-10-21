package selfservicecopier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jaring.jom.util.common.PropertyLoaderUtil;

/**
 * This program does not consider it's performance value.
 * The concern for this program is the output (and the performance of it's output).
 * @author yoong.han
 */

public class FolderCopier {

	private final String SPLIT_TOKEN=" > ";
	private final String PROPERTY_MODIFIER="modifier.properties";
	private String SRC_FOLDER = "";
	private String DEST_FOLDER = "";
	private List<String> EXTENSION_TO_ALTER = Arrays.asList(".html",".css","controller.js"); //don't for js. Dangerous use minimizer.
	private List<String[]> listModifyValue = new ArrayList<String[]>(10);
	private List<String> foldersToSkip = new ArrayList<String>(10);
	
	public FolderCopier(String sourceFolder, String destFolder){
		
		sourceFolder = trimLastChar(replaceSpecialChar(sourceFolder));
		destFolder = trimLastChar(replaceSpecialChar(destFolder));
		SRC_FOLDER = sourceFolder;
		DEST_FOLDER = destFolder;
		
		loadValueToModify();
	}
	
	private String replaceSpecialChar(String value){
		return value.replaceAll("\\\\", "/");
	}
	
	private String trimLastChar(String value){
		return value.charAt(value.length()-1) == '/'?value.substring(0, value.length()-1):value;
	}
	
	/**
	 * Execute file copying.
	 */
	public void execute(){
		if(SRC_FOLDER.equals(DEST_FOLDER)){
			System.err.println("Source and destination must not be the same.");//to avoid recursive loop
			System.exit(-1);
		}
		
		createFiles(SRC_FOLDER, DEST_FOLDER);
		cleanUp(DEST_FOLDER, SRC_FOLDER);
	}
	
	/**
	 * Execute file clean up, which is to remove unwanted files that have been deleted.
	 */
	public void cleanUp(String src, String dest){
		
		File srcFolder = new File(src);
		
		File[] filesInFolder = srcFolder.listFiles();
		for(File file: filesInFolder){
			final String fileName = file.getName();
			
			if(file.isDirectory() && shouldSkip(fileName) == false){
				
				File destFolder = new File(addFolderSlash(dest)+fileName);
				if(destFolder.exists() == false){

					boolean status = file.delete();
					if(status == false  && file.isDirectory()){
						System.out.println("Removing subentries");
						removeSubFiles(file.listFiles());
						file.delete();
					}else{
						System.out.println("Removed folder:"+fileName);	
					}
				}else{
					cleanUp(file.toPath().toString(),destFolder.toPath().toString());
				}
			}else if(file.isFile()){

				File srcFile = new File(addFolderSlash(src)+fileName);
				File targetFile = new File(addFolderSlash(dest)+fileName);

				if(!targetFile.exists()) {
					//remove from destFile
					System.out.println("Removing file:"+srcFile.getName());
					srcFile.delete();
			    }
			}
		}
	}
	
	public void addFoldersToSkip(String folder){
		foldersToSkip.add(folder);
	}
	
	private boolean shouldSkip(String fileName) {
		
		boolean found = false;
		
		if( fileName.startsWith(".") 
				|| foldersToSkip.contains(fileName))
			found = true;
					
		return found;
	}

	private void removeSubFiles(File[] listFiles) {
		for(File subFile: listFiles){
			String subFileName = subFile.getName();
			boolean status = subFile.delete();
			if(status == false && subFile.isDirectory()){
				removeSubFiles(subFile.listFiles());
				subFile.delete();
			}else{
				System.out.println("Removing file:"+subFileName);
			}
		}
	}

	private void loadValueToModify() {
		try {
			InputStream inputStream = PropertyLoaderUtil.fileLoader(PROPERTY_MODIFIER);
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));
			
			String value="";
			while((value = bufferReader.readLine()) != null){
				String splitValue[] = value.split(SPLIT_TOKEN);
				listModifyValue.add(splitValue);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void createFiles(String src, String dest){
		File srcFolder = new File(src);

		
		File[] filesInFolder = srcFolder.listFiles();
		for(File file: filesInFolder){
			if(file.isDirectory()){
				File destFolder = new File(addFolderSlash(dest)+file.getName());
				if(destFolder.exists() == false){
					System.out.println("Directory "+file.getName()+" created "+(destFolder.mkdir()?"successfully":"failed"));
				}
				
				createFiles(file.toPath().toString(),destFolder.toPath().toString()); //may get infinite recursive?!
				
			}else if(file.isFile()){
				/**
				 * do file execution
				 * 1) Suppress file size.
				 * 2) Copy file into new file.
				 * 3) Replace keywords.
				 */
				String filename = file.getName();
				File srcFile = new File(addFolderSlash(src)+filename);
				File targetFile = new File(addFolderSlash(dest)+filename);

				if(isScanAndAlter(srcFile)){
					alterCopy(srcFile,targetFile);
				}else{
					directCopy(srcFile,targetFile);
				}
			}
		}
	}
	
	private void alterCopy(File srcFile, File destFile) {
		try{
		if(!destFile.exists()) {
			destFile.createNewFile();
	    }

	    FileReader source = null;
	    FileWriter destination = null;
	    BufferedReader inputFileReader = null;
	    BufferedWriter outputFileWriter = null;
	    
	    try {
	        source = new FileReader(srcFile);
	        destination = new FileWriter(destFile);
	        
	        inputFileReader = new BufferedReader(source);
	        outputFileWriter = new BufferedWriter(destination);
	        
	        String output = "";
	        while((output = inputFileReader.readLine()) != null){
	        	output = doStringReplacement(output);
	        	
	        	outputFileWriter.write(output);
	        	outputFileWriter.flush();
	        }
	        
	        
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	        if(inputFileReader != null){
	        	inputFileReader.close();
	        }
	        if(outputFileWriter != null){
	        	outputFileWriter.close();
	        }
	    }
		}catch(Exception e){
			System.err.println("FILE COPY FAIL");
			e.printStackTrace();	//local machine and this will fail? come on.
		}
	}

	public String doStringReplacement(String output) {
		
		//Remove if there is a comment.
		output = output.replaceAll("<!--.*-->", "");
		if(output.contains("<!--")){
			System.out.println("Not able to remove multi-line output");
		}
		output = urlReplacement(output);
		output = output.replaceAll("\\r", "");	//replace all windows \r
		
		output = output.replaceAll("\\s\\s+"," "); //replace double spaces as single space. this may affect <pre> tags.
		return "".equals(output)?"":output+"\n";
	}

	private String urlReplacement(String output) {
		for(String[] modifyValue:listModifyValue){
			output = output.replaceAll(modifyValue[0], modifyValue[1]);
		}
		return output;
	}

	private boolean isScanAndAlter(File srcFile) {
		
		boolean filter = false;
		
		for(String extension: EXTENSION_TO_ALTER){
			if(srcFile.getName().lastIndexOf(extension) > -1){
				filter=true;
				break;
			}
		}
		
		return filter;
	}

	public void directCopy(File srcFile, File targetFile) {
		try {
			Files.copy(srcFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.err.println("FILE COPY FAIL");
			e.printStackTrace();	//local machine and this will fail? come on.
		}
	}

	private String addFolderSlash(String folder){
		return folder+"/";
	}
	
}
