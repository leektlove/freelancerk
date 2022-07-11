package com.freelancerk.service.impl;

import com.freelancerk.service.StorageService;
import com.freelancerk.service.VideoThumbnailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;
import org.jcodec.scale.AWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class VideoThumbnailServiceImpl implements VideoThumbnailService {

    private StorageService storageService;

    @Autowired
    public VideoThumbnailServiceImpl(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public List<String> getThumbnailUrls(String videoFileUrl) {
        List<String> thumbnails = new ArrayList<>();
        try {
            File file = new File(String.format("/tmp/video-%d.mp4", System.currentTimeMillis()));
            FileUtils.copyURLToFile(
                    new URL(videoFileUrl),
                    file,
                    2000,
                    2000);

            int frameIndex = 0;
            int frameCount = 10;

            FileChannelWrapper ch = NIOUtils.readableChannel(file);
            MP4Demuxer demuxer = MP4Demuxer.createMP4Demuxer(ch);
            DemuxerTrack videoTrack = demuxer.getVideoTrack();
            int totalFrames = videoTrack.getMeta().getTotalFrames();
            int frameDiff = totalFrames / 11;
            FrameGrab grab = FrameGrab.createFrameGrab(ch);
            grab.seekToFramePrecise(frameIndex);

            for (int i = 0; i < frameCount; i++) {
                Picture picture = grab.getNativeFrame();
                log.info(picture.getWidth() + "x" + picture.getHeight() + " " + picture.getColor());
                BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
                File frameFile = new File(String.format("/tmp/%d-frame-%d.png", System.currentTimeMillis(), i));
                ImageIO.write(bufferedImage, "png", frameFile);
                thumbnails.add(storageService.storeFile(FileUtils.openInputStream(frameFile), frameFile.getName()));
                frameIndex+=frameDiff;
                grab.seekToFramePrecise(frameIndex);
                frameFile.delete();
            }

            file.delete();
        } catch (Exception e) {
            log.error("<<< error at making thumbnails", e);
        }

        return thumbnails;
    }

    @Override
    public String getOneThumbnailUrl(String videoFileUrl) {
        String thumbnailUrl = null;
        try {
            File file = new File(String.format("/tmp/video-%d.mp4", System.currentTimeMillis()));
            FileUtils.copyURLToFile(
                    new URL(videoFileUrl),
                    file,
                    2000,
                    2000);

            FileChannelWrapper ch = NIOUtils.readableChannel(file);
            FrameGrab grab = FrameGrab.createFrameGrab(ch);
            grab.seekToFramePrecise(0);

            Picture picture = grab.getNativeFrame();
            log.info(picture.getWidth() + "x" + picture.getHeight() + " " + picture.getColor());
            BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
            File frameFile = new File(String.format("/tmp/%d-frame-%d.png", System.currentTimeMillis(), 0));
            ImageIO.write(bufferedImage, "png", frameFile);
            thumbnailUrl = storageService.storeFile(FileUtils.openInputStream(frameFile), frameFile.getName());
            frameFile.delete();

            file.delete();
        } catch (Exception e) {
            log.error("<<< error at making thumbnails", e);
        }
        return thumbnailUrl;
    }
}
