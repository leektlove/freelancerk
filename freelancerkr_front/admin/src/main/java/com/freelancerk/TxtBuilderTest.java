package com.freelancerk;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;



public class TxtBuilderTest {

	public static void main(String[] args) {

		  Path directoryPath = Paths.get("C:\\applyList");      
		  try {            // 디렉토리 생성           
			  Files.createDirectory(directoryPath);      
			  System.out.println(directoryPath + " 디렉토리가 생성되었습니다.");    
		  } catch (FileAlreadyExistsException e) {         
			  System.out.println("디렉토리가 이미 존재합니다");        
		  } catch (NoSuchFileException e) {          
			  System.out.println("디렉토리 경로가 존재하지 않습니다");    
		  }catch (IOException e) {           
			  e.printStackTrace();     
		  }
	

	}

}
