import com.amazonaws.*;
import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PartB {
    public static void main(String[] args) {
        try{
            AmazonS3 s3client = setUpAWSConnection();

            addFileToBucket1(s3client);
            disablePublicAccessBucket2(s3client);
            changeACLOptionToFullControl(s3client);
            moveObjectFromBucket1ToBucket2(s3client);
        }
        catch (AmazonS3Exception ex){
            System.err.println(ex.getErrorMessage());
        }
    }

    public static AmazonS3 setUpAWSConnection(){
        BasicSessionCredentials credentials = new BasicSessionCredentials(
                "",
                "",
                ""
        );
        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();

        return  s3client;
    }

    public static void addFileToBucket1(AmazonS3 s3client){
        String bucketName = "ass1bucket1";
        s3client.putObject(
                bucketName,
                "Rajni.txt",
                new File("F://Rajni/javaass1/src/main/java/Rajni.txt")
        );
    }

    public static void disablePublicAccessBucket2(AmazonS3 s3client){
        String bucketName = "ass1bucket2";

        s3client.setPublicAccessBlock(new SetPublicAccessBlockRequest()
                .withBucketName(bucketName)
                .withPublicAccessBlockConfiguration(new PublicAccessBlockConfiguration()
                        .withBlockPublicAcls(true)
                .withIgnorePublicAcls(true)
            .withBlockPublicPolicy(true)
            .withRestrictPublicBuckets(true)));
    }

    public static void changeACLOptionToFullControl(AmazonS3 s3client){
        String bucketName = "ass1bucket2";
        ArrayList<Grant> grantCollection = new ArrayList<Grant>();

        Grant grant1 = new Grant(new CanonicalGrantee(s3client.getS3AccountOwner().getId()), Permission.FullControl);
        grantCollection.add(grant1);

        AccessControlList bucketAcl = new AccessControlList();
        bucketAcl.setOwner(s3client.getBucketAcl(bucketName).getOwner());
        bucketAcl.grantAllPermissions(grantCollection.toArray(new Grant[0]));
        s3client.setBucketAcl(bucketName, bucketAcl);
    }

    public static void moveObjectFromBucket1ToBucket2(AmazonS3 s3client){
        ArrayList<String> filesToBeCopied = new ArrayList<String>();
        filesToBeCopied.add("Rajni.txt");

        String from_bucket_name = "ass1bucket1";
        String to_bucket = "ass1bucket2";
        ListObjectsV2Result result = s3client.listObjectsV2(from_bucket_name);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        try {
            for (S3ObjectSummary os : objects) {
                String bucketKey = os.getKey();
                if (filesToBeCopied.contains(bucketKey)) {
                    s3client.copyObject(from_bucket_name, bucketKey, to_bucket, bucketKey);
                }
            }

        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }
}
