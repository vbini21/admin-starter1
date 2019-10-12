/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.model;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;

@Named
@SessionScoped
public class Polytechnic implements Serializable, Comparable<Polytechnic> {

    /**
     *
     */
    protected PreparedStatement ps;
    protected ResultSet rs;
    private static Logger logger = Logger.getLogger(Polytechnic.class);
    private static final long serialVersionUID = 1L;
    private String opno;
    private String names;    
    private String manager;
    private String phone;
    private String tr_male;
    private String tr_female;
	private String tr_material;
    private String benches;
    private String tables;
    private String chairs;
    private String tools;
    private String machines;
    private String date_created;
    private String created_by;
    

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public void init() {
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getTr_male() {
        return tr_male;
    }

    public void setTr_male(String tr_male) {
        this.tr_male = tr_male;
    }
   
    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public Polytechnic() {
        init();
    }

    public Polytechnic(String opno) {
        this.opno = opno;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    @Override
    public int compareTo(Polytechnic o) {
        return this.opno.compareTo(o.getOpno());
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }

    public Polytechnic opno(String opno) {
        this.opno = opno;
        return this;
    }

    public Polytechnic names(String names) {
        this.names = names;
        return this;
    }

    public Polytechnic manager(String manager) {
        this.manager = manager;
        return this;
    }

    public Polytechnic tr_male(String tr_male) {
        this.tr_male = tr_male;
        return this;
    }

    public Polytechnic date_created(String date_created) {
        this.date_created = date_created;
        return this;
    }

    public Polytechnic created_by(String created_by) {
        this.created_by = created_by;
        return this;
    }
    
    public String getTr_female() {
		return tr_female;
	}

	public void setTr_female(String tr_female) {
		this.tr_female = tr_female;
	}

	public String getTr_material() {
		return tr_material;
	}

	public void setTr_material(String tr_material) {
		this.tr_material = tr_material;
	}

	public String getBenches() {
		return benches;
	}

	public void setBenches(String benches) {
		this.benches = benches;
	}

	public String getTables() {
		return tables;
	}

	public void setTables(String tables) {
		this.tables = tables;
	}

	public String getChairs() {
		return chairs;
	}

	public void setChairs(String chairs) {
		this.chairs = chairs;
	}

	public String getTools() {
		return tools;
	}

	public void setTools(String tools) {
		this.tools = tools;
	}

	public String getMachines() {
		return machines;
	}

	public void setMachines(String machines) {
		this.machines = machines;
	}	
	
	public Polytechnic tr_female(String tr_female) {
        this.tr_female = tr_female;
        return this;
    }
	
	public Polytechnic tr_material(String tr_material) {
        this.tr_material = tr_material;
        return this;
    }
	
	public Polytechnic benches(String benches) {
        this.benches = benches;
        return this;
    }
	
	public Polytechnic tables(String tables) {
        this.tables = tables;
        return this;
    }
	
	public Polytechnic chairs(String chairs) {
        this.chairs = chairs;
        return this;
    }
	
	public Polytechnic tools(String tools) {
        this.tools = tools;
        return this;
    }
	
	public Polytechnic machines(String machines) {
        this.machines = machines;
        return this;
    }

	public Polytechnic phone(String phone) {
        this.phone = phone;
        return this;
    }
}
