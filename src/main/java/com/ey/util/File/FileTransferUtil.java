package com.ey.util.File;
import com.ey.util.FileUtil;
import org.springframework.util.Assert;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class FileTransferUtil {


    // NIO文件转存到本地
    public static List<File> transferFiles(List<File> remoteFileUrls) throws InterruptedException, ExecutionException {
        List <File> list = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(3); // 使用固定大小的线程池
        String currentWorkingDir = System.getProperty("user.dir"); // 获取当前工作目录
        File newFolder = new File(currentWorkingDir, "newFolder");
        if (!newFolder.exists()) {
            boolean success = newFolder.mkdir();
            Assert.isTrue(success, "Failed to create folder at: " + newFolder.getAbsolutePath());
        }
        List<Future<?>> futures = new ArrayList<>();
        for (File remoteFileUrl : remoteFileUrls) {
            futures.add(executor.submit(() -> {
                try {
                    File localFolder = new File(newFolder,remoteFileUrl.getName()); // 本地目标文件夹路径
                    FileChannel  inputChannel = FileChannel.open(remoteFileUrl.toPath(),StandardOpenOption.READ);
                    FileChannel  outputChannel = FileChannel.open(localFolder.toPath(),StandardOpenOption.WRITE,StandardOpenOption.CREATE);
                    long size = inputChannel.size();
                    long position = 0;
                    long trasferred = inputChannel.transferTo(position,size,outputChannel);
                    outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                    list.add(localFolder);
                    System.out.println("传输字节数:"+trasferred);
                    inputChannel.close();
                    outputChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
        }
        // 等待所有任务完成
        for (Future<?> future : futures) {
            future.get();
        }
        executor.shutdown();
        return remoteFileUrls;
    }

    public static List<File> localFiles(List<File> remoteFileUrls,String filePath) {
        boolean isNotLetterColon =! filePath.matches("[A-Za-z]:.*");
        if(isNotLetterColon) {
            try {
                return transferFiles(remoteFileUrls);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }finally {
                return  remoteFileUrls;
            }
        }
        return  remoteFileUrls;
    }



    public static void deleteFolder() throws IOException {
        String currentWorkingDir = System.getProperty("user.dir"); // 获取当前工作目录
        File newFolder = new File(currentWorkingDir, "newFolder");
        if (newFolder.exists()) {
            Files.walk(newFolder.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }




    public static void main(String[] args)  {

//        String path =  "C:\\Users\\smart\\Desktop\\导入";
        String path = " \\\\CNSHAAPPFL006 006.ey.net\\0128812\\12912\\123";

        List<File> files = FileUtil.getPathFile(path,".*.xlsx$");
        for (File file : files) {
            System.out.println(file.getName());
        }
        localFiles(files,path);

    }



}
