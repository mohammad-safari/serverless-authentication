package ir.aut.ce.cloud.serverlessauthentication.objectStorage;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;

import jakarta.annotation.PostConstruct;

@Service
public class S3ObjectStorageService {

    @Value("${objectstorage.s3provider.storage-endpoint}")
    private String storageEndpoint;
    @Value("${objectstorage.s3provider.bucket-name}")
    private String bucketName;
    @Value("${objectstorage.s3provider.access-key}")
    private String accessKey;
    @Value("${objectstorage.s3provider.secret-key}")
    private String secretKey;

    private AmazonS3 amazonS3Client;

    @PostConstruct
    public void initService() {
        var credentials = new BasicAWSCredentials(accessKey, secretKey);
        var endpoint = new EndpointConfiguration(storageEndpoint, accessKey);
        var credentialsProvider = new AWSStaticCredentialsProvider(credentials);
        amazonS3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(endpoint)
                .withCredentials(credentialsProvider).build();
    }

    public InputStream getFileFromS3Bucket(String fileName) {
        var getObjectRequest = new GetObjectRequest(bucketName, fileName);
        return amazonS3Client.getObject(getObjectRequest).getObjectContent();
    }
}
