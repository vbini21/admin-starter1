package com.jcg.jsf.log4j;
 
import java.io.Serializable;
 
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
 
import org.apache.log4j.Logger;
 
@ManagedBean
@SessionScoped
public class Navigator implements Serializable {
 
    private static final long serialVersionUID = 1L;    
    private static Logger logger = Logger.getLogger(Navigator.class);
 
    private String name;
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public String validate() {
        String navResult = "";
        logger.info("Username is?= " + name);
        if (name.equalsIgnoreCase("jcg")) {
            navResult = "result";
        } else {
            name = "test user";
            navResult = "result";
        }
        return navResult;
    }
}

