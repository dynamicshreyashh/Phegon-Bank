package com.phegon.phegonbank.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {
 @Bean
 public AwsS3Service awsS3Service(@Value("${AWS_REGION:us-east-1}") String region,@Value("${AWS_S3_BUCKET:}") String bucket){
  return new AwsS3Service(region,bucket);
 }
}
