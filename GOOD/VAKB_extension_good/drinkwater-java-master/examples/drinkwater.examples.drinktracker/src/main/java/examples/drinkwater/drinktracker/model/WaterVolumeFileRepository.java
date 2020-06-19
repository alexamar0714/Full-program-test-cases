package examples.drinkwater.drinktracker.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by A406775 on 27/12/2016.
 */
public class WaterVolumeFileRepository implements IWaterVolumeRepository {

    public String directory;
    private static Logger logger = LoggerFactory.getLogger(WaterVolumeFileRepository.class);

    public WaterVolumeFileRepository() {
    }

    public WaterVolumeFileRepository(String directory) {
        this.directory = directory;
    }

    private static String createPath(String accountId) {
        return "water-volumes-of-" + accountId + ".txt";
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    @Override
    public synchronized void saveWaterVolume(Account account, String volume) {
        try {
            volume = volume + "\n";
            Thread.sleep(Constants.LATENCY);
            Files.write(Paths.get(directory, createPath(account.getAcountId())), volume.getBytes(),
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized List<String> getVolumes(Account account) {

        List<String> lines = new ArrayList<>(0);

        try {
            lines =
                    Files.readAllLines(Paths.get(directory, createPath(account.getAcountId())), Charset.defaultCharset());

        } catch (IOException e) {
            logger.warn("error while getting volume from repository : " + e.getMessage());
        }
        return lines;

    }

    public synchronized void clearVolumes(Account account) {
        try {

            Files.delete(Paths.get(directory, createPath(account.getAcountId())));

        } catch (IOException e) {
            //perhaps file do not exists
        }
    }
}
