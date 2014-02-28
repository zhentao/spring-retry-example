package com.zhentao.retry;

import java.io.File;
import java.io.FileNotFoundException;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.ResourceUtils;


public class RetryRunner {
    public static void main(String[] args) throws FileNotFoundException {
//        AnnotationConfigApplicationContext context = null;
//        try {
//            context = new AnnotationConfigApplicationContext(SpringConfig.class);
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class)) {
            UploadClient client = context.getBean(UploadClient.class);
            File data = ResourceUtils.getFile("/file.xml");
            client.upload(data, data);
        }
    }
}
