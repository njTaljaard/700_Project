/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Processing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nico
 */
public final class Stats {
    
    public synchronized static void writeState(File file, String write) {
        
        try {            
            if (file.createNewFile()) {
                
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(write);
                }
                
            } else {
                System.out.println("Could not create file " + file.getCanonicalPath());
            }        
        } catch (IOException ex) {
            Logger.getLogger(Stats.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}
