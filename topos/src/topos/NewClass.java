/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package topos;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author diego
 */
public class NewClass {
    public static void main(String[] args) throws UnknownHostException {
        String res = InetAddress.getLocalHost().getHostAddress();
        System.out.println(res);
    }
}
