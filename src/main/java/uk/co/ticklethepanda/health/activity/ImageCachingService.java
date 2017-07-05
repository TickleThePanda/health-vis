package uk.co.ticklethepanda.health.activity;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ImageCachingService implements
        SelfDescriptiveMap.Describing<ActivityChartService.Images> {

    private static final Logger LOG = LogManager.getLogger();

    private final ByteArrayFunction task;
    private final ActivityChartService.Images image;
    private File imageFile;
    private ReentrantReadWriteLock fileLock = new ReentrantReadWriteLock();

    @Override
    public ActivityChartService.Images getDescriptor() {
        return image;
    }

    public interface ByteArrayFunction {
        byte[] get() throws IOException;
    }

    public ImageCachingService(
            ActivityChartService.Images image,
            ByteArrayFunction task
    ) {
        this.image = image;
        this.imageFile = new File("cached/" + image.name());
        this.task = task;
    }

    public void update() {
        this.imageFile.getParentFile().mkdirs();
        fileLock.writeLock().lock();
        try (OutputStream stream = new BufferedOutputStream(new FileOutputStream(imageFile))) {
            stream.write(task.get());
        } catch (IOException exception) {
            LOG.error("unable to cache image", exception);
        } finally {
            fileLock.writeLock().unlock();
        }
    }

    public byte[] get() {
        fileLock.readLock().lock();
        try (InputStream stream = new BufferedInputStream(new FileInputStream(imageFile))) {
            return IOUtils.toByteArray(stream);
        } catch (IOException e) {
            LOG.error("unable to read cached image", e);
            throw new IllegalStateException("cache not loaded", e);
        } finally {
            fileLock.readLock().unlock();
        }

    }

}
