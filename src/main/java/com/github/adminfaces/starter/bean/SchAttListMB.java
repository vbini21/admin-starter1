package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.model.Att;
import com.github.adminfaces.starter.model.User;
import com.github.adminfaces.starter.service.SchAttService;

import org.apache.log4j.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Faces;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import static com.github.adminfaces.template.util.Assert.has;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by rmpestano on 12/02/17.
 */

@Named
@ViewScoped
public class SchAttListMB implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(SchAttListMB.class);
    @Inject
    SchAttService schAttService;
    private Att att;
    String opno;

    LazyDataModel<Att> patients;

    Filter<Att> filter = new Filter<>(new Att());

    List<Att> selectedPatients; //patients selected in checkbox column

    List<Att> filteredValue;// datatable filteredValue attribute (column filters)
    
	@PostConstruct
    public void initDataModel() {
        patients = new LazyDataModel<Att>() {
            @Override
            public List<Att> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                com.github.adminfaces.starter.infra.model.SortOrder order = null;
                if (sortOrder != null) {
                    order = sortOrder.equals(SortOrder.ASCENDING) ? com.github.adminfaces.starter.infra.model.SortOrder.ASCENDING
                            : sortOrder.equals(SortOrder.DESCENDING) ? com.github.adminfaces.starter.infra.model.SortOrder.DESCENDING
                            : com.github.adminfaces.starter.infra.model.SortOrder.UNSORTED;
                }
                filter.setFirst(first).setPageSize(pageSize)
                        .setSortField(sortField).setSortOrder(order)
                        .setParams(filters);
                List<Att> list = schAttService.paginate(filter);
                setRowCount((int) schAttService.count(filter));
                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public Att getRowData(String key) {
                return schAttService.findByOpno(key);
            }
        };
    }

    public void clear() {
        filter = new Filter<Att>(new Att());
    }

    public List<String> completeModelOpno(String query) {
        List<String> result = schAttService.getOpno(query);
        return result;
    }

    public List<String> completeModel(String query) {
        List<String> result = schAttService.getNames(query);
        return result;
    }

   
    public List<Att> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<Att> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public LazyDataModel<Att> getPatients() {
        return patients;
    }

    public void setPatients(LazyDataModel<Att> patients) {
        this.patients = patients;
    }

    public Filter<Att> getFilter() {
        return filter;
    }

    public void setFilter(Filter<Att> filter) {
        this.filter = filter;
    }

    public List<Att> getSelectedPatients() {
        return selectedPatients;
    }

    public void setSelectedPatients(List<Att> selectedPatients) {
        this.selectedPatients = selectedPatients;
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }
    
    public void onChecked(String opno) {
    	logger.error("opno"+opno);	
    	logger.error("Constructor executed 1");			
    	att = schAttService.findByOpno(opno);
    	logger.error("Constructor executed 2"+att.getNames());
    	schAttService.insert(att);
    }
    
}
