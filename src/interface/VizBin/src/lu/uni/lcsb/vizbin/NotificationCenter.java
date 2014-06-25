/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.lcsb.vizbin;

import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author Arash
 */
public class NotificationCenter {
 
    static ArrayList<NotificationListener> observers = new ArrayList<NotificationListener>();
    
    public static void addObserver(NotificationListener notifListener)
    {
        observers.add(notifListener);
    }
    
    public static void removeObserver(NotificationListener notifListener)
    {
        observers.remove(notifListener);
    }
    
    /**
     *
     */
    public static void notifyObservers()
    {
        for (NotificationListener nl: observers)
        {
            nl.notifyObserver();
        }
    };
    
}
