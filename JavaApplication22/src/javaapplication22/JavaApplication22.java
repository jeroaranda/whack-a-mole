/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication22;

import java.util.Random;

/**
 *
 * @author diego
 */
public class JavaApplication22 {

    /**
     * @param args the command line arguments
     */
    	private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
    
    public static void main(String[] args) {
        int res =  getRandomNumberInRange(1,12);
        System.out.println(res);
    }
    
}
