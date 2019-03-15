/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package topos;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 *
 * @author jeroaranda
 */
public class estresador {
    public static void main(String[] args) throws UnknownHostException, IOException {
        int n=50;
        long[] tiempos=new long[n];
        String res="";
        ComputeClient cliente=new ComputeClient();
        for (int i = 0; i < n; i++) {
            tiempos[i]=cliente.estresa();
            res=res+" "+tiempos[i]+",";
        }
        whenWriteStringUsingBufferedWritter_thenCorrect(res);
    }
    public static void whenWriteStringUsingBufferedWritter_thenCorrect(String res) 
  throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter("resultados2.csv"));
    writer.write(res);
     
    writer.close();
}
}
