package com.phegon.phegonbank.aws;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class AwsS3Service {
 private final S3Client s3; private final String bucket;
 public AwsS3Service(String region,String bucket){this.s3=S3Client.builder().region(Region.of(region)).build();this.bucket=bucket;}
 public String upload(InputStream input,long size,String contentType,String originalName) throws IOException{
  String extension="";
  if(originalName!=null&&originalName.contains("."))extension=originalName.substring(originalName.lastIndexOf('.')).toLowerCase();
  String key="profile-pictures/"+UUID.randomUUID()+extension;
  s3.putObject(PutObjectRequest.builder().bucket(bucket).key(key).contentType(contentType).build(),RequestBody.fromInputStream(input,size));
  return s3.utilities().getUrl(GetUrlRequest.builder().bucket(bucket).key(key).build()).toExternalForm();
 }
 public void deleteByUrl(String url){
  if(url==null||url.isBlank())return;
  String marker="/profile-pictures/";
  int i=url.indexOf(marker); if(i<0)return;
  s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(url.substring(i+1)).build());
 }
}
