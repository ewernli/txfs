/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ewe.txfs;
 
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.ws.WebServiceContext;

/**
 *
 * @author ewernli
 */
@Stateless
@WebService
public class TxfsWsTestImpl implements TxfsWsTest {

    @Resource(name = "txfs")
    TxfsConnectionFactory txfsConnFactory;

    @Resource
    EJBContext context;

    public void testCreateFileCommit(String path, String data) {

        TxfsConnection conn = null;

        try {
            conn = txfsConnFactory.getConnection();
            conn.createFile(path, new ByteArrayInputStream(data.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error in web service");

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e2) {
                }
            }
        }
    }

     public void testCreateFileRollback(String path, String data) {

        TxfsConnection conn = null;

        try {
            conn = txfsConnFactory.getConnection();
            conn.createFile(path, new ByteArrayInputStream(data.getBytes("UTF-8")));

            Thread.sleep( 10 * 1000 );
            context.setRollbackOnly();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error in web service");

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e2) {
                }
            }
        }
    }
}

