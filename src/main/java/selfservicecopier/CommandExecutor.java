package selfservicecopier;

import java.io.File;
import java.io.IOException;

public class CommandExecutor {
	
	//[1]command, [2]source, [3]destination
	private final String[] EXECUTE_CSS_PRETTIFIER=new String[]{"C:\\SelfProject\\sass\\sass.bat","C:\\SelfProject\\sass\\precompiled","C:\\SelfProject\\vert.x-2.1\\bin\\webapp\\cache\\css"};
	private final String[] EXECUTE_PLAY_COMPILER=new String[]{"C:\\SelfProject\\playCompiler\\compile.bat","",""};
	
	public void compilePlay(){
		executeCmd(EXECUTE_PLAY_COMPILER,"","");
	}
	
	public void scanSass(String dirName){
		
		final String DIRECTORY_NAMING=dirName+"/";
				
		File srcFolder = new File(EXECUTE_CSS_PRETTIFIER[1]+DIRECTORY_NAMING);
		File[] filesInFolder = srcFolder.listFiles();
		if(filesInFolder != null){
			for(File file: filesInFolder){
				if(file.isDirectory()){
					File tempFolder = new File(EXECUTE_CSS_PRETTIFIER[2]+"/"+file.getName());
					if(tempFolder.exists() == false)
						System.out.println("Created folder:-"+file.getName()+(tempFolder.mkdir()?"successfully":"fail"));
					scanSass(DIRECTORY_NAMING+file.getName());
				}else if(file.isFile()){
					executeSass(DIRECTORY_NAMING+file.getName());
				}
			}
		}
	}

	private void executeSass(String fileName){
		
		fileName = fileName.substring(0, fileName.indexOf('.'));
		
		final String fileName_bfr = fileName + ".sass";
		final String fileName_aft = fileName + ".css";
		executeCmd(EXECUTE_CSS_PRETTIFIER, fileName_bfr, fileName_aft);
	}
	
	private void executeCmd(String[] cmd, String fileName_bfr, String fileName_aft){
		//check file exist.
		StringBuilder cmdLine = new StringBuilder(500);
		cmdLine.append(cmd[0]).append(" ")
			.append(cmd[1]).append(fileName_bfr).append(" ")
			.append(cmd[2]).append(fileName_aft);
		
		System.out.println("Execute cmd:"+cmdLine.toString());
		
		try {
			Runtime.getRuntime().exec(cmdLine.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
