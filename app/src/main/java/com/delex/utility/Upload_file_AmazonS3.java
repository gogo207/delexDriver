package com.delex.utility;

import android.content.Context;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;

import java.io.File;

/**
 * Created by shobhit on 21/4/16.
 */


public class Upload_file_AmazonS3 {
    private static Upload_file_AmazonS3 uploadAmazonS3;
    private CognitoCachingCredentialsProvider credentialsProvider = null;
    private AmazonS3Client s3Client = null;
    private TransferUtility transferUtility = null;

    /**
     * Creating single tone object by defining private.
     * <p>
     * At the time of creating
     * </P>
     */
    private Upload_file_AmazonS3(Context context, String canito_pool_id) {
        /**
         * Creating the object of the getCredentialProvider object. */
        credentialsProvider = getCredentialProvider(context, canito_pool_id);
        /**
         * Creating the object  of the s3Client */
        s3Client = getS3Client(credentialsProvider);

        /**
         * Creating the object of the TransferUtility of the Amazone.*/
        transferUtility = getTransferUtility(context, s3Client);

    }

    public static Upload_file_AmazonS3 getInstance(Context context, String canito_pool_id) {
        if (uploadAmazonS3 == null) {
            uploadAmazonS3 = new Upload_file_AmazonS3(context, canito_pool_id);
            return uploadAmazonS3;
        } else {
            return uploadAmazonS3;
        }

    }

    /**
     * This method is used to upload the image on AWS.
     *
     * @param bucket_name , contains the bucket name.
     * @param key         , contains the folder structure with file name.
     * @param file,       contains the actual file.
     * @param callBack,   provides the instance of user defined callback.
     */
    public void Upload_data(final String bucket_name, final String key, final File file, final Upload_CallBack callBack) {
        if (transferUtility != null && file != null) {
            TransferObserver observer = transferUtility.upload(bucket_name, key, file);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state.equals(TransferState.COMPLETED)) {
                        callBack.sucess("https://s3.amazonaws.com/" + bucket_name + "/" + key);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    //int percentage = (int) (bytesCurrent/bytesTotal * 100);
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.e("Error", id + ":" + ex.toString());
                    callBack.error("Unable to upload file . Check your internet connection and Retry!");
                }
            });
        } else {
            callBack.error("Amamzones3 is not intialize or File is empty !");
        }
    }

    public void Upload_data(final String type, final String bucket_name, final String key, final File file, final Upload_CallBack callBack){
        if (transferUtility != null && file != null) {
            TransferObserver observer = transferUtility.upload(bucket_name, key, file);
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state.equals(TransferState.COMPLETED)) {
                        callBack.sucess(VariableConstant.AMAZON_BASE_URL + bucket_name + "/" + key,type);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    //int percentage = (int) (bytesCurrent/bytesTotal * 100);
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.e("Error", id + ":" + ex.toString());
                    callBack.error(type);
                }
            });
        } else {
            callBack.error("Amamzones3 is not intialize or File is empty !");
        }
    }
    /**
     * <h3>Upload_data</h3>
     * <p>
     * Method is use to download file from amazone.
     * </P>
     *
     * @param bukkate_name      The name of the bucket containing the object to download.
     * @param keyname           The key under which the object to download is stored.
     * @param StorageFile       The file to download the object's data to.
     * @param download_callBack return sucess and failure of the amazone.
     */

    public void download_File(String bukkate_name, String keyname, File StorageFile, final Download_CallBack download_callBack) {
        TransferObserver transferObserver = transferUtility.download(bukkate_name, keyname, StorageFile);
        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.equals(TransferState.COMPLETED)) {
                    download_callBack.sucess("Sucess !");

                } else if (state.equals(TransferState.CANCELED)) {
                    download_callBack.error("Canceled !");
                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent / bytesTotal * 100);
            }

            @Override
            public void onError(int id, Exception ex) {
                download_callBack.error(ex.toString());
            }
        });
    }

    /**
     * <h2>delete_Item</h2>
     * <P>Deleting the item from aws.</P>
     *
     * @param bucketName Aws folder name
     * @param keyName    file name in the aws folder.
     */
    public void delete_Item(String bucketName, String keyName) {
        try {
            if (s3Client != null)
                s3Client.deleteObject(new DeleteObjectRequest(bucketName, keyName));
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

    /**
     * This method is used to get the CredentialProvider and we provide only context as a parameter.
     *
     * @param context Here, we are getting the context from calling Activity.
     */
    private CognitoCachingCredentialsProvider getCredentialProvider(Context context, String pool_id) {
        if (credentialsProvider == null) {
            credentialsProvider = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(),
                    pool_id, // Identity Pool ID
                    Regions.US_EAST_1 // Region
            );
        }
        return credentialsProvider;
    }

    /**
     * This method is used to get the AmazonS3 Client
     * and we provide only context as a parameter.
     * and from here we are calling getCredentialProvider() function.
     */
    private AmazonS3Client getS3Client(CognitoCachingCredentialsProvider cognitoCachingCredentialsProvider) {
        if (s3Client == null) {
            s3Client = new AmazonS3Client(cognitoCachingCredentialsProvider);
        }
        return s3Client;
    }

    /**
     * This method is used to get the Transfer Utility
     * and we provide only context as a parameter.
     * and from here we are, calling getS3Client() function.
     *
     * @param context Here, we are getting the context from calling Activity.
     */
    private TransferUtility getTransferUtility(Context context, AmazonS3Client amazonS3Client) {
        if (transferUtility == null) {
            transferUtility = new TransferUtility(amazonS3Client, context.getApplicationContext());
        }
        return transferUtility;
    }


    /**
     * Interface for the sucess callback fro the Amazon uploading .
     */
    public interface Upload_CallBack {
        /**
         * Method for sucess .
         *
         * @param sucess it is true on sucess and false for falure.
         */
        void sucess(String sucess);
        void sucess(String url,String type);

        /**
         * Method for falure.
         *
         * @param errormsg contains the error message.
         */
        void error(String errormsg);

    }

    /**
     * Interface for the sucess callback for downloading file. .
     */
    public interface Download_CallBack {
        /**
         * Method for sucess .
         *
         * @param url it is true on sucess and false for falure.
         */
        void sucess(String url);


        /**
         * Method for falure.
         *
         * @param errormsg contains the error message.
         */
        void error(String errormsg);

    }
}
