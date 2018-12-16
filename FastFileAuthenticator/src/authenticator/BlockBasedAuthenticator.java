/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package authenticator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Samet Tonyali
 * This file authenticator breaks a file into 1KB blocks and computes a SHA256 
 * hash for each of them. First, a hash is computed for the last block, and this
 * hash is appended to the second block to the last one. Then, the hash of this
 * augmented block is computed, and the hash is appended to the third block from
 * the end. This process continues from the last to the first block. In the end,
 * a final hash is computed for the first augmented block. Thus, a download 
 * manager does not have to download an entire file before checking its
 * authenticity.
 * 
 * 03c08f4ee0b576fe319338139c045c89c3e8e9409633bea29442e21425006ea8
 */
public class BlockBasedAuthenticator {
    public static void main(String args[]){
        final int BUFFERSIZE = 1024;
        
        File file = new File("target.mp4");
        
        long fileSize = file.length();
        int lastBlockSize = (int)(fileSize%BUFFERSIZE);
        int numBlocks = (int)(fileSize/BUFFERSIZE);
        long offset = fileSize-lastBlockSize;
        
        System.out.println("File length is " + fileSize + " bytes!");
        
        RandomAccessFile raf;
        
        try {
            raf = new RandomAccessFile(file, "r");
            
            byte[] buffer = new byte[lastBlockSize];
            
            raf.seek(offset);
            System.out.println((fileSize-lastBlockSize) + " bytes have been skipped!");
            raf.read(buffer, 0, lastBlockSize);
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = digest.digest(buffer);
            byte[] augmentedBlock = new byte[BUFFERSIZE+32];
            
            System.out.println("Size of the hash is " + messageDigest.length);
            
            for(int i=0; i<numBlocks; i++){
                offset -= BUFFERSIZE;
                raf.seek(offset);
                raf.read(augmentedBlock, 0, BUFFERSIZE);
                System.arraycopy(messageDigest, 0, augmentedBlock, BUFFERSIZE, 32);
                messageDigest = digest.digest(augmentedBlock);
            }
            
            System.out.println(bytesToHex(messageDigest));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BlockBasedAuthenticator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | NoSuchAlgorithmException ex) {
            Logger.getLogger(BlockBasedAuthenticator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
        }
        return hexString.toString();
    }
}
