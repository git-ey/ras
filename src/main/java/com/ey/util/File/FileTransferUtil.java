package com.ey.util.File;
import org.springframework.util.Assert;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class FileTransferUtil {


    // NIO文件转存到本地
    public static List<File> transferFiles(List<File> remoteFileUrls,String importId) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3); // 使用固定大小的线程池
        List <File> transferFiles = new ArrayList<>();
        File newFolder = new File(System.getProperty("user.dir"), "transferFiles" + File.separator + importId);
        if (!newFolder.exists()) {
            boolean success = newFolder.mkdirs();
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
                    transferFiles.add(localFolder);
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
        return transferFiles;
    }

    public static List<File> localFiles(List<File> remoteFileUrls,String filePath,String importId) {
        //正则校验为本地盘或是公盘
        boolean isNotLetterColon = filePath.matches("[A-Za-z]:.*");
        if(!isNotLetterColon) {
            try {
                return transferFiles(remoteFileUrls,importId);
            } catch (Exception e){
                return  remoteFileUrls;
            }
        }
        return  remoteFileUrls;
    }

    private static final int MAX_RETRIES = 5; // 最大重试次数
    private static final long RETRY_DELAY = 500; // 重试间隔（毫秒）

    public static void deleteFolder(String importId) {
        Path rootPath = Paths.get(System.getProperty("user.dir"), "transferFiles", importId);
        if (Files.exists(rootPath) && Files.isDirectory(rootPath)) {
            try {
                // 清空文件夹内的所有文件
                Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                });
                // 尝试删除最外层的文件夹
                boolean deleted = false;
                int attempt = 0;
                while (!deleted && attempt < MAX_RETRIES) {
                    try {
                        Files.delete(rootPath);
                        deleted = true; // 删除成功
                        deleteFolder();
                    } catch (IOException e) {
                        attempt++; // 增加重试次数

                        if (attempt < MAX_RETRIES) {
                            try {
                                // 等待一段时间后重试
                                TimeUnit.MILLISECONDS.sleep(RETRY_DELAY);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt(); // 恢复中断状态
                                break; // 退出循环，不再重试
                            }
                            System.err.println("Retrying to delete folder " + rootPath + " (attempt " + attempt + ")");
                        } else {
                            // 达到最大重试次数，记录失败
                            System.err.println("Failed to delete folder " + rootPath + " after " + MAX_RETRIES + " attempts");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void deleteFolder( ){
        // 获取目标目录路径
        Path rootPath = Paths.get(System.getProperty("user.dir"), "transferFiles");
        // 遍历目录下的所有文件夹
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootPath)) {
            for (Path path : stream) {
                // 检查是否为目录
                if (Files.isDirectory(path)) {
                    // 获取文件夹的最后修改时间
                    BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                    LocalDateTime lastModifiedTime = attrs.lastModifiedTime()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

                    // 计算与当前时间的差值
                    long daysBetween = ChronoUnit.DAYS.between(lastModifiedTime, LocalDateTime.now());

                    // 如果超过一天，则删除该文件夹
                    if (daysBetween > 1) {
                        deleteDirectory(path); // 删除文件夹及其内容
                        System.out.println("Deleted folder: " + path);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 删除文件夹及其内容的辅助方法
    private static void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                    .sorted((a, b) -> b.compareTo(a)) // 从最深层的文件开始删除
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }



//    public static void main(String[] args) throws IOException {
//
////        String path =  "C:\\Users\\smart\\Desktop\\导入";
//        String path = "C:\\Users\\smart\\Desktop\\导入";
//
//        List<File> files = FileUtil.getPathFile(path,".*.xlsx$");
//        for (File file : files) {
//            System.out.println(file.getName());
//        }
//         files = localFiles(files,path,"123");
//        deleteFolder("123");//清空转存文件夹
//    }



}
