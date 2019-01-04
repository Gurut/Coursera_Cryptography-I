/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meetinthemiddle;

import java.math.BigInteger;
import java.util.Hashtable;

/**
 *
 * @author Samet Tonyali
 * This code computes discrete log modulo a prime p. Let g be some element in Z_p^*
 * and suppose you are given h in Z_p^* such that h = g^x where 1<=x<=2^40. The 
 * goal is to find x.
 */
public class MeetInTheMiddle {
    
    public static void main (String args[]){
        BigInteger p = new BigInteger("13407807929942597099574024998205846127479365820592393377723561443721764030073546976801874298166903427690031858186486050853753882811946569946433649006084171");
        BigInteger g = new BigInteger("11717829880366207009516117596335367088558084999998952205599979459063929499736583746670572176471460312928594829675428279466566527115212748467589894601965568");
        BigInteger h = new BigInteger("3239475104050450443565264378728065788649097520952449527834792452971981976143292558073856937958553180532878928001494706097394108577585732452307673444020333");
        
        BigInteger B = new BigInteger("2");
        B = B.modPow(new BigInteger("20"), p);
        
        Hashtable<BigInteger, BigInteger> hovergtothexsub1 = new Hashtable<>();
        
        BigInteger x1 = new BigInteger("0");
        
        BigInteger gtothexsub1, hPrime;
        
        do{
            gtothexsub1 = g.modPow(x1, p);
            gtothexsub1 = gtothexsub1.modInverse(p);
            hPrime = h.multiply(gtothexsub1);
            hPrime = hPrime.mod(p);
            
            hovergtothexsub1.put(hPrime, x1);
            
            x1 = x1.add(BigInteger.ONE);
        }while(x1.compareTo(B) != 0);

        
        BigInteger gtotheB = g.modPow(B, p);
        BigInteger gtotheBtothexsub0;
        BigInteger x0 = new BigInteger("0");
        do{
            gtotheBtothexsub0 = gtotheB.modPow(x0, p);
            
            if(hovergtothexsub1.containsKey(gtotheBtothexsub0)){
                x1 = hovergtothexsub1.get(gtotheBtothexsub0);
                break;
            }
            
            x0 = x0.add(BigInteger.ONE);
        }while(x0.compareTo(B) != 0);
        
        BigInteger x = x0.multiply(B).add(x1).mod(p);
        
        System.out.println(x.toString());
    }
    
}
