/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ewe.txfs;

 
/**
 *
 * @author ewernli
 */
public interface TxfsWsTest {

    void testCreateFileCommit(String path, String data) ;

     void testCreateFileRollback(String path, String data) ;

}
