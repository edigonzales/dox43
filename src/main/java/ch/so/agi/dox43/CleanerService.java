package ch.so.agi.dox43;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

@Service
public class CleanerService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.workDirectory}")
    private String workDirectory;
    
    @Value("${app.folderPrefix}")
    private String folderPrefix;

    @Async("asyncTaskExecutor")
    @Scheduled(cron="0 */5 * * * *")
    //@Scheduled(fixedRate = 1 * 30 * 1000) /* Runs every 30 seconds */
    public void cleanUp() {    
        long deleteFileAge = 60;
        logger.info("Deleting files older than {} [s]...", deleteFileAge);

        File[] tmpDirs = new java.io.File(workDirectory).listFiles();
        if(tmpDirs!=null) {
            for (File tmpDir : tmpDirs) {
                if (tmpDir.getName().startsWith(folderPrefix)) {
                    try {
                        FileTime creationTime = (FileTime) Files.getAttribute(Paths.get(tmpDir.getAbsolutePath()), "creationTime");                    
                        Instant now = Instant.now();
                        
                        long fileAge = now.getEpochSecond() - creationTime.toInstant().getEpochSecond();
                        logger.trace("found folder with prefix: {}, age [s]: {}", tmpDir, fileAge);

                        if (fileAge > deleteFileAge) {
                            logger.debug("deleting {}", tmpDir.getAbsolutePath());
                            FileSystemUtils.deleteRecursively(tmpDir);
                        }
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }            
    }

}
