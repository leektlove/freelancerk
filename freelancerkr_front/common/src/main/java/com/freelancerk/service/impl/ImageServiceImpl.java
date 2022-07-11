package com.freelancerk.service.impl;

import com.freelancerk.service.ImageService;
import com.freelancerk.service.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

@Service
public class ImageServiceImpl implements ImageService {

    private StorageService storageService;

    @Autowired
    public ImageServiceImpl(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public String getCompressedImageUrl(String imageUrl) {
        File compressedImageFile = new File("/tmp/compressed_image.jpg");
        String outputPath = "/tmp/medium_image.png";
        String resultUrl = null;
        String extension = FilenameUtils.getExtension(imageUrl);
        try {
            if ("png".equalsIgnoreCase(extension)) {
                BufferedImage bufferedImage = ImageIO.read(new URL(imageUrl));
                BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
                        bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);

                // write to jpeg file
                ImageIO.write(newBufferedImage, "jpg", new File(outputPath));

                BufferedImage image = ImageIO.read(new File(outputPath));

                OutputStream os = new FileOutputStream(compressedImageFile);

                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
                ImageWriter writer = (ImageWriter) writers.next();

                ImageOutputStream ios = ImageIO.createImageOutputStream(os);
                writer.setOutput(ios);

                ImageWriteParam param = writer.getDefaultWriteParam();

                param.setCompressionMode(ImageWriteParam.MODE_DEFAULT);
//                param.setCompressionQuality(0.10f);  // Change the quality value you prefer
                writer.write(null, new IIOImage(image, null, null), param);

                os.close();
                ios.close();
                writer.dispose();

                resultUrl = storageService.storeFile(new FileInputStream(compressedImageFile),
                        String.format("%s_compressed.%s", FilenameUtils.getBaseName(imageUrl), "jpg"));

            } else {
                BufferedImage image = ImageIO.read(new URL(imageUrl));

                OutputStream os = new FileOutputStream(compressedImageFile);

                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(StringUtils.isEmpty(extension)?"jpg":extension);
                ImageWriter writer = (ImageWriter) writers.next();

                ImageOutputStream ios = ImageIO.createImageOutputStream(os);
                writer.setOutput(ios);

                ImageWriteParam param = writer.getDefaultWriteParam();

                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.25f);  // Change the quality value you prefer
                writer.write(null, new IIOImage(image, null, null), param);

                os.close();
                ios.close();
                writer.dispose();

                resultUrl = storageService.storeFile(new FileInputStream(compressedImageFile),
                        String.format("%s_compressed.%s", FilenameUtils.getBaseName(imageUrl), StringUtils.isEmpty(extension)?"jpg":extension));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultUrl;
    }
}
