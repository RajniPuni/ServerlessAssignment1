import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PartC {
    public static ArrayList arr = new ArrayList();
    public static void main(String[] args) {
        try{
            AmazonS3 s3client = setUpAWSConnection();
            addFileToBucket1(s3client);
            arr = getObjectFromBucket(s3client);
            String UserId = "testuser1";
            String Password = "testpwd";

            String encryptedPassword = encryptPassword(Password);
            insertUserData(UserId,encryptedPassword);
            String passwordOutput = getUserData(UserId);
            System.out.println("The encrypted password of user 'testuser1' is : ");
            System.out.println(passwordOutput);
        }
        catch (AmazonS3Exception ex){
            System.err.println(ex.getErrorMessage());
        }
    }

    public static void addFileToBucket1(AmazonS3 s3client){
        String bucketName = "ass1bucket1";
        s3client.putObject(
                bucketName,
                "lookup.txt",
                new File("F://Rajni/javaass1/src/main/java/Lookup5410.txt")
        );
    }
    public static ArrayList getObjectFromBucket(AmazonS3 s3client){
        List<S3Object> s3Objects = new ArrayList<>();
        ArrayList<String> arrList = new ArrayList<String>();
        String bucketName = "ass1bucket1";
        ListObjectsV2Result result = s3client.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        try {
            for (S3ObjectSummary os : objects) {
                String bucketKey = os.getKey();
                S3Object object;
                object = s3client.getObject(new GetObjectRequest(bucketName, bucketKey));
                InputStream objectData = object.getObjectContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(objectData));
                String line = null;

                while ((line = reader.readLine()) != null) {
                    arrList.add(line);
                    System.out.println(line);
                }

                System.out.println();
            }

        } catch (AmazonServiceException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return arrList;
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

    public static void insertUserData(String userid, String password){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost/assignment1?useSSL=false","root","xxxxxx");

            PreparedStatement stmt=con.prepareStatement("insert into user values(?,?)");
            stmt.setString(1,userid);//1 specifies the first parameter in the query
            stmt.setString(2,password);

            int i=stmt.executeUpdate();
            System.out.println(i+" records inserted");

            con.close();
        }catch(Exception e){ System.out.println(e);}
    }

    public static String getUserData(String userId){
        String password = "";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost/assignment1?useSSL=false","root","xxxxx");
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from user where userId = '" + userId + "'");
            while(rs.next()){
                password = rs.getString(2);
            }

            con.close();
        }catch(Exception e){ System.out.println(e);}
        return password;
    }

    public static String encryptPassword(String password) {
        String encryptedPassword = "";
        for (char ch : password.toCharArray()) {
            encryptedPassword = encryptedPassword + getEncryptedValue(ch);
        }
        return encryptedPassword;
    }
    public static String getEncryptedValue(Character passwordChar) {
        String encryptedValue="";


            for (int i = 0; i < arr.size(); i++) {
                String data = arr.get(i).toString();
                String key = String.valueOf(data.charAt(0));
                if(key.equals(String.valueOf(passwordChar).toLowerCase())){
                    String value = String.valueOf(data.charAt(2));
                    value = value + String.valueOf(data.charAt(3));
                    encryptedValue = value;
                }
            }

        return encryptedValue;
    }


}
