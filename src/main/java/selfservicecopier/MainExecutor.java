package selfservicecopier;

public class MainExecutor {

	private static final String srcFolder = "C:\\SelfProject\\vert.x-2.1\\bin\\webapp";
	private static final String destFolder = "C:\\SelfProject\\IDE\\workspace\\_migrateGitHub\\starskydeploy\\webroot";
	
	public static void main(String[] args) {
		System.out.println("========File started.========");
		FolderCopier fc = new FolderCopier(srcFolder, destFolder);
		fc.execute();
		System.out.println("========File completed.========");
	}

}
