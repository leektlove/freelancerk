package com.freelancerk;

import com.freelancerk.domain.ContestEntryFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.tika.Tika;

import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;

@Slf4j
public class FileUtil {

    public static ContestEntryFile.Type getFileType(String fileUrl) {
        try {
            String mediaType =  new Tika().detect(new URL(fileUrl));
            log.info("<<< {}`s detected mediaType: {}", fileUrl, mediaType);
            if (Arrays.asList("image/gif", "image/jpeg", "image/png").contains(mediaType)) {
                return ContestEntryFile.Type.IMAGE;
            } else if (Arrays.asList("video/mp4", "video/webm", "video/ogg").contains(mediaType)) {
                return ContestEntryFile.Type.VIDEO;
            }
        } catch (Exception e) {
            return null;
        }

        return ContestEntryFile.Type.ETC;
    }

    public static Header[] getHeadersFromUrl(String url) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            //get all headers
            return response.getAllHeaders();
        } catch (Exception e) {
            log.error("<<< error at getting header", e);
        }
        return null;
    }

    public static String getFileNameFromContentDisposition(String value) {
        String fileName = URLDecoder.decode(value.replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1"));
        if (fileName.endsWith(";")) {
            return fileName.substring(0, fileName.length() - 1);
        }
        return fileName;
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static ContestEntryFile setMetaInfo(ContestEntryFile contestEntryFile, String fileUrl) {
        if (StringUtils.isNotEmpty(fileUrl)) {
            Header[] headers = FileUtil.getHeadersFromUrl(fileUrl);
            if (headers != null) {
                for (Header header: headers) {
                    if ("Content-Length".equalsIgnoreCase(header.getName())) {
                        contestEntryFile.setFileSize(Long.parseLong(header.getValue()));
                    }
                    if ("Content-Disposition".equalsIgnoreCase(header.getName())) {
                        contestEntryFile.setFileOriginName(FileUtil.getFileNameFromContentDisposition(header.getValue()));
                    }
                }
            }
        }
        return contestEntryFile;
    }
}
